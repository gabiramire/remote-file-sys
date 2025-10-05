package remotefs.server;

import io.grpc.stub.StreamObserver;
import remotefs.*;

import java.io.IOException;

// Implementação dos serviços gRPC (Open/Read/Write/Close) a partir do .proto
public class FileServiceImpl extends RemoteFileSystemGrpc.RemoteFileSystemImplBase {

    private final FileManager fileManager;

    public FileServiceImpl(FileManager fm) {
        this.fileManager = fm;
    }

    @Override
    public void abre(OpenRequest request, StreamObserver<OpenResponse> responseObserver) {
        try {
            int fd = fileManager.open(request.getNomeArquivo());
            OpenResponse resp = OpenResponse.newBuilder()
                    .setCodigoErro(0)
                    .setDescritor(fd)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            System.out.println("[Server] Arquivo aberto: " + request.getNomeArquivo() + " (fd=" + fd + ")");
        } catch (IOException | IllegalArgumentException e) {
            OpenResponse resp = OpenResponse.newBuilder()
                    .setCodigoErro(-1)
                    .setDescritor(-1)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void le(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
        try {
            byte[] buffer = new byte[request.getTamanho()];
            int n = fileManager.read(request.getDescritor(), request.getPosicao(), buffer);
            int versao = fileManager.getVersionByFd(request.getDescritor());

            ReadResponse resp = ReadResponse.newBuilder()
                    .setCodigoErro(0)
                    .setConteudo(com.google.protobuf.ByteString.copyFrom(buffer, 0, n))
                    .setVersao(versao)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            System.out.println("[Server] Leitura de fd=" + request.getDescritor() + ", bytes=" + n + ", versao=" + versao);
        } catch (IOException | IllegalArgumentException e) {
            ReadResponse resp = ReadResponse.newBuilder()
                    .setCodigoErro(-1)
                    .setConteudo(com.google.protobuf.ByteString.EMPTY)
                    .setVersao(0)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void escreve(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
        try {
            byte[] data = request.getConteudo().toByteArray();
            fileManager.write(request.getDescritor(), request.getPosicao(), data);
            int versao = fileManager.bumpVersionByFd(request.getDescritor());

            WriteResponse resp = WriteResponse.newBuilder()
                    .setCodigoErro(0)
                    .setBytesEscritos(data.length)
                    .setVersao(versao)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            System.out.println("[Server] Escrita em fd=" + request.getDescritor() + ", bytes=" + data.length + ", nova versao=" + versao);
        } catch (IOException | IllegalArgumentException e) {
            WriteResponse resp = WriteResponse.newBuilder()
                    .setCodigoErro(-1)
                    .setBytesEscritos(0)
                    .setVersao(0)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void fecha(CloseRequest request, StreamObserver<CloseResponse> responseObserver) {
        try {
            fileManager.close(request.getDescritor());
            CloseResponse resp = CloseResponse.newBuilder()
                    .setCodigoErro(0)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            System.out.println("[Server] fd fechado: " + request.getDescritor());
        } catch (IOException | IllegalArgumentException e) {
            CloseResponse resp = CloseResponse.newBuilder()
                    .setCodigoErro(-1)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}
