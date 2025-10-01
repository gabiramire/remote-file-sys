package remote;

import io.grpc.stub.StreamObserver;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FileServiceImpl extends RemoteFileServiceGrpc.RemoteFileServiceImplBase {
  private final AtomicInteger nextFd = new AtomicInteger(100);
  private final Map<Integer, FileChannel> fdToChannel = new ConcurrentHashMap<>();
  private final Map<Integer, String>       fdToPath    = new ConcurrentHashMap<>();
  private final Map<String, AtomicInteger> fileVersion = new ConcurrentHashMap<>();

  @Override
  public void open(OpenRequest req, StreamObserver<OpenResponse> obs) {
    try {
      String path = req.getNomeArquivo(); // note: campo vira getNomeArquivo() ou getNome_arquivo() conforme plugin; ajuste se necessário
      path = req.getNomeArquivo(); // <- use esta se o método gerado for este (o mais comum)

      FileChannel ch = FileChannel.open(
          Path.of(path),
          StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);

      int fd = nextFd.getAndIncrement();
      fdToChannel.put(fd, ch);
      fdToPath.put(fd, path);
      fileVersion.putIfAbsent(path, new AtomicInteger(0));

      OpenResponse resp = OpenResponse.newBuilder()
          .setCodigoErro(0)
          .setDescritor(fd)
          .setVersao(fileVersion.get(path).get())
          .build();
      obs.onNext(resp);
      obs.onCompleted();
    } catch (Exception e) {
      obs.onNext(OpenResponse.newBuilder().setCodigoErro(-1).build());
      obs.onCompleted();
    }
  }

  @Override
  public void read(ReadRequest req, StreamObserver<ReadResponse> obs) {
    FileChannel ch = fdToChannel.get(req.getDescritor());
    if (ch == null) {
      obs.onNext(ReadResponse.newBuilder().setCodigoErro(-2).build());
      obs.onCompleted();
      return;
    }
    try {
      int tamanho = Math.max(0, req.getTamanho());
      ByteBuffer buf = ByteBuffer.allocate(tamanho);
      int n = ch.read(buf, req.getPosicao());
      if (n < 0) n = 0;
      buf.flip();

      String path = fdToPath.get(req.getDescritor());
      int ver = fileVersion.getOrDefault(path, new AtomicInteger(0)).get();

      ReadResponse resp = ReadResponse.newBuilder()
          .setCodigoErro(0)
          .setBytesLidos(n)
          .setDados(com.google.protobuf.ByteString.copyFrom(buf))
          .setVersao(ver)
          .build();
      obs.onNext(resp);
      obs.onCompleted();
    } catch (IOException e) {
      obs.onNext(ReadResponse.newBuilder().setCodigoErro(-1).build());
      obs.onCompleted();
    }
  }

  @Override
  public void write(WriteRequest req, StreamObserver<WriteResponse> obs) {
    FileChannel ch = fdToChannel.get(req.getDescritor());
    if (ch == null) {
      obs.onNext(WriteResponse.newBuilder().setCodigoErro(-2).build());
      obs.onCompleted();
      return;
    }
    try {
      byte[] data = req.getDados().toByteArray();
      int written = ch.write(ByteBuffer.wrap(data), req.getPosicao());

      String path = fdToPath.get(req.getDescritor());
      int newVer = fileVersion.computeIfAbsent(path, p -> new AtomicInteger(0)).incrementAndGet();

      WriteResponse resp = WriteResponse.newBuilder()
          .setCodigoErro(0)
          .setBytesEscritos(written)
          .setVersao(newVer)
          .build();
      obs.onNext(resp);
      obs.onCompleted();
    } catch (IOException e) {
      obs.onNext(WriteResponse.newBuilder().setCodigoErro(-1).build());
      obs.onCompleted();
    }
  }

  @Override
  public void close(CloseRequest req, StreamObserver<CloseResponse> obs) {
    FileChannel ch = fdToChannel.remove(req.getDescritor());
    if (ch == null) {
      obs.onNext(CloseResponse.newBuilder().setCodigoErro(-2).build());
      obs.onCompleted();
      return;
    }
    try {
      ch.close();
      fdToPath.remove(req.getDescritor());
      obs.onNext(CloseResponse.newBuilder().setCodigoErro(0).build());
      obs.onCompleted();
    } catch (IOException e) {
      obs.onNext(CloseResponse.newBuilder().setCodigoErro(-1).build());
      obs.onCompleted();
    }
  }
}
