package xyz.awesomenetwork.skyroyale.configs;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.skyroyale.maps.MapInfo;

public class MapsConfig extends YamlConfigBase {
	private final String MAP_SCHEMATICS = "schematics";

	private final HashMap<String, MapInfo> maps = new HashMap<>();

	public MapsConfig(JavaPlugin plugin) {
		super(plugin, "maps.yml", true);

		getConfig().getConfigurationSection("").getKeys(false).forEach(mapName -> {
			ConfigurationSection mapConfig = getConfig().getConfigurationSection(mapName);
			List<String> schematics = mapConfig.getStringList(MAP_SCHEMATICS);
			maps.put(mapName, new MapInfo(mapName, schematics));
		});
	}

	public MapInfo getMap(String name) {
		return maps.get(name);
	}

	public Collection<MapInfo> getMaps() {
		return maps.values();
	}
}
