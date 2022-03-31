package xyz.awesomenetwork.skyroyale.configs;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class YamlConfigBase {
	private final YamlConfiguration config;

	public YamlConfigBase(JavaPlugin plugin, String configFileName) {
		this(plugin, configFileName, false);
	}
	public YamlConfigBase(JavaPlugin plugin, String configFileName, boolean overwrite) {
		File configFile = new File(plugin.getDataFolder().getAbsolutePath() + "/" + configFileName);
		if (!configFile.exists()) {
			plugin.saveResource(configFileName, overwrite);
		}

		config = YamlConfiguration.loadConfiguration(configFile);
	}

	public YamlConfiguration getConfig() {
		return config;
	}
}
