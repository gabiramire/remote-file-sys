package remotefs.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import remotefs.util.ConfigLoader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

// Classe principal do servidor. Carrega config, inicializa FileManager e inicia gRPC.
public class ServerMain {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Carregar config
        ConfigLoader config = new ConfigLoader("config/config.txt");
        int port = config.getPort();
        Path root = Paths.get(config.getRoot());

        // Inicializar FileManager
        FileManager fm = new FileManager(root);

        // Criar e iniciar servidor gRPC
        Server server = ServerBuilder.forPort(port)
                .addService(new FileServiceImpl(fm))
                .build();

        System.out.println("[Server] Iniciado em porta " + port + " com root=" + root.toAbsolutePath());
        server.start();
        server.awaitTermination();
    }
}
