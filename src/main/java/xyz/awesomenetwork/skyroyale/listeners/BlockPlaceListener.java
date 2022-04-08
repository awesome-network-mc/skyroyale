package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {
	private final int buildHeightLimit;

	public BlockPlaceListener(int buildHeightLimit) {
		this.buildHeightLimit = buildHeightLimit;
	}

	@EventHandler
	public void blockPlace(BlockPlaceEvent e) {
		// Don't allow placing blocks above the height limit - mostly because of the supply drop spawn positions
		e.setCancelled(e.getBlock().getLocation().getBlockY() >= buildHeightLimit);
	}
}
