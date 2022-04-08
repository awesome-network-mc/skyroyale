package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;

public class EntityChangeBlockListener implements Listener {
	@EventHandler
	public void entityChangeBlock(EntityChangeBlockEvent e) {
		// Prevent bedrock supply drops from accidentally landing
		e.setCancelled(e.getTo() == Material.BEDROCK);
	}
}
