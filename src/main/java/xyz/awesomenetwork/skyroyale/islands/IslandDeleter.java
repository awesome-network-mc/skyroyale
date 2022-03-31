package xyz.awesomenetwork.skyroyale.islands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.SchematicPasteCallback;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public class IslandDeleter implements SchematicPasteCallback {
	private final BlockData AIR = Material.AIR.createBlockData();

	@Override
	public boolean blockPaste(String pasteId, BlockData block, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
		absoluteLocation.getWorld().setBlockData(absoluteLocation, AIR);
		return false;
	}
}
