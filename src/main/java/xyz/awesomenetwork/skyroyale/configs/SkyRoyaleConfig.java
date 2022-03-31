package xyz.awesomenetwork.skyroyale.configs;

import org.bukkit.plugin.java.JavaPlugin;

public class SkyRoyaleConfig extends YamlConfigBase {
	private final String DEFAULT_ISLAND_GENERATE_SPEED_TICKS = "default_island_generate_speed_ticks";
	private final String ISLAND_GENERATE_SPEED_MULTIPLIER_START = "island_generate_speed_multiplier_start";
	private final String ISLAND_GENERATE_SPEED_MULTIPLIER_END = "island_generate_speed_multiplier_end";
	private final String ISLAND_Y = "island_y";
	private final String ISLAND_SPAWN_BOX_Y = "island_spawn_box_y";
	private final String ISLAND_SPAWN_BOX_SCHEMATIC = "island_spawn_box_schematic";
	private final String DISTANCE_BETWEEN_ISLANDS = "distance_between_islands";
	private final String ISLAND_WORLD_NAME = "island_world_name";
	private final String BASE_ISLAND_CRUMBLE_START_SECONDS = "base_island_crumble_start_seconds";

	public SkyRoyaleConfig(JavaPlugin plugin) {
		super(plugin, "skyroyale.yml", true);
	}

	public int getDefaultIslandGenerateSpeedTicks() {
		return getConfig().getInt(DEFAULT_ISLAND_GENERATE_SPEED_TICKS);
	}

	public int getIslandGenerateSpeedMultiplierStart() {
		return getConfig().getInt(ISLAND_GENERATE_SPEED_MULTIPLIER_START);
	}

	public int getIslandGenerateSpeedMultiplierEnd() {
		return getConfig().getInt(ISLAND_GENERATE_SPEED_MULTIPLIER_END);
	}

	public int getIslandY() {
		return getConfig().getInt(ISLAND_Y);
	}

	public int getIslandSpawnBoxY() {
		return getConfig().getInt(ISLAND_SPAWN_BOX_Y);
	}

	public String getIslandSpawnBoxSchematicName() {
		return getConfig().getString(ISLAND_SPAWN_BOX_SCHEMATIC);
	}

	public int getDistanceBetweenIslands() {
		return getConfig().getInt(DISTANCE_BETWEEN_ISLANDS);
	}

	public String getIslandsWorldName() {
		return getConfig().getString(ISLAND_WORLD_NAME);
	}

	public int getBaseIslandCrumbleStartSeconds() {
		return getConfig().getInt(BASE_ISLAND_CRUMBLE_START_SECONDS);
	}
}
