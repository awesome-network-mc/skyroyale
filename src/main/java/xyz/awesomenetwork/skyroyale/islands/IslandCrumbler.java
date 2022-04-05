package xyz.awesomenetwork.skyroyale.islands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;

import xyz.awesomenetwork.schematics.SchematicPasteCallback;
import xyz.awesomenetwork.schematics.data.LocationNoWorld;

public class IslandCrumbler implements SchematicPasteCallback {
	private final BlockData AIR = Material.AIR.createBlockData();

	@Override
	public boolean prePaste(String pasteId, BlockData blockData, Location pasteCentre, Location absoluteLocation, LocationNoWorld relativeLocation) {
		World world = absoluteLocation.getWorld();
		world.setBlockData(absoluteLocation, AIR);
		world.spawnFallingBlock(absoluteLocation, blockData);
		return false;
	}
}
