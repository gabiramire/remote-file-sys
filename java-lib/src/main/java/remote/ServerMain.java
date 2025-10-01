package remote;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class ServerMain {
  public static void main(String[] args) throws Exception {
    int port = 50051; // simples; pode ler de config depois
    Server server = ServerBuilder
        .forPort(port)
        .addService(new FileServiceImpl())
        .build()
        .start();

    System.out.println("[Server] gRPC ON port " + port);
    server.awaitTermination();
  }
}
