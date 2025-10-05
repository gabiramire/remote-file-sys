import grpc
import configparser
from collections import OrderedDict

# Importa os stubs gerados do .proto
from client_python import remote_file_pb2 as pb2
from client_python import remote_file_pb2_grpc as pb2_grpc


class RemoteFileClient:
    def __init__(self, config_path="config/config.txt"):
        # Configuração
        config = configparser.ConfigParser()
        config.read(config_path)
        section = config["DEFAULT"] if "DEFAULT" in config else config["config"]

        host = section.get("host", "localhost")
        port = int(section.get("port", "50051"))
        self.block_size = int(section.get("block_size", "4096"))
        self.cache_capacity = int(section.get("cache_max_entries", "100"))

        # Canal gRPC
        self.channel = grpc.insecure_channel(f"{host}:{port}")
        self.stub = pb2_grpc.RemoteFileSystemStub(self.channel)

        # Cache (LRU)
        self.cache = OrderedDict()
        self.versao_por_fd = {}
        self.hits = 0
        self.misses = 0

        print(f"[PythonClient] Conectado a {host}:{port}")

    def abre(self, nome_arquivo: str) -> int:
        req = pb2.OpenRequest(nomeArquivo=nome_arquivo)
        res = self.stub.Abre(req)
        if res.codigoErro != 0:
            raise RuntimeError(f"Erro ao abrir {nome_arquivo}, codigo={res.codigoErro}")
        fd = res.descritor
        self.versao_por_fd[fd] = 0
        self._invalidate_fd(fd)
        return fd

    def le(self, fd: int, pos: int, tamanho: int) -> bytes:
        if tamanho <= 0:
            return b""

        block_size = self.block_size
        ini = pos // block_size
        fim = (pos + tamanho - 1) // block_size
        ver_local = self.versao_por_fd.get(fd, 0)

        resultado = bytearray()

        for b in range(ini, fim + 1):
            block_start = b * block_size
            block_end = block_start + block_size
            want_start = max(pos, block_start)
            want_end = min(block_end, pos + tamanho)
            want_len = max(0, want_end - want_start)

            if want_len == 0:
                continue

            key = (fd, b)
            entry = self.cache.get(key)

            hit = entry and entry["versao"] == ver_local
            if hit:
                block_data = entry["data"]
                self.hits += 1
            else:
                self.misses += 1
                req = pb2.ReadRequest(descritor=fd, posicao=block_start, tamanho=block_size)
                res = self.stub.Le(req)

                if res.codigoErro != 0:
                    raise RuntimeError(f"Erro ao ler fd={fd}, codigo={res.codigoErro}")

                ver_srv = res.versao
                if ver_srv != ver_local:
                    self._invalidate_fd(fd)
                    self.versao_por_fd[fd] = ver_srv
                    ver_local = ver_srv

                data = bytes(res.conteudo)
                if len(data) < block_size:
                    data = data + bytes(block_size - len(data))

                self.cache[key] = {"data": data, "versao": ver_local}
                if len(self.cache) > self.cache_capacity:
                    self.cache.popitem(last=False)
                block_data = data

            src_off = want_start - block_start
            resultado.extend(block_data[src_off : src_off + want_len])

        return bytes(resultado)

    def escreve(self, fd: int, pos: int, dados: bytes) -> int:
        req = pb2.WriteRequest(descritor=fd, posicao=pos, conteudo=dados)
        res = self.stub.Escreve(req)
        if res.codigoErro != 0:
            raise RuntimeError(f"Erro ao escrever, codigo={res.codigoErro}")

        self.versao_por_fd[fd] = res.versao
        self._invalidate_fd(fd)
        return res.bytesEscritos

    def fecha(self, fd: int):
        req = pb2.CloseRequest(descritor=fd)
        res = self.stub.Fecha(req)
        if res.codigoErro != 0:
            raise RuntimeError(f"Erro ao fechar fd={fd}, codigo={res.codigoErro}")
        self._invalidate_fd(fd)
        self.versao_por_fd.pop(fd, None)

    def close(self):
        self.channel.close()

    def _invalidate_fd(self, fd: int):
        to_remove = [k for k in self.cache.keys() if k[0] == fd]
        for k in to_remove:
            self.cache.pop(k, None)

    # Debug / métricas
    def get_cache_hits(self):
        return self.hits

    def get_cache_misses(self):
        return self.misses
