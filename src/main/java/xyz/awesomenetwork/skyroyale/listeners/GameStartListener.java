package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameStartEvent;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.SchematicPasteOptions;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.islands.IslandDeleter;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GameStartListener implements Listener {
	private final IslandManager islandManager;
	private final SchematicHandler schematicHandler;
	private final LoadedSchematic ISLAND_SPAWN_BOX_SCHEMATIC;

	private final IslandDeleter islandDeleter = new IslandDeleter();

	public GameStartListener(IslandManager islandManager, SchematicHandler schematicHandler, LoadedSchematic islandSpawnBox) {
		this.islandManager = islandManager;
		this.schematicHandler = schematicHandler;
		this.ISLAND_SPAWN_BOX_SCHEMATIC = islandSpawnBox;
	}

	@EventHandler
	public void gameStart(GameStartEvent e) {
		islandManager.getIslands().forEach(island -> {
			schematicHandler.pasteSchematic(new SchematicPasteOptions(ISLAND_SPAWN_BOX_SCHEMATIC, islandManager.getIslandSpawnBoxCentre(island.getIslandNumber()), 0, islandDeleter, 0));
		});
	}
}
