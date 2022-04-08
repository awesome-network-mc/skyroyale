package xyz.awesomenetwork.skyroyale.listeners;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;

import xyz.awesomenetwork.minigametemplate.GameManager;
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
	private final BukkitScheduler scheduler;
	private final BossBar islandCrumbleBar;
	private final GameManager gameManager;

	private final IslandDeleter islandDeleter = new IslandDeleter();
	private final BlockData BEDROCK = Material.BEDROCK.createBlockData();
	private final BlockData CHEST = Material.CHEST.createBlockData();
	private final ChestPopulator chestPopulator = new ChestPopulator();
	private final HashMap<Integer, Integer> blockFallSpeed = new HashMap<>(); // <Block fall distance, Ticks required to fall that distance>

	private final String SUPPLY_DROP_SUBTITLE = "Supply drops are falling!";

	public GameStartListener(Plugin plugin, IslandManager islandManager, SchematicHandler schematicHandler, LoadedSchematic islandSpawnBox, World islandWorld, SkyRoyaleConfig skyRoyaleConfig, ChestLootConfig chestConfig, BossBar islandCrumbleBar, GameManager gameManager) {
		this.plugin = plugin;
		this.islandManager = islandManager;
		this.schematicHandler = schematicHandler;
		this.ISLAND_SPAWN_BOX_SCHEMATIC = islandSpawnBox;
		this.islandWorld = islandWorld;
		this.skyRoyaleConfig = skyRoyaleConfig;
		this.chestConfig = chestConfig;
		this.scheduler = plugin.getServer().getScheduler();
		this.islandCrumbleBar = islandCrumbleBar;
		this.gameManager = gameManager;

		/*
		 * Pre-calculate how many ticks it roughly takes for a falling block to fall X blocks
		 * This is used because while I can track when falling blocks land successfully, I cannot track when they land on a half slab and break into an item
		 * Therefore there is a scheduled task alongside the falling block that estimates when the falling block will land, then places a chest at its pre-determined landing point
		 * It may get messy if players place blocks in the time between falling block start and finish but this was good enough for 95% of cases - plus the chest will always spawn regardless
		*/
		int ticks = 0;
		double y = 0;
		double velocity = 0;
		while (y < islandWorld.getMaxHeight()) {
			ticks++;
			velocity += 0.03;
			if (velocity > 57.46) velocity = 57.46; // Roughly the terminal velocity of a falling block

			y += velocity;

			int blockY = (int) Math.floor(y);
			if (!blockFallSpeed.containsKey(blockY)) blockFallSpeed.put(blockY, ticks);
		}
	}

	@EventHandler
	public void gameStart(GameStartEvent e) {
		// Populate island chests at start of game so players can't use hacks or spectator mode to peek in the chests
		islandManager.populateAllIslandChests(chestConfig.getTier(1));

		// Remove spawn box schematics so players fall
		islandManager.getIslands().forEach(island -> {
			schematicHandler.pasteSchematic(new SchematicPasteOptions(ISLAND_SPAWN_BOX_SCHEMATIC, islandManager.getIslandSpawnBoxCentre(island.getIslandNumber()), 0, islandDeleter, 0));
		});

		int amountOfIslands = islandManager.getIslands().size();
		// This is a magic formula that someone I was working with wrote ~5 years ago. It was designed to modify the island crumble start time based on how many players the game started with, and to make the game feel fast paced no matter the size. It was only designed for up to 100 people.
		int crumbleStartTicks = (int) (Math.floor(21.3 + Math.sqrt(100 * (amountOfIslands - 1)) + (2.545 / (2 * Math.pow(10, 15))) * Math.pow(amountOfIslands, 8) - (13 / (104 * Math.pow(10, 6))) * Math.pow(amountOfIslands, 4)) + 29) * 20;

		// When time hits 12000, islands start to crumble as it turns to night
		islandWorld.setTime(12000 - crumbleStartTicks);
		islandWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);

		// Set initial world border to just outside the furthest island
		SpawnedIsland furthestIsland = islandManager.getIslands().get(islandManager.getIslands().size() - 1);
		int x = Math.abs(furthestIsland.getRelativeIslandCoordinates().x);
		int z = Math.abs(furthestIsland.getRelativeIslandCoordinates().z);
		int distance = x > z ? x : z;
		distance += skyRoyaleConfig.getDistanceBetweenIslands();
		distance *= 2;
		islandWorld.getWorldBorder().setCenter(0.0, 0.0);
		islandWorld.getWorldBorder().setSize(distance * 2);

		// Create task to start island crumbling and world border shrinking
		scheduler.scheduleSyncDelayedTask(plugin, () -> {
			String title = ChatColor.GOLD + "The islands are crumbling!";
			String subtitle = "Get to the centre island!";
			gameManager.getIngamePlayers().forEach(player -> player.sendTitle(title, subtitle, 5, 50, 10));

			islandManager.crumbleIslands();

			// World border should finish just outside the centre island
			islandWorld.getWorldBorder().setSize(skyRoyaleConfig.getDistanceBetweenIslands() * 2, skyRoyaleConfig.getIslandCrumbleSpeed() / 20);
		}, crumbleStartTicks);

		// Create scheduled tasks for supply drops
		scheduler.scheduleSyncDelayedTask(plugin, () -> scheduleSupplyDrop(chestConfig.getTier(2)), crumbleStartTicks / 3);
		scheduler.scheduleSyncDelayedTask(plugin, () -> scheduleSupplyDrop(chestConfig.getTier(3)), (int) (crumbleStartTicks / 1.5));

		// Start island crumble timer boss bar
		islandCrumbleBar.setVisible(true);
		plugin.getServer().getPluginManager().registerEvents(new GameRunningTimeListener(islandCrumbleBar, crumbleStartTicks / 20), plugin);
	}

	private Location generateSupplyDropLocation(int islandNumber) {
		int radius = skyRoyaleConfig.getIslandSupplyDropRadiusFromCentre();
		IslandCoordinates coords = islandManager.getIslandAbsoluteCoordinates(islandNumber);
		double x = coords.x + ThreadLocalRandom.current().nextDouble(-radius, radius + 1.0);
		double z = coords.z + ThreadLocalRandom.current().nextDouble(-radius, radius + 1.0);
		return new Location(islandWorld, x, skyRoyaleConfig.getBuildHeightLimit() + 3, z);
	}

	private void scheduleSupplyDrop(LootTier loot) {
		islandManager.getIslands().forEach(island -> {

			// Generate random point for supply drop to fall from
			Location dropPoint = generateSupplyDropLocation(island.getIslandNumber());

			// Find ground block below
			int fallDistance = 0;
			Block chestBlock = dropPoint.getBlock();
			Block below;
			while ((below = chestBlock.getRelative(BlockFace.DOWN, 1)).getType() == Material.AIR) {
				chestBlock = below;
				fallDistance++;
			}

			// Schedule chest to appear and populate when falling block hits the ground
			final Block finalChestBlock = chestBlock;
			scheduler.scheduleSyncDelayedTask(plugin, () -> {
				finalChestBlock.setBlockData(CHEST);
				chestPopulator.populate(loot, 13, finalChestBlock);
			}, blockFallSpeed.get(fallDistance));

			// Start falling block
			islandWorld.spawnFallingBlock(dropPoint, BEDROCK);

			// Notify player of supply drop (title must have space or the subtitle doesn't show)
			island.getAssignedPlayer().sendTitle(" ", SUPPLY_DROP_SUBTITLE, 0, 40, 10);
		});
	}
}
