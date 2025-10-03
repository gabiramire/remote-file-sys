package remotefs.lib;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import remotefs.RemoteFileSystemGrpc;
import remotefs.RemoteFileSystemOuterClass.*;

import remotefs.OpenRequest;
import remotefs.OpenResponse;
import remotefs.ReadRequest;
import remotefs.ReadResponse;
import remotefs.WriteRequest;
import remotefs.WriteResponse;
import remotefs.CloseRequest;
import remotefs.CloseResponse;

// Biblioteca cliente. Fornece a API do sistema de arquivos remoto
public class RemoteFileClient {
    private final ManagedChannel channel;
    private final RemoteFileSystemGrpc.RemoteFileSystemBlockingStub stub;

    public RemoteFileClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
                                            .usePlaintext()
                                            .build();
        this.stub = RemoteFileSystemGrpc.newBlockingStub(channel);
    }

    public int abre(String nomeArquivo) {
        OpenRequest req = OpenRequest.newBuilder().setNomeArquivo(nomeArquivo).build();
        OpenResponse res = stub.abre(req);
        return res.getDescritor();
    }

    public byte[] le(int descritor, int pos, int tamanho) {
        ReadRequest req = ReadRequest.newBuilder()
                                     .setDescritor(descritor)
                                     .setPosicao(pos)
                                     .setTamanho(tamanho)
                                     .build();
        ReadResponse res = stub.le(req);
        return res.getConteudo().toByteArray();
    }

    public int escreve(int descritor, int pos, byte[] dados) {
        WriteRequest req = WriteRequest.newBuilder()
                                       .setDescritor(descritor)
                                       .setPosicao(pos)
                                       .setConteudo(com.google.protobuf.ByteString.copyFrom(dados))
                                       .build();
        WriteResponse res = stub.escreve(req);
        return res.getBytesEscritos();
    }

    public void fecha(int descritor) {
        CloseRequest req = CloseRequest.newBuilder().setDescritor(descritor).build();
        stub.fecha(req);
    }

    public void shutdown() {
        channel.shutdown();
    }
}
