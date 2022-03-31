package xyz.awesomenetwork.skyroyale.maps;

import java.util.List;

import org.bukkit.World.Environment;

public class MapInfo {
	private final String name;
	private final Environment environment;
	private final List<String> schematics;
	
	public MapInfo(String name, Environment environment, List<String> schematics) {
		this.name = name;
		this.environment = environment;
		this.schematics = schematics;
	}

	public String getName() {
		return name;
	}

	public Environment getEnvironment() {
		return this.environment;
	}

	public List<String> getIslandSchematics() {
		return schematics;
	}
}
