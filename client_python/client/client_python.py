from client_python.lib.remote_file_client import RemoteFileClient


def main():
    client = RemoteFileClient("config/config.txt")
    fd = None
    print("== Remote File System - Python Client ==")
    print("Digite 'help' para mostrar os comandos disponíveis.")

    try:
        while True:
            try:
                line = input("> ").strip()
            except EOFError:
                break
            if not line:
                continue

            parts = line.split(maxsplit=2)  # até 3 partes (write com texto)
            cmd = parts[0].lower()

            try:
                if cmd == "help":
                    print("""Comandos:
  open <arquivo>        - abre/cria arquivo remoto
  read <pos> <tam>      - lê 'tam' bytes a partir de 'pos'
  write <pos> <texto>   - escreve <texto> a partir de 'pos'
  close                 - fecha o arquivo atual
  stats                 - mostra hits/misses da cache
  exit                  - sai""")

                elif cmd == "open":
                    if len(parts) < 2:
                        print("Uso: open <arquivo>")
                        continue
                    nome = parts[1]
                    fd = client.abre(nome)
                    print(f"Arquivo aberto: '{nome}' (fd={fd})")

                elif cmd == "read":
                    if fd is None:
                        print("Nenhum arquivo aberto. Use: open <arquivo>")
                        continue
                    if len(parts) < 3:
                        print("Uso: read <pos> <tam>")
                        continue
                    pos = int(parts[1])
                    tam = int(parts[2])
                    data = client.le(fd, pos, tam)
                    print(f"Lidos {len(data)} bytes")
                    print("String:", data.decode(errors="ignore"))

                elif cmd == "write":
                    if fd is None:
                        print("Nenhum arquivo aberto. Use: open <arquivo>")
                        continue
                    if len(parts) < 3:
                        print("Uso: write <pos> <texto>")
                        continue
                    pos = int(parts[1])
                    texto = parts[2]
                    n = client.escreve(fd, pos, texto.encode())
                    print(f"Escreveu {n} bytes.")

                elif cmd == "close":
                    if fd is None:
                        print("Nenhum arquivo aberto.")
                        continue
                    client.fecha(fd)
                    print(f"fd {fd} fechado.")
                    fd = None

                elif cmd == "stats":
                    print(
                        f"cache hits={client.get_cache_hits()}, misses={client.get_cache_misses()}"
                    )

                elif cmd in ("exit", "quit"):
                    if fd is not None:
                        try:
                            client.fecha(fd)
                        except Exception:
                            pass
                        fd = None
                    print("Tchau!")
                    break

                else:
                    print("Comando desconhecido. Digite 'help'.")
            except Exception as e:
                print("Erro:", str(e))

    finally:
        client.close()


if __name__ == "__main__":
    main()
