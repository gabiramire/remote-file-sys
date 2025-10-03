package remotefs.server;

import java.io.IOException;
import java.io.RandomAccessFile; 
import java.nio.file.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Responsável por gerenciar arquivos no servidor
class FileManager {
  private final Path root;
  private final AtomicInteger nextFd = new AtomicInteger(1);
  private final Map<Integer, RandomAccessFile> fdMap = new ConcurrentHashMap<>();
  private final Map<Integer, Path> fdPath = new ConcurrentHashMap<>();
  private final Map<Path, AtomicInteger> versions = new ConcurrentHashMap<>();

  FileManager(Path rootDir) throws IOException {
    this.root = rootDir.toAbsolutePath().normalize();
    Files.createDirectories(this.root);
  }

  private Path resolveSafe(String userPath) {
    Path p = root.resolve(userPath).normalize();
    if (!p.startsWith(root)) throw new IllegalArgumentException("path traversal");
    return p;
  }

  // abre (ou cria) arquivo, retorna descritor
  int open(String userPath) throws IOException {
    Path p = resolveSafe(userPath);
    Files.createDirectories(p.getParent() == null ? root : p.getParent());
    RandomAccessFile raf = new RandomAccessFile(p.toFile(), "rw");
    int fd = nextFd.getAndIncrement();
    fdMap.put(fd, raf);
    fdPath.put(fd, p);
    versions.putIfAbsent(p, new AtomicInteger(0));
    return fd;
  }

  // lê até out.length bytes do arquivo (a partir de pos), retorna quantos bytes lidos
  int read(int fd, int pos, byte[] out) throws IOException {
    RandomAccessFile raf = fdMap.get(fd);
    if (raf == null) throw new IllegalArgumentException("fd inválido");
    raf.seek(pos);
    int n = raf.read(out, 0, out.length);
    return Math.max(n, 0);
  }

  // escreve data no arquivo (a partir de pos), retorna nova versão do arquivo
  int write(int fd, int pos, byte[] data) throws IOException {
    RandomAccessFile raf = fdMap.get(fd);
    if (raf == null) throw new IllegalArgumentException("fd inválido");
    raf.seek(pos);
    raf.write(data);
    Path p = fdPath.get(fd);
    return versions.computeIfAbsent(p, __ -> new AtomicInteger(0)).incrementAndGet(); // nova versão
  }

  // fecha arquivo
  int close(int fd) throws IOException {
    RandomAccessFile raf = fdMap.remove(fd);
    if (raf != null) raf.close();
    fdPath.remove(fd);
    return 0;
  }

  
  int getVersion(int fd) {
    Path p = fdPath.get(fd);
    return versions.getOrDefault(p, new AtomicInteger(0)).get();
  }
}
