package remotefs.server;

import io.grpc.stub.StreamObserver;
import remotefs.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// Implementação dos serviços gRPC (Open/Read/Write/Close) a partir do .proto
public class FileServiceImpl extends RemoteFileSystemGrpc.RemoteFileSystemImplBase {
  private final FileManager fileManager;

  // recebe String (ex. vindo do config) e converte para Path
  public FileServiceImpl(String rootDir) throws IOException {
      this(Paths.get(rootDir));
  }

  // recebe Path diretamente
  public FileServiceImpl(Path rootDir) throws IOException {
      this.fileManager = new FileManager(rootDir);
  }

  @Override
  public void abre(OpenRequest req, StreamObserver<OpenResponse> obs) {
    try {
      int fd = fileManager.open(req.getNomeArquivo());
      obs.onNext(OpenResponse.newBuilder().setCodigoErro(0).setDescritor(fd).build());
    } catch (Exception e) {
      obs.onNext(OpenResponse.newBuilder().setCodigoErro(-1).build());
    }
    obs.onCompleted();
  }

  @Override
  public void le(ReadRequest req, StreamObserver<ReadResponse> obs) {
    try {
      byte[] buf = new byte[Math.max(0, req.getTamanho())];
      int n = fileManager.read(req.getDescritor(), req.getPosicao(), buf);
      obs.onNext(ReadResponse.newBuilder()
          .setCodigoErro(0)
          .setConteudo(com.google.protobuf.ByteString.copyFrom(buf, 0, n))
          .build());
    } catch (Exception e) {
      obs.onNext(ReadResponse.newBuilder().setCodigoErro(-1).build());
    }
    obs.onCompleted();
  }

  @Override
  public void escreve(WriteRequest req, StreamObserver<WriteResponse> obs) {
    try {
      int newVer = fileManager.write(req.getDescritor(), req.getPosicao(), req.getConteudo().toByteArray());
      // Log opcional:
      System.out.println("Mensagem recebida do cliente: \"" +
          req.getConteudo().toStringUtf8() + "\" (versao=" + newVer + ")");
      obs.onNext(WriteResponse.newBuilder()
          .setCodigoErro(0)
          .setBytesEscritos(req.getConteudo().size())
          .build());
    } catch (Exception e) {
      obs.onNext(WriteResponse.newBuilder().setCodigoErro(-1).build());
    }
    obs.onCompleted();
  }

  @Override
  public void fecha(CloseRequest req, StreamObserver<CloseResponse> obs) {
    try {
      fileManager.close(req.getDescritor());
      obs.onNext(CloseResponse.newBuilder().setCodigoErro(0).build());
    } catch (Exception e) {
      obs.onNext(CloseResponse.newBuilder().setCodigoErro(-1).build());
    }
    obs.onCompleted();
  }
}
