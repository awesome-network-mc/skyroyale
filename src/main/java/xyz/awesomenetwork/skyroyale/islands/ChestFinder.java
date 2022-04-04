package xyz.awesomenetwork.skyroyale.islands;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.SchematicPasteCallback;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public class ChestFinder implements SchematicPasteCallback {
	private final IslandManager islandManager;
	private final int islandNumber;

	public ChestFinder(IslandManager islandManager, int islandNumber) {
		this.islandManager = islandManager;
		this.islandNumber = islandNumber;
	}

	@Override
	public boolean blockPaste(String pasteId, Block block, BlockData blockData, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
		if (block instanceof Chest) {
			islandManager.getIsland(islandNumber).addChest(((Chest) block).getBlockInventory());
		}
		return true;
	}
}
