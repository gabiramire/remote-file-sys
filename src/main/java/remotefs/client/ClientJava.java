package remotefs.client;

import remotefs.lib.RemoteFileClient;

// Cliente de demonstração em Java
public class ClientJava {
    public static void main(String[] args) {
        RemoteFileClient client = new RemoteFileClient("localhost", 50051);
        try {
            int fd = client.abre("teste.txt");
            System.out.println("Arquivo aberto com descritor: " + fd);

            String msg = "HELLO";
            client.escreve(fd, 0, msg.getBytes());
            System.out.println("Escrevi: " + msg);

            byte[] dados = client.le(fd, 0, 5);
            System.out.println("Li: " + new String(dados));

            client.fecha(fd);
        } finally {
            client.shutdown();
        }
    }
}
