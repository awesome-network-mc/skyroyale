package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import xyz.awesomenetwork.minigametemplate.events.GameStartEvent;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.SchematicPasteOptions;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.configs.SkyRoyaleConfig;
import xyz.awesomenetwork.skyroyale.islands.IslandDeleter;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;
import xyz.awesomenetwork.skyroyale.islands.SpawnedIsland;

public class GameStartListener implements Listener {
	private final Plugin plugin;
	private final IslandManager islandManager;
	private final SchematicHandler schematicHandler;
	private final LoadedSchematic ISLAND_SPAWN_BOX_SCHEMATIC;
	private final World islandWorld;
	private final SkyRoyaleConfig skyRoyaleConfig;

	private final IslandDeleter islandDeleter = new IslandDeleter();

	public GameStartListener(Plugin plugin, IslandManager islandManager, SchematicHandler schematicHandler, LoadedSchematic islandSpawnBox, World islandWorld, SkyRoyaleConfig skyRoyaleConfig) {
		this.plugin = plugin;
		this.islandManager = islandManager;
		this.schematicHandler = schematicHandler;
		this.ISLAND_SPAWN_BOX_SCHEMATIC = islandSpawnBox;
		this.islandWorld = islandWorld;
		this.skyRoyaleConfig = skyRoyaleConfig;
	}

	@EventHandler
	public void gameStart(GameStartEvent e) {
		islandManager.getIslands().forEach(island -> islandManager.populateIslandChests(island.getIslandNumber(), 1));

		islandManager.getIslands().forEach(island -> {
			schematicHandler.pasteSchematic(new SchematicPasteOptions(ISLAND_SPAWN_BOX_SCHEMATIC, islandManager.getIslandSpawnBoxCentre(island.getIslandNumber()), 0, islandDeleter, 0));
		});

		int amountOfIslands = islandManager.getIslands().size();
		// This is a magic formula that someone I was working with wrote ~5 years ago. It was designed to modify the island crumble start time based on how many players the game started with, and to make the game feel fast paced no matter the size. It was only designed for up to 100 people.
		int crumbleStartSeconds = (int) Math.floor(21.3 + Math.sqrt(100 * (amountOfIslands - 1)) + (2.545 / (2 * Math.pow(10, 15))) * Math.pow(amountOfIslands, 8) - (13 / (104 * Math.pow(10, 6))) * Math.pow(amountOfIslands, 4)) + 29;

		// When time hits 12000, islands start to crumble as it turns to night
		islandWorld.setTime(12000 - (crumbleStartSeconds * 20));
		islandWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

		// Set initial world border
		SpawnedIsland furthestIsland = islandManager.getIslands().get(islandManager.getIslands().size() - 1);
		int x = Math.abs(furthestIsland.getRelativeIslandCoordinates().x);
		int z = Math.abs(furthestIsland.getRelativeIslandCoordinates().z);
		int distance = x > z ? x : z;
		distance += skyRoyaleConfig.getDistanceBetweenIslands();
		distance *= 2;
		islandWorld.getWorldBorder().setCenter(0.0, 0.0);
		islandWorld.getWorldBorder().setSize(distance * 2);

		plugin.getServer().getPluginManager().registerEvents(new GameRunningTimeListener(islandManager, islandWorld, skyRoyaleConfig), plugin);
	}
}
