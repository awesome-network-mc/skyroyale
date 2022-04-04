package xyz.awesomenetwork.skyroyale.islands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.SchematicPasteCallback;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public class IslandDeleter implements SchematicPasteCallback {
	private final BlockData AIR = Material.AIR.createBlockData();

	@Override
	public boolean blockPaste(String pasteId, Block block, BlockData blockdata, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
		block.setBlockData(AIR);
		return false;
	}
}
