# remote-file-sys

## Para rodar:

- Dentro de java-lib, rodar:

 ``` mvn -U clean compile ```
    
- Para rodar o server:

 ``` mvn exec:java -Dexec.mainClass=remotefs.server.ServerMain ```

- Para rodar o Client:

 ``` mvn exec:java -Dexec.mainClass=remotefs.client.ClientJava ```
 
Se o teste funcionar, um teste.txt será criado escrito "HELLO"

        
## Estrutura de Arquivos por enquanto:

  ``` 
  remote-file-sys/
    proto/
      remote_file.proto         # contrato gRPC (compartilhado por server e libs)

    config/
      config.txt                # host/port do server; 

    src/main/java/remotefs/
      server/
        ServerMain.java         # sobe o servidor 
        FileServiceImpl.java    # implementação dos RPCs (Open/Read/Write/Close)
        FileManager.java        # 

      lib/
        RemoteFileClient.java   # biblioteca (API): abre/le/escreve/fecha + cache por blocos(TODO)

      client/
        ClientJava.java         # cliente exemplo (Java)

    python/
      lib/
        remote_file_client.py   # biblioteca (API) equivalente em Python 
        remote_file_pb2.py      # gerado do .proto
        remote_file_pb2_grpc.py # gerado do .proto

      demo/
        client_python.py        # cliente exemplo (Python)

    pom.xml                    # para o maven
    README.md                  # como compilar/rodar 
  
  ```

