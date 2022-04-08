package xyz.awesomenetwork.skyroyale.maps;

import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.configs.MapsConfig;

public class MapManager {
	private final MapInfo loadedMap;
	private final LoadedSchematic[] islandSchematics;

	public MapManager(MapsConfig mapsConfig, SchematicHandler schematicHandler) throws IOException {
		MapInfo[] maps = mapsConfig.getMaps().toArray(new MapInfo[0]);

		// Pick a random map to use
		loadedMap = maps[ThreadLocalRandom.current().nextInt(maps.length)];

		// Load all schematics from disk
		islandSchematics = new LoadedSchematic[loadedMap.getIslandSchematics().size()];
		for (int i = 0; i < islandSchematics.length; i++) {
			islandSchematics[i] = schematicHandler.loadSchematic(loadedMap.getIslandSchematics().get(i));
		}
	}

	public MapInfo getLoadedMap() {
		return loadedMap;
	}

	public LoadedSchematic getRandomMapSchematic() {
		return islandSchematics[ThreadLocalRandom.current().nextInt(islandSchematics.length)];
	}
}
