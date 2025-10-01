package remote;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Arrays;

public class ClientMain {
  public static void main(String[] args) {
    String host = "127.0.0.1";
    int port = 50051;

    ManagedChannel ch = ManagedChannelBuilder.forAddress(host, port)
        .usePlaintext()
        .build();
    RemoteFileServiceGrpc.RemoteFileServiceBlockingStub stub =
        RemoteFileServiceGrpc.newBlockingStub(ch);

    try {
      // 1) Abrir (cria arquivo se não existir)
      var open = stub.open(OpenRequest.newBuilder().setNomeArquivo("teste.txt").build());
      if (open.getCodigoErro() != 0) throw new RuntimeException("open falhou");
      int fd = open.getDescritor();
      System.out.println("abriu fd=" + fd + " versao=" + open.getVersao());

      // 2) Escrever "HELLO" no offset 0
      var wr = stub.write(WriteRequest.newBuilder()
          .setDescritor(fd).setPosicao(0)
          .setDados(com.google.protobuf.ByteString.copyFromUtf8("HELLO"))
          .build());
      System.out.println("write: err=" + wr.getCodigoErro() + " bytes=" + wr.getBytesEscritos() + " versao=" + wr.getVersao());

      // 3) Ler 16 bytes a partir do offset 0
      var rr = stub.read(ReadRequest.newBuilder().setDescritor(fd).setPosicao(0).setTamanho(16).build());
      System.out.println("read: err=" + rr.getCodigoErro() + " bytes=" + rr.getBytesLidos() + " versao=" + rr.getVersao());
      byte[] got = rr.getDados().toByteArray();
      System.out.println("conteudo=" + Arrays.toString(got) + " string=\"" + new String(got, 0, rr.getBytesLidos()) + "\"");

      // 4) Fechar
      var cr = stub.close(CloseRequest.newBuilder().setDescritor(fd).build());
      System.out.println("close: err=" + cr.getCodigoErro());
    } finally {
      ch.shutdown();
    }
  }
}
