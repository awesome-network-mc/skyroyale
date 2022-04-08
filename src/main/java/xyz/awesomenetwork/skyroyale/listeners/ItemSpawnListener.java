package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class ItemSpawnListener implements Listener {
	@EventHandler
	public void itemSpawn(ItemSpawnEvent e) {
		// If the supply drop bedrock breaks on a half slab, remove the spawned bedrock
		e.setCancelled(e.getEntity().getItemStack().getType() == Material.BEDROCK);
	}
}
