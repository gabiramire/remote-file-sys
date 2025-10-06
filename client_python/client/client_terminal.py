from client_python.lib.remote_file_client import RemoteFileClient

class ClientTerminal:
    def __init__(self):
        self.client_remote_file_sys = RemoteFileClient("config/config.txt")
        self.fd = None
        self.running = True

        try:
            while self.running:
                try:
                    line = input("> ").strip()
                except EOFError:
                    break

                if not line:
                    continue

                parts = line.split(maxsplit=2)
                cmd = parts[0].lower()

                try:
                    self.select_command(cmd, parts)
                except Exception as e:
                    print(f"Erro ao executar comando '{cmd}': {e}\n")

        except Exception as e:
            print(f"Erro inesperado: {e}")
        finally:
            self.client_remote_file_sys.close()

    def select_command(self, cmd, parts):
        match cmd:
            case "help":
                self.help()
            case "open":
                self.open(parts)
            case "read":
                self.read(parts)
            case "write":
                self.write(parts)
            case "close":
                self.close()
            case "stats":
                self.stats()
            case "exit":
                self.exit()
                return
            case _:
                self.invalid_command()

    def help(self):
        print("""Comandos:
                open <arquivo>        - abre/cria arquivo remoto
                read <pos> <tam>      - lê 'tam' bytes a partir de 'pos'
                write <pos> <texto>   - escreve <texto> a partir de 'pos'
                close                 - fecha o arquivo atual
                stats                 - mostra hits/misses da cache
                exit                  - sai""")

    def open(self, parts):
        if len(parts) < 2:
            return self.invalid_command_use('open')
        
        if self.fd is not None:
            return print("Já há um arquivo aberto. Utilize o 'close' para fechá-lo antes de abrir outro.")

        nome = parts[1]
        self.fd = self.client_remote_file_sys.abre(nome)
        print(f"Arquivo aberto: '{nome}' (fd={self.fd})")

    def read(self, parts):
        if self.fd is None:
            return self.not_opened_file()
        if len(parts) < 3:
            return self.invalid_command_use('read')

        pos = int(parts[1])
        tam = int(parts[2])
        data = self.client_remote_file_sys.le(self.fd, pos, tam)
        print(f"Lidos {len(data)} bytes")
        print("String:", data.decode(errors="ignore"))

    def write(self, parts):
        if self.fd is None:
            return self.not_opened_file()
        if len(parts) < 3:
            return self.invalid_command_use('write')

        pos = int(parts[1])
        texto = parts[2]
        n = self.client_remote_file_sys.escreve(self.fd, pos, texto.encode())
        print(f"Escreveu {n} bytes.")

    def close(self):
        if self.fd is None:
            return self.not_opened_file()

        self.client_remote_file_sys.fecha(self.fd)
        print(f"fd {self.fd} fechado.")
        self.fd = None

    def stats(self):
        hits = self.client_remote_file_sys.get_cache_hits()
        misses = self.client_remote_file_sys.get_cache_misses()
        print(f"cache hits={hits}, misses={misses}")

    def exit(self):
        if self.fd is not None:
            try:
                print("Fechando arquivo aberto antes de sair...")
                self.client_remote_file_sys.fecha(self.fd)
            except Exception as e:
                print(f"Erro ao fechar arquivo: {e}")
            self.fd = None

        print("Até logo!")
        self.client_remote_file_sys.close()
        self.running = False

    def invalid_command(self):
        print("Comando inválido. Digite 'help' para ver os comandos disponíveis.")

    def invalid_command_use(self, cmd):
        print(f"Uso inválido do comando {cmd}.")
        match cmd:
            case "open":
                print("Utilize: open <arquivo>")
            case "read":
                print("Utilize: read <pos> <tam>")
            case "write":
                print("Utilize: write <pos> <texto>")

    def not_opened_file(self):
        print("Não há nenhum arquivo aberto. Utilize open <arquivo> para abrir um arquivo.")


if __name__ == "__main__":
    print("== Remote File System - Python Client ==")
    print("Digite 'help' para mostrar os comandos disponíveis.")
    ClientTerminal()
