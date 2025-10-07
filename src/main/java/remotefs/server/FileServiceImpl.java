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
            System.out.println(
                    "[Server] Leitura de fd=" + request.getDescritor() + ", bytes=" + n + ", versao=" + versao);
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
            int fd = request.getDescritor();
            int expected = request.getExpectedVersao(); // pode vir 0 se cliente não souber
            int versaoAtual = fileManager.getVersionByFd(fd);

            // OCC: se o cliente informou versão esperada e está diferente, retorna conflito
            // (409)
            if (expected != 0 && expected != versaoAtual) {
                WriteResponse resp = WriteResponse.newBuilder()
                        .setCodigoErro(409) // conflito
                        .setBytesEscritos(0)
                        .setVersao(versaoAtual) // diga ao cliente qual é a versão atual
                        .build();
                responseObserver.onNext(resp);
                responseObserver.onCompleted();
                System.out
                        .println("[Server] CONFLITO OCC fd=" + fd + " expected=" + expected + " atual=" + versaoAtual);
                return;
            }

            // Sem conflito -> aplica escrita
            byte[] data = request.getConteudo().toByteArray();
            fileManager.write(fd, request.getPosicao(), data);
            int novaVersao = fileManager.bumpVersionByFd(fd);

            WriteResponse resp = WriteResponse.newBuilder()
                    .setCodigoErro(0)
                    .setBytesEscritos(data.length)
                    .setVersao(novaVersao)
                    .build();
            responseObserver.onNext(resp);
            responseObserver.onCompleted();
            System.out.println("[Server] Escrita OK fd=" + fd + " bytes=" + data.length + " novaVersao=" + novaVersao);

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

    @Override
    public void ls(LsRequest request, StreamObserver<LsResponse> responseObserver) {
        try {
            String conteudo = fileManager.listFiles();

            LsResponse resp = LsResponse.newBuilder()
                    .setCodigoErro(0)
                    .setConteudo(conteudo)
                    .build();

            responseObserver.onNext(resp);
            responseObserver.onCompleted();

            System.out.println("[Server] Enviando lista de arquivos.");
        } catch (IOException e) {
            LsResponse resp = LsResponse.newBuilder()
                    .setCodigoErro(-1)
                    .setConteudo("Erro ao listar arquivos: " + e.getMessage())
                    .build();

            responseObserver.onNext(resp);
            responseObserver.onCompleted();
        }
    }
}
