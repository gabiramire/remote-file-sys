package remotefs.client;

import remotefs.lib.RemoteFileClient;

import java.nio.charset.StandardCharsets;
import java.util.Scanner;

// Cliente Java com interação pelo terminal
public class ClientJava {

    public static void main(String[] args) {
        try (RemoteFileClient client = new RemoteFileClient(); Scanner in = new Scanner(System.in)) {
            System.out.println("== RemoteFS ==");
            System.out.println("digite 'help' para ajuda.");

            Integer fd = null; // descritor atual (se aberto)
            while (true) {
                System.out.print("> ");
                if (!in.hasNextLine()) break;
                String line = in.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+", 3); // até 3 pedaços (para write com texto)
                String cmd = parts[0].toLowerCase();

                try {
                    switch (cmd) {
                        case "help" -> {
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
                        case "open" -> {
                            if (parts.length < 2) { System.out.println("uso: open <arquivo>"); break; }
                            String nome = parts[1];
                            fd = client.abre(nome);
                            System.out.println("arquivo aberto: '" + nome + "' (fd=" + fd + ")");
                        }
                        case "read" -> {
                            if (fd == null) { System.out.println("nenhum arquivo aberto. use: open <arquivo>"); break; }
                            if (parts.length < 3) { System.out.println("uso: read <pos> <tam>"); break; }
                            int pos = Integer.parseInt(parts[1]);
                            int tam = Integer.parseInt(parts[2]);
                            byte[] data = client.le(fd, pos, tam);
                            // mostra tanto bytes quanto string 
                            System.out.println("lidos " + data.length + " bytes");
                            System.out.println("string: \"" + new String(data, StandardCharsets.UTF_8) + "\"");
                        }
                        case "write" -> {
                            if (fd == null) { System.out.println("nenhum arquivo aberto. use: open <arquivo>"); break; }
                            if (parts.length < 3) { System.out.println("uso: write <pos> <texto>"); break; }
                            int pos = Integer.parseInt(parts[1]);
                            String texto = parts[2]; // mantém espaços
                            int n = client.escreve(fd, pos, texto.getBytes(StandardCharsets.UTF_8));
                            System.out.println("escreveu " + n + " bytes.");
                        }
                        case "close" -> {
                            if (fd == null) { System.out.println("nenhum arquivo aberto."); break; }
                            client.fecha(fd);
                            System.out.println("fd " + fd + " fechado.");
                            fd = null;
                        }
                        case "stats" -> {
                            System.out.println("cache hits=" + client.getCacheHits() + ", misses=" + client.getCacheMisses());
                        }
                        case "exit", "quit" -> {
                            if (fd != null) {
                                try { client.fecha(fd); } catch (Exception ignored) {}
                                fd = null;
                            }
                            System.out.println("tchau!");
                            return;
                        }
                        default -> System.out.println("comando desconhecido. digite 'help'.");
                    }
                } catch (Exception e) {
                    System.out.println("erro: " + e.getMessage());
                }
            }
        }
    }
}
