package xyz.awesomenetwork.skyroyale.chests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import xyz.awesomenetwork.schematics.SchematicPasteCallback;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class ChestFinder implements SchematicPasteCallback {
	private final IslandManager islandManager;
	private final int islandNumber;

	public ChestFinder(IslandManager islandManager, int islandNumber) {
		this.islandManager = islandManager;
		this.islandNumber = islandNumber;
	}

	@Override
	public void postPaste(String pasteId, Block block, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
		// When an island is pasting, this finds the chests and adds them to the spawned island instance
		if (block.getType() == Material.CHEST) {
			islandManager.getIsland(islandNumber).addChest(block);
		}
	}
}
