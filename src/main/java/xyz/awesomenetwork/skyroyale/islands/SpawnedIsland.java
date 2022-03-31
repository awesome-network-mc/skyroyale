package xyz.awesomenetwork.skyroyale.islands;

import org.bukkit.entity.Player;

import xyz.awesomenetwork.schematics.SchematicPasteOptions;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;

public class SpawnedIsland {
	private final int id;
	private final int rotation;
	private final IslandCoordinates relativeCoordinates;
	private final LoadedSchematic schematic;
	private final Player player;
	private final SchematicPasteOptions schematicPasteOptions;

	public SpawnedIsland(int id, int rotation, IslandCoordinates relativeCoordinates, LoadedSchematic schematic, Player player, SchematicPasteOptions schematicPasteOptions) {
		this.id = id;
		this.rotation = rotation;
		this.relativeCoordinates = relativeCoordinates;
		this.schematic = schematic;
		this.player = player;
		this.schematicPasteOptions = schematicPasteOptions;
	}

	public int getIslandNumber() {
		return id;
	}

	public int getRotation() {
		return rotation;
	}

	public IslandCoordinates getRelativeIslandCoordinates() {
		return relativeCoordinates;
	}

	public LoadedSchematic getSchematic() {
		return schematic;
	}

	public Player getAssignedPlayer() {
		return player;
	}

	public SchematicPasteOptions getSchematicPasteOptions() {
		return schematicPasteOptions;
	}
}