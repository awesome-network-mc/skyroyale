package xyz.awesomenetwork.skyroyale.chests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;

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
		if (block.getType() == Material.CHEST) {
			islandManager.getIsland(islandNumber).addChest((Chest) block.getState());
		}
	}
}
