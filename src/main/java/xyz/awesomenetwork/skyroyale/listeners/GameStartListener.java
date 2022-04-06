package xyz.awesomenetwork.skyroyale.listeners;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import xyz.awesomenetwork.minigametemplate.events.GameStartEvent;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.SchematicPasteOptions;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.chests.ChestPopulator;
import xyz.awesomenetwork.skyroyale.configs.ChestLootConfig;
import xyz.awesomenetwork.skyroyale.configs.SkyRoyaleConfig;
import xyz.awesomenetwork.skyroyale.islands.IslandCoordinates;
import xyz.awesomenetwork.skyroyale.islands.IslandDeleter;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;
import xyz.awesomenetwork.skyroyale.islands.SpawnedIsland;
import xyz.awesomenetwork.skyroyale.loot.LootTier;

public class GameStartListener implements Listener {
	private final Plugin plugin;
	private final IslandManager islandManager;
	private final SchematicHandler schematicHandler;
	private final LoadedSchematic ISLAND_SPAWN_BOX_SCHEMATIC;
	private final World islandWorld;
	private final SkyRoyaleConfig skyRoyaleConfig;
	private final ChestLootConfig chestConfig;

	private final IslandDeleter islandDeleter = new IslandDeleter();
	private final BlockData BEDROCK = Material.BEDROCK.createBlockData();
	private final BlockData CHEST = Material.CHEST.createBlockData();
	private final ChestPopulator chestPopulator = new ChestPopulator();

	public GameStartListener(Plugin plugin, IslandManager islandManager, SchematicHandler schematicHandler, LoadedSchematic islandSpawnBox, World islandWorld, SkyRoyaleConfig skyRoyaleConfig, ChestLootConfig chestConfig) {
		this.plugin = plugin;
		this.islandManager = islandManager;
		this.schematicHandler = schematicHandler;
		this.ISLAND_SPAWN_BOX_SCHEMATIC = islandSpawnBox;
		this.islandWorld = islandWorld;
		this.skyRoyaleConfig = skyRoyaleConfig;
		this.chestConfig = chestConfig;
	}

	@EventHandler
	public void gameStart(GameStartEvent e) {
		islandManager.getIslands().forEach(island -> islandManager.populateAllIslandChests(1));

		islandManager.getIslands().forEach(island -> {
			schematicHandler.pasteSchematic(new SchematicPasteOptions(ISLAND_SPAWN_BOX_SCHEMATIC, islandManager.getIslandSpawnBoxCentre(island.getIslandNumber()), 0, islandDeleter, 0));
		});

		int amountOfIslands = islandManager.getIslands().size();
		// This is a magic formula that someone I was working with wrote ~5 years ago. It was designed to modify the island crumble start time based on how many players the game started with, and to make the game feel fast paced no matter the size. It was only designed for up to 100 people.
		int crumbleStartTicks = (int) (Math.floor(21.3 + Math.sqrt(100 * (amountOfIslands - 1)) + (2.545 / (2 * Math.pow(10, 15))) * Math.pow(amountOfIslands, 8) - (13 / (104 * Math.pow(10, 6))) * Math.pow(amountOfIslands, 4)) + 29) * 20;

		// When time hits 12000, islands start to crumble as it turns to night
		islandWorld.setTime(12000 - (crumbleStartTicks));
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

		// Create task to start island crumbling and world border shrinking
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			islandManager.crumbleIslands();
			islandWorld.getWorldBorder().setSize(skyRoyaleConfig.getDistanceBetweenIslands() * 2, skyRoyaleConfig.getIslandCrumbleSpeed() / 20);
		}, crumbleStartTicks);

		// Create scheduled tasks for supply drops
		int supplyDrop1Ticks = (crumbleStartTicks) / 3;
		int supplyDrop2Ticks = (int) ((crumbleStartTicks) / 1.5);

		LootTier lootTier2 = chestConfig.getTier(2);
		LootTier lootTier3 = chestConfig.getTier(3);
		islandManager.getIslands().forEach(island -> {
			int islandNumber = island.getIslandNumber();
			scheduleSupplyDrop(lootTier2, islandNumber, supplyDrop1Ticks);
			scheduleSupplyDrop(lootTier3, islandNumber, supplyDrop2Ticks);
		});
	}

	private Location generateSupplyDropLocation(int islandNumber) {
		int radius = skyRoyaleConfig.getIslandSupplyDropRadiusFromCentre();
		IslandCoordinates coords = islandManager.getIslandAbsoluteCoordinates(islandNumber);
		double x = coords.x + ThreadLocalRandom.current().nextDouble(-radius, radius + 1.0);
		double z = coords.z + ThreadLocalRandom.current().nextDouble(-radius, radius + 1.0);
		return new Location(islandWorld, x, skyRoyaleConfig.getBuildHeightLimit() + 3, z);
	}

	private void scheduleSupplyDrop(LootTier loot, int islandNumber, int waitTicks) {
		final Location dropPoint = generateSupplyDropLocation(islandNumber);

		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			islandManager.getIslands().forEach(island -> islandWorld.spawnFallingBlock(dropPoint, BEDROCK));
		}, waitTicks);

		// Find ground block
		Block block = new Location(islandWorld, dropPoint.getX(), skyRoyaleConfig.getIslandSpawnBoxY() - 1, dropPoint.getZ()).getBlock(); // Start at the spawn box centre instead of drop point because otherwise it would stop at the barrier blocks in the spawn box schematic
		Block below;
		while ((below = block.getRelative(BlockFace.DOWN, 1)).getType() == Material.AIR) {
			block = below;
		}

		// Calculate amount of time required to reach ground
		int ticksRequired = 0;
		double velocity = 0;
		double yCurrent = dropPoint.getBlockY();
		int yEnd = block.getLocation().getBlockY();
		while (yCurrent > yEnd) {
			ticksRequired++;
			velocity += 0.03;
			if (velocity > 57.46) velocity = 57.46;
			yCurrent -= velocity;
		}

		final Block chestBlock = block;
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
			// Place and populate chest
			chestBlock.setBlockData(CHEST);
			chestPopulator.populate(loot, 13, (Chest) chestBlock.getState()); // Max of 13 items because if the supply drops happen to land in the same chest there will always be space for everything (providing the player hasn't stored items there)
		}, waitTicks + ticksRequired);
	}
}
