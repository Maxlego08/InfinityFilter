package eu.realalpha.infinityfilter.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class FilterBungee extends Plugin {

	private String key;
	private boolean onlineMode;
	private Configuration config;

	@Override
	public void onEnable() {
		this.getProxy().getPluginManager().registerListener(this, new SetProtocolHandler(this));
		this.loadConfig();
	}

	public void loadConfig() {
		File file;
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdir();
		}
		if (!(file = new File(this.getDataFolder(), "config.yml")).isFile()) {
			try (InputStream in = this.getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(this.getDataFolder(), "config.yml"));
			this.key = this.config.getString("secret-key");
			this.onlineMode = this.config.getBoolean("allow-external-connexion");
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
		this.config.set("secret-key", key);
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(this.config,
					new File(this.getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
