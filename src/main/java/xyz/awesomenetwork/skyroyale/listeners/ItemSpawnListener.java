package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class ItemSpawnListener implements Listener {
	@EventHandler
	public void itemSpawn(ItemSpawnEvent e) {
		e.setCancelled(e.getEntity().getItemStack().getType() == Material.BEDROCK);
	}
}
