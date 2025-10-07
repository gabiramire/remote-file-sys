package remotefs.server;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Responsável por gerenciar arquivos no servidor localmente.
public class FileManager {

    private final Path rootDir;
    private final Path fileListPath;
    private final AtomicInteger nextFd = new AtomicInteger(100);
    private final Map<Integer, RandomAccessFile> fdToRaf = new ConcurrentHashMap<>();
    private final Map<Integer, Path> fdToPath = new ConcurrentHashMap<>();
    private final Map<Path, AtomicInteger> versionByPath = new ConcurrentHashMap<>();

    public FileManager(Path rootDir) throws IOException {
        this.rootDir = rootDir.toAbsolutePath().normalize();
        Files.createDirectories(this.rootDir);
        this.fileListPath = rootDir.resolve("file_list");
        if (!Files.exists(fileListPath)) {
            Files.createFile(fileListPath);
        }
    }

    // Garante que o path solicitado fique dentro de rootDir.
    private Path resolveSafe(String relative) {
        Path p = rootDir.resolve(relative).normalize();
        if (!p.startsWith(rootDir)) {
            throw new IllegalArgumentException("Path traversal detectado: " + relative);
        }
        return p;
    }

    // Abre (ou cria) um arquivo e retorna um descritor inteiro.
    public int open(String relativePath) throws IOException {
        Path p = resolveSafe(relativePath);
        if (p.getParent() != null) {
            Files.createDirectories(p.getParent());
        }
        boolean isNewFile = !Files.exists(p);
        RandomAccessFile raf = new RandomAccessFile(p.toFile(), "rw");
        int fd = nextFd.getAndIncrement();
        fdToRaf.put(fd, raf);
        fdToPath.put(fd, p);
        versionByPath.putIfAbsent(p, new AtomicInteger(0));

        if (isNewFile) {
            appendToFileList(relativePath);
        }

        return fd;
    }

    // Adiciona o nome de um arquivo ao file_list
    private synchronized void appendToFileList(String relativePath) throws IOException {
        try (RandomAccessFile listFile = new RandomAccessFile(fileListPath.toFile(), "rw")) {
            listFile.seek(listFile.length());
            listFile.writeBytes(relativePath + System.lineSeparator());
        }
    }

    // Lê até out.length bytes a partir de pos. Retorna quantos bytes foram lidos
    public int read(int fd, int pos, byte[] out) throws IOException {
        RandomAccessFile raf = fdToRaf.get(fd);
        if (raf == null)
            throw new IllegalArgumentException("fd inválido: " + fd);
        raf.seek(Integer.toUnsignedLong(Math.max(0, pos)));
        int n = raf.read(out, 0, out.length);
        return Math.max(n, 0);
    }

    /** Escreve os bytes em 'data' a partir de pos. */
    public void write(int fd, int pos, byte[] data) throws IOException {
        RandomAccessFile raf = fdToRaf.get(fd);
        if (raf == null)
            throw new IllegalArgumentException("fd inválido: " + fd);
        raf.seek(Integer.toUnsignedLong(Math.max(0, pos)));
        raf.write(data);
    }

    // Fecha o descritor.
    public void close(int fd) throws IOException {
        RandomAccessFile raf = fdToRaf.remove(fd);
        if (raf != null)
            raf.close();
        fdToPath.remove(fd);
    }

    // Retorna o conteúdo do file_list (para o comando ls)
    public synchronized String listFiles() throws IOException {
        if (!Files.exists(fileListPath))
            return "";

        // Lê todas as linhas e junta com espaço
        return String.join(" ", Files.readAllLines(fileListPath)).trim();
    }

    // Retorna a versão atual do arquivo associado ao fd.
    public int getVersionByFd(int fd) {
        Path p = fdToPath.get(fd);
        if (p == null)
            return 0;
        return versionByPath.getOrDefault(p, new AtomicInteger(0)).get();
    }

    // Incrementa e retorna a nova versão do arquivo associado ao fd (após escrita).
    public int bumpVersionByFd(int fd) {
        Path p = fdToPath.get(fd);
        if (p == null)
            return 0;
        return versionByPath.computeIfAbsent(p, __ -> new AtomicInteger(0)).incrementAndGet();
    }

    // Caminho absoluto do diretório raiz (para logs).
    public Path getRootDir() {
        return rootDir;
    }
}
