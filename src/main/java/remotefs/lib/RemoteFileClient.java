package remotefs.lib;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import remotefs.*; // stubs/mensagens geradas do proto
import com.google.protobuf.ByteString;

import remotefs.util.ConfigLoader;

import java.util.*;

// Biblioteca cliente. Fornece a API do sistema de arquivos remoto com cache.
// Usa gRPC para comunicação com o servidor e mantém cache local por blocos.
public class RemoteFileClient implements AutoCloseable {

    // ---------- Conexão gRPC ----------
    private final ManagedChannel channel;
    private final RemoteFileSystemGrpc.RemoteFileSystemBlockingStub stub;

    // ---------- Cache ----------
    private final int blockSize;
    private final int capacity;

    private static final class BlockKey {
        final int fd, idx;
        BlockKey(int fd, int idx) { this.fd = fd; this.idx = idx; }
        @Override public boolean equals(Object o){ if(!(o instanceof BlockKey b)) return false; return b.fd==fd && b.idx==idx; }
        @Override public int hashCode(){ return Objects.hash(fd, idx); }
    }
    private static final class CacheEntry {
        final byte[] data;
        final int versao;
        CacheEntry(byte[] data, int versao) { this.data = data; this.versao = versao; }
    }

    // LRU por acesso
    private final Map<BlockKey, CacheEntry> cache;
    // versão local por descritor (última vista)
    private final Map<Integer, Integer> versaoPorFd = new HashMap<>();

    // Métricas simples (opcional p/ relatório)
    private long hits = 0, misses = 0;

    // ---------- Construtores ----------

    // Constrói lendo host/port/block_size/cache_max_entries de config/config.txt 
    public RemoteFileClient() {
        this(new ConfigLoader("config/config.txt"));
    }

    public RemoteFileClient(ConfigLoader cfg) {
        this(cfg.getHost(), cfg.getPort(), cfg.getBlockSize(), cfg.getCacheMaxEntries());
    }

    public RemoteFileClient(String host, int port, int blockSize, int capacity) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.stub = RemoteFileSystemGrpc.newBlockingStub(channel);
        this.blockSize = Math.max(1, blockSize);
        this.capacity = Math.max(1, capacity);
        this.cache = new LinkedHashMap<>(16, 0.75f, true) {
            @Override protected boolean removeEldestEntry(Map.Entry<BlockKey, CacheEntry> eldest) {
                return size() > RemoteFileClient.this.capacity;
            }
        };
        System.out.println("[Client] Conectado a " + host + ":" + port + " | blockSize=" + this.blockSize + " | cap=" + this.capacity);
    }

    // ---------- API pública ----------

    // Abre/cria arquivo no servidor e retorna seu descritor (fd). 
    public int abre(String nomeArquivo) {
        OpenResponse res = stub.abre(OpenRequest.newBuilder().setNomeArquivo(nomeArquivo).build());
        if (res.getCodigoErro() != 0) throw new RuntimeException("abre falhou (codigo=" + res.getCodigoErro() + ")");
        int fd = res.getDescritor();
        versaoPorFd.put(fd, 0); // desconhecida inicialmente
        invalidateFd(fd);       // por segurança
        return fd;
    }

    // Lê 'tamanho' bytes a partir de 'pos' do arquivo 'fd'. Usa cache por blocos. 
    public byte[] le(int fd, int pos, int tamanho) {
        if (tamanho <= 0) return new byte[0];

        int ini = pos / blockSize;
        int fim = (pos + tamanho - 1) / blockSize;

        byte[] out = new byte[tamanho];
        int outOff = 0;
        int verLocal = versaoPorFd.getOrDefault(fd, 0);

        for (int b = ini; b <= fim; b++) {
            int blockStart = b * blockSize;
            int wantStart = Math.max(pos, blockStart);
            int wantEnd   = Math.min(blockStart + blockSize, pos + tamanho);
            int wantLen   = Math.max(0, wantEnd - wantStart);
            if (wantLen == 0) continue;

            BlockKey key = new BlockKey(fd, b);
            CacheEntry entry = cache.get(key);

            byte[] blockData;
            boolean hit = (entry != null && entry.versao == verLocal);

            if (hit) {
                blockData = entry.data;
                hits++;
            } else {
                misses++;
                // Busca do servidor alinhada ao bloco
                ReadResponse resp = stub.le(ReadRequest.newBuilder()
                        .setDescritor(fd)
                        .setPosicao(blockStart)
                        .setTamanho(blockSize)
                        .build());

                if (resp.getCodigoErro() != 0) throw new RuntimeException("le falhou (codigo=" + resp.getCodigoErro() + ")");

                int verSrv = resp.getVersao();
                if (verSrv != verLocal) {
                    // invalida tudo desse fd e atualiza versão local
                    invalidateFd(fd);
                    versaoPorFd.put(fd, verSrv);
                    verLocal = verSrv;
                }

                byte[] bytes = resp.getConteudo().toByteArray();
                if (bytes.length < blockSize) {
                    byte[] padded = new byte[blockSize];
                    System.arraycopy(bytes, 0, padded, 0, bytes.length);
                    bytes = padded;
                }
                blockData = bytes;
                cache.put(key, new CacheEntry(blockData, verLocal));
            }

            int srcOff = wantStart - blockStart;
            System.arraycopy(blockData, srcOff, out, outOff, wantLen);
            outOff += wantLen;
        }

        return out;
    }

    // Escreve 'dados' a partir de 'pos' no arquivo 'fd'. Invalida cache do fd e ajusta versão. */
    public int escreve(int fd, int pos, byte[] dados) {
        WriteResponse res = stub.escreve(WriteRequest.newBuilder()
                .setDescritor(fd)
                .setPosicao(pos)
                .setConteudo(ByteString.copyFrom(dados))
                .build());
        if (res.getCodigoErro() != 0) throw new RuntimeException("escreve falhou (codigo=" + res.getCodigoErro() + ")");

        int novaVer = res.getVersao();
        versaoPorFd.put(fd, novaVer);
        invalidateFd(fd); // write-through + invalidate
        return res.getBytesEscritos();
    }

    // Fecha descritor no servidor e limpa cache local desse arquivo. 
    public void fecha(int fd) {
        CloseResponse res = stub.fecha(CloseRequest.newBuilder().setDescritor(fd).build());
        if (res.getCodigoErro() != 0) throw new RuntimeException("fecha falhou (codigo=" + res.getCodigoErro() + ")");
        invalidateFd(fd);
        versaoPorFd.remove(fd);
    }

    // Fecha o canal gRPC. 
    public void shutdown() {
        channel.shutdown();
    }

    @Override
    public void close() {
        shutdown();
    }

    // ---------- Utilidades ----------

    // Remove todas as entradas de cache relacionadas a um fd. 
    private void invalidateFd(int fd) {
        cache.keySet().removeIf(k -> k.fd == fd);
    }

    // Retorna métricas de cache (para demo/relatório). 
    public long getCacheHits()   { return hits; }
    public long getCacheMisses() { return misses; }
}
