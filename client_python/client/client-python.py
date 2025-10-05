from client_python.lib.remote_file_client import RemoteFileClient

def main():
    client = RemoteFileClient("config/config.txt")

    fd = client.abre("teste.txt")
    print(f"Arquivo aberto (fd={fd})")

    texto = b"Hello from Python Client!\n"
    client.escreve(fd, 0, texto)
    print("Escrita concluída")

    dados = client.le(fd, 0, len(texto))
    print("Leitura:", dados.decode())

    client.fecha(fd)
    print("Arquivo fechado")

    print(f"Cache hits={client.get_cache_hits()} | misses={client.get_cache_misses()}")

    client.close()

if __name__ == "__main__":
    main()