package xyz.awesomenetwork.skyroyale.maps;

import java.util.List;

public class MapInfo {
	private final String name;
	private final List<String> schematics;
	
	public MapInfo(String name, List<String> schematics) {
		this.name = name;
		this.schematics = schematics;
	}

	public String getName() {
		return name;
	}

	public List<String> getIslandSchematics() {
		return schematics;
	}
}
