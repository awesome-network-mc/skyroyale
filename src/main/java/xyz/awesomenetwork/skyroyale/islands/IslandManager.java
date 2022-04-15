package xyz.awesomenetwork.skyroyale.islands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.SchematicPasteOptions;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.chests.ChestFinder;
import xyz.awesomenetwork.skyroyale.chests.ChestPopulator;
import xyz.awesomenetwork.skyroyale.configs.SkyRoyaleConfig;
import xyz.awesomenetwork.skyroyale.loot.LootTier;
import xyz.awesomenetwork.skyroyale.maps.MapManager;

public class IslandManager {
	private final GameManager gameManager;
	private final SchematicHandler schematics;
	private final MapManager mapManager;
	private final World islandWorld;
	private final LoadedSchematic islandSpawnBox;
	private final SkyRoyaleConfig skyRoyaleConfig;

	private final IslandCoordinates[] islandCoordinates;
	private final List<SpawnedIsland> islands = new ArrayList<>();
	private final Map<Player, Integer> assignedIslands = new HashMap<>();

	public IslandManager(GameManager gameManager, SchematicHandler schematics, MapManager mapManager, World islandWorld, LoadedSchematic islandSpawnBox, SkyRoyaleConfig skyRoyaleConfig) throws IOException {
		this.gameManager = gameManager;
		this.schematics = schematics;
		this.mapManager = mapManager;
		this.islandWorld = islandWorld;
		this.islandSpawnBox = islandSpawnBox;
		this.skyRoyaleConfig = skyRoyaleConfig;

		// Pre-alculate island positions in a spiral pattern around the centre island
		// I tried writing a formula for this but decided this is faster and easier
		int maxPlayers = gameManager.getOptions().maxPlayers;
		islandCoordinates = new IslandCoordinates[maxPlayers];
		IslandDirection direction = IslandDirection.NEGATIVE_X;
		int sideLength = 0;
		int sideCount = 0;
		int x = 0;
		int z = 0;
		for (int i = 0; i < maxPlayers; i++) {
			islandCoordinates[i] = new IslandCoordinates(x, z);

			sideCount++;
			switch (direction) {
			case POSITIVE_Z:
				z++;
				if (sideCount >= sideLength) direction = IslandDirection.POSITIVE_X;
				break;
			case POSITIVE_X:
				x++;
				if (sideCount >= sideLength) direction = IslandDirection.NEGATIVE_Z;
				break;
			case NEGATIVE_Z:
				z--;
				if (sideCount >= sideLength) direction = IslandDirection.NEGATIVE_X;
				break;
			case NEGATIVE_X:
				x--;
				if (sideCount >= sideLength) {
					sideCount = 0;
					direction = IslandDirection.POSITIVE_Z;
					sideLength += 2;
					z = -sideLength + sideLength / 2;
					x = z;
				}
				break;
			}
			if (sideCount >= sideLength) sideCount = 0;
		}
	}

	private Location getIslandCentre(int islandNumber) {
		IslandCoordinates islandCoords = getIslandAbsoluteCoordinates(islandNumber);
		return new Location(islandWorld, islandCoords.x + 0.5, skyRoyaleConfig.getIslandY(), islandCoords.z + 0.5);
	}

	public Location getIslandSpawnBoxCentre(int islandNumber) {
		IslandCoordinates islandCoords = getIslandAbsoluteCoordinates(islandNumber);
		return new Location(islandWorld, islandCoords.x + 0.5, skyRoyaleConfig.getIslandSpawnBoxY() + 0.1, islandCoords.z + 0.5);
	}

