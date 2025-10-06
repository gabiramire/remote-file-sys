package remotefs.client;

import remotefs.lib.RemoteFileClient;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ClientJava {

    private final RemoteFileClient client;
    private Integer fd = null;
    private boolean running = true;

    public ClientJava() {
        this.client = new RemoteFileClient("config/config.txt");
        run();
    }

    private void run() {
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("== Remote File System - Java Client ==");
            System.out.println("Digite 'help' para mostrar os comandos disponíveis.");

            while (running) {
                System.out.print("> ");
                if (!in.hasNextLine()) break;

                String line = in.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 3);
                String cmd = parts[0].toLowerCase();

                try {
                    selectCommand(cmd, parts);
                } catch (Exception e) {
                    System.out.println("Erro ao executar comando '" + cmd + "': " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erro inesperado: " + e.getMessage());
        } finally {
            client.close();
        }
    }

    private void selectCommand(String cmd, String[] parts) {
        switch (cmd) {
            case "help" -> help();
            case "open" -> open(parts);
            case "read" -> read(parts);
            case "write" -> write(parts);
            case "close" -> close();
            case "stats" -> stats();
            case "exit" -> exit();
            default -> invalidCommand();
        }
    }

    private void help() {
        System.out.println("""
            Comandos:
                open <arquivo>        - abre/cria arquivo remoto
                read <pos> <tam>      - lê 'tam' bytes a partir de 'pos'
                write <pos> <texto>   - escreve <texto> a partir de 'pos'
                close                 - fecha o arquivo atual
                stats                 - mostra hits/misses da cache
                exit                  - sai
            """);
    }

    private void open(String[] parts) {
        if (parts.length < 2) {
            invalidCommandUse("open");
            return;
        }

        if (fd != null) {
            System.out.println("Já há um arquivo aberto. Utilize 'close' para fechá-lo antes de abrir outro.");
            return;
        }

        String nome = parts[1];
        fd = client.abre(nome);
        System.out.println("Arquivo aberto: '" + nome + "' (fd=" + fd + ")");
    }

    private void read(String[] parts) {
        if (fd == null) {
            notOpenedFile();
            return;
        }
        if (parts.length < 3) {
            invalidCommandUse("read");
            return;
        }

        int pos = Integer.parseInt(parts[1]);
        int tam = Integer.parseInt(parts[2]);
        byte[] data = client.le(fd, pos, tam);
        System.out.println("Lidos " + data.length + " bytes");
        System.out.println("String: " + new String(data, StandardCharsets.UTF_8));
    }

    private void write(String[] parts) {
        if (fd == null) {
            notOpenedFile();
            return;
        }
        if (parts.length < 3) {
            invalidCommandUse("write");
            return;
        }

        int pos = Integer.parseInt(parts[1]);
        String texto = parts[2];
        int n = client.escreve(fd, pos, texto.getBytes(StandardCharsets.UTF_8));
        System.out.println("Escreveu " + n + " bytes.");
    }

    private void close() {
        if (fd == null) {
            notOpenedFile();
            return;
        }

        client.fecha(fd);
        System.out.println("fd " + fd + " fechado.");
        fd = null;
    }

    private void stats() {
        System.out.println("cache hits=" + client.getCacheHits() + ", misses=" + client.getCacheMisses());
    }

    private void exit() {
        if (fd != null) {
            try {
                System.out.println("Fechando arquivo aberto antes de sair...");
                client.fecha(fd);
            } catch (Exception e) {
                System.out.println("Erro ao fechar arquivo: " + e.getMessage());
            }
            fd = null;
        }

        System.out.println("Até logo!");
        client.close();
        running = false;
    }

    private void invalidCommand() {
        System.out.println("Comando inválido. Digite 'help' para ver os comandos disponíveis.");
    }

    private void invalidCommandUse(String cmd) {
        System.out.println("Uso inválido do comando " + cmd + ".");
        switch (cmd) {
            case "open" -> System.out.println("Utilize: open <arquivo>");
            case "read" -> System.out.println("Utilize: read <pos> <tam>");
            case "write" -> System.out.println("Utilize: write <pos> <texto>");
        }
    }

    private void notOpenedFile() {
        System.out.println("Não há nenhum arquivo aberto. Utilize open <arquivo> para abrir um arquivo.");
    }

    public static void main(String[] args) {
        new ClientJava();
    }
}
