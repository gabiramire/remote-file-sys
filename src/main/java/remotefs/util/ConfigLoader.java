package remotefs.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

// Carrega o config.txt para pegar host, port, rootDir, etc
public class ConfigLoader {
    private final Properties props = new Properties();

    public ConfigLoader(String path) {
        try (FileReader reader = new FileReader(path)) {
            props.load(reader);
        } catch (IOException e) {
            System.err.println("Erro ao carregar config: " + e.getMessage());
        }
    }

    public String getHost() {
        return props.getProperty("host", "localhost");
    }

    public int getPort() {
        return Integer.parseInt(props.getProperty("port", "50051"));
    }

    public String getRootDir() {
        return props.getProperty("root", "./data");
    }

    public int getBlockSize() {
        return Integer.parseInt(props.getProperty("block_size", "4096"));
    }

    public int getCacheMaxEntries() {
        return Integer.parseInt(props.getProperty("cache_max_entries", "100"));
    }
}