	public int assignIsland(Player player) {
		int islandNumber = islands.size();

		// Create spawn box for player above island
		Location spawnBoxCentre = getIslandSpawnBoxCentre(islandNumber);
		schematics.pasteSchematic(islandSpawnBox, spawnBoxCentre);

		// Teleport player to spawn box
		player.teleport(spawnBoxCentre);

		// Create island
		int islandRotation = ThreadLocalRandom.current().nextInt(4) * 90;
		LoadedSchematic schematic = mapManager.getRandomMapSchematic();
		SchematicPasteOptions pasteOptions = new SchematicPasteOptions(schematic, getIslandCentre(islandNumber), islandRotation, new ChestFinder(this, islandNumber), skyRoyaleConfig.getDefaultIslandGenerateSpeedTicks());
		schematics.pasteSchematic(pasteOptions);

		// Assign island to player
		islands.add(new SpawnedIsland(islandNumber, islandRotation, getIslandRelativeCoordinates(islandNumber), schematic, player, pasteOptions));
		assignedIslands.put(player, islandNumber);

		return islandNumber;
	}

	public SpawnedIsland getIsland(int islandNumber) {
		return islands.get(islandNumber);
	}

	public boolean unassignIsland(Player player) {
		return unassignIsland(assignedIslands.get(player));
	}
	public boolean unassignIsland(int islandNumber) {
		// Don't unassign if the game has started so we're able to crumble the island properly
		if (gameManager.getGameState() == GameState.STARTED || gameManager.getGameState() == GameState.ENDED) return false;

		// Remove final island schematics
		int finalIndex = islands.size() - 1;
		SpawnedIsland finalIsland = islands.get(finalIndex);
		finalIsland.getSchematicPasteOptions().setCancelled(true);
		schematics.pasteSchematic(new SchematicPasteOptions(finalIsland.getSchematic(), getIslandCentre(finalIndex), finalIsland.getRotation(), new IslandDeleter(), 0));
		schematics.pasteSchematic(new SchematicPasteOptions(islandSpawnBox, getIslandSpawnBoxCentre(finalIndex), 0, new IslandDeleter(), 0));

		// Reassign final island player to the now empty island
		Player finalIslandPlayer = finalIsland.getAssignedPlayer();
		finalIslandPlayer.teleport(getIslandSpawnBoxCentre(islandNumber));
		Player emptyIslandPlayer = islands.get(islandNumber).getAssignedPlayer();
		assignedIslands.put(finalIslandPlayer, islandNumber);
		assignedIslands.remove(emptyIslandPlayer);
		islands.get(islandNumber).assignPlayer(finalIslandPlayer);

		// Clear up final island
		islands.remove(finalIndex);

		return true;
	}

	public IslandCoordinates getIslandRelativeCoordinates(int island) {
		return islandCoordinates[island];
	}

	public IslandCoordinates getIslandAbsoluteCoordinates(int island) {
		IslandCoordinates relativeCoords = getIslandRelativeCoordinates(island);

		return new IslandCoordinates(relativeCoords.x * skyRoyaleConfig.getDistanceBetweenIslands(), relativeCoords.z * skyRoyaleConfig.getDistanceBetweenIslands());
	}

	public void setGlobalIslandGenerateSpeed(int ticksToComplete) {
		islands.forEach(island -> island.getSchematicPasteOptions().setTicksToComplete(ticksToComplete));
	}

	public List<SpawnedIsland> getIslands() {
		return islands;
	}

	public void populateAllIslandChests(LootTier loot) {
		ChestPopulator chestPopulator = new ChestPopulator();
		int itemsPerChest = skyRoyaleConfig.getItemsPerChest();
		islands.forEach(island -> chestPopulator.populate(loot, itemsPerChest, island.getChests()));
	}

	// Crumbles all but the centre island - this is where everyone should be heading towards
	public void crumbleIslands() {
		SpawnedIsland island;
		for (int i = 1; i < islands.size(); i++) {
			island = islands.get(i);

			SchematicPasteOptions pasteOptions = new SchematicPasteOptions(island.getSchematic(), getIslandCentre(i), island.getRotation(), new IslandCrumbler(), skyRoyaleConfig.getIslandCrumbleSpeed());
			schematics.pasteSchematic(pasteOptions);
		}
	}
}
