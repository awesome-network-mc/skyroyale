package xyz.awesomenetwork.skyroyale.islands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.SchematicPasteOptions;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.maps.MapManager;

public class IslandManager {
	private final GameManager gameManager;
	private final SchematicHandler schematics;
	private final MapManager mapManager;
	private final World islandWorld;
	private final LoadedSchematic ISLAND_SPAWN_BOX_SCHEMATIC;

	private final int ISLAND_Y, ISLAND_SPAWN_BOX_Y, DISTANCE_BETWEEN_ISLANDS, DEFAULT_ISLAND_GENERATE_SPEED_TICKS;

	private final IslandCoordinates[] islandCoordinates;
	private final ArrayList<SpawnedIsland> islands = new ArrayList<>();

	public IslandManager(GameManager gameManager, SchematicHandler schematics, MapManager mapManager, World islandWorld, String islandSpawnBoxSchematicName, int islandY, int islandSpawnBoxY, int distanceBetweenIslands, int defaultIslandGenerateSpeedTicks, LoadedSchematic spawnBoxSchematic) throws IOException {
		this.gameManager = gameManager;
		this.schematics = schematics;
		this.mapManager = mapManager;
		this.islandWorld = islandWorld;
		ISLAND_SPAWN_BOX_SCHEMATIC = spawnBoxSchematic;
		ISLAND_Y = islandY;
		ISLAND_SPAWN_BOX_Y = islandSpawnBoxY;
		DISTANCE_BETWEEN_ISLANDS = distanceBetweenIslands;
		DEFAULT_ISLAND_GENERATE_SPEED_TICKS = defaultIslandGenerateSpeedTicks;

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
		return new Location(islandWorld, islandCoords.x + 0.5, ISLAND_Y, islandCoords.z + 0.5);
	}

	public Location getIslandSpawnBoxCentre(int islandNumber) {
		IslandCoordinates islandCoords = getIslandAbsoluteCoordinates(islandNumber);
		return new Location(islandWorld, islandCoords.x + 0.5, ISLAND_SPAWN_BOX_Y + 0.1, islandCoords.z + 0.5);
	}

	public int assignIsland(Player player) {
		int islandNumber = islands.size();

		// Create spawn box for player above island
		Location spawnBoxCentre = getIslandSpawnBoxCentre(islandNumber);
		schematics.pasteSchematic(ISLAND_SPAWN_BOX_SCHEMATIC, spawnBoxCentre);

		// Teleport player to spawn box
		player.teleport(spawnBoxCentre);

		// Create island
		int islandRotation = ThreadLocalRandom.current().nextInt(4) * 90;
		LoadedSchematic schematic = mapManager.getRandomMapSchematic();
		SchematicPasteOptions pasteOptions = new SchematicPasteOptions(schematic, getIslandCentre(islandNumber), islandRotation, null, DEFAULT_ISLAND_GENERATE_SPEED_TICKS);
		schematics.pasteSchematic(pasteOptions);

		// Assign island to player
		islands.add(new SpawnedIsland(islandNumber, islandRotation, getIslandRelativeCoordinates(islandNumber), schematic, player, pasteOptions));

		return islandNumber;
	}

	public SpawnedIsland getIsland(int islandNumber) {
		return islands.get(islandNumber);
	}

	public boolean unassignIsland(int islandNumber) {
		// Don't unassign if the game has started so we're able to crumble the island properly
		if (gameManager.getGameState() == GameState.STARTED || gameManager.getGameState() == GameState.ENDED) return false;

		// Reassign island
		int finalIndex = islands.size() - 1;
		SpawnedIsland finalIsland = islands.get(finalIndex);
		islands.set(islandNumber, finalIsland);
		finalIsland.getAssignedPlayer().teleport(getIslandSpawnBoxCentre(islandNumber));
		islands.remove(finalIndex);

		// Cancel final island paste
		finalIsland.getSchematicPasteOptions().setCancelled(true);

		// Remove final island schematics
		schematics.pasteSchematic(new SchematicPasteOptions(finalIsland.getSchematic(), getIslandCentre(finalIndex), finalIsland.getRotation(), new IslandDeleter(), 0));
		schematics.pasteSchematic(new SchematicPasteOptions(ISLAND_SPAWN_BOX_SCHEMATIC, getIslandSpawnBoxCentre(finalIndex), 0, new IslandDeleter(), 0));

		return true;
	}

	public IslandCoordinates getIslandRelativeCoordinates(int island) {
		return islandCoordinates[island];
	}

	public IslandCoordinates getIslandAbsoluteCoordinates(int island) {
		IslandCoordinates relativeCoords = getIslandRelativeCoordinates(island);

		return new IslandCoordinates(relativeCoords.x * DISTANCE_BETWEEN_ISLANDS, relativeCoords.z * DISTANCE_BETWEEN_ISLANDS);
	}

	public void setGlobalIslandGenerateSpeed(int ticksToComplete) {
		islands.forEach(island -> island.getSchematicPasteOptions().setTicksToComplete(ticksToComplete));
	}

	public ArrayList<SpawnedIsland> getIslands() {
		return islands;
	}
}
