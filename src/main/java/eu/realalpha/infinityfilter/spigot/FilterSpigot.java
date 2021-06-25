package eu.realalpha.infinityfilter.spigot;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FilterSpigot extends JavaPlugin {

    private String key;
    private boolean onlineMode;

    @Override
    public void onEnable() {
        this.getDataFolder().mkdir();

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        protocolManager.addPacketListener(new SetProtocolHandler(this));

        loadConfig();
    }


    public void loadConfig() {
        File file;
        if (!(file = new File(this.getDataFolder(), "config.yml")).isFile()) {
            try (InputStream in = this.getClassLoader().getResourceAsStream("config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            this.getConfig().load(new File(this.getDataFolder(), "config.yml"));
            this.key = this.getConfig().getString("secret-key");
            this.onlineMode = this.getConfig().getBoolean("online-mode");
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    public String getKey() {
        return this.key;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public void setKey(String key) {
        this.key = key;
        this.getConfig().set("secret-key", key);
        try {
            this.getConfig().save(new File(this.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
