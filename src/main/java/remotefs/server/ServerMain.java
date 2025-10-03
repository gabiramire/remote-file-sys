package remotefs.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import remotefs.util.ConfigLoader;

import java.nio.file.Path;

// Classe principal do servidor
public class ServerMain {
  public static void main(String[] args) throws Exception {
    // carrega config.txt para pegar porta e rootDir
    ConfigLoader config = new ConfigLoader("config/config.txt");

    int port = config.getPort();
    Path root = Path.of(config.getRootDir());  

    Server server = ServerBuilder.forPort(port)
        .addService(new FileServiceImpl(root)) 
        .build()
        .start();

    System.out.println("Servidor rodando porta " + port + " root=" + root.toAbsolutePath());
    server.awaitTermination();
  }
}
