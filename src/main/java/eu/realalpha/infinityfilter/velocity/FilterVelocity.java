package eu.realalpha.infinityfilter.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Plugin(
        id = "forward",
        name = "Forward",
        description = "Forward setup real ip",
        version = "0.0.1"
)
public class FilterVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataFolder;
    private final Yaml yaml = new Yaml();
    private String key;
    private boolean onlineMode;

    @Inject
    public FilterVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataFolder) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataFolder;
        dataFolder.toFile().mkdir();
        loadConfig();

    }

    public void loadConfig() {
        File file;
        if (!(file = new File(dataFolder.toFile(), "config.yml")).isFile()) {
            try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try(FileInputStream fileInputStream = new FileInputStream(new File(this.dataFolder.toFile(), "config.yml"))) {
            Map<String, Object> config = (Map<String, Object>) yaml.load(fileInputStream);
            this.key = (String) config.get("secret-key");
            this.onlineMode = (boolean) config.get("online-mode");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
         server.getEventManager().register(this, new SetProtocolHandler(this));
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public ProxyServer getServer() {
        return server;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getKey() {
        return key;
    }
}
