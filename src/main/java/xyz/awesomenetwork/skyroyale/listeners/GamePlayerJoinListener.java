package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

import xyz.awesomenetwork.minigametemplate.events.GamePlayerJoinEvent;
import xyz.awesomenetwork.skyroyale.MetadataNames;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GamePlayerJoinListener implements Listener {
	private final IslandManager islandManager;
	private final Plugin plugin;

	public GamePlayerJoinListener(IslandManager islandManager, Plugin plugin) {
		this.islandManager = islandManager;
		this.plugin = plugin;
	}

	@EventHandler
	public void gamePlayerJoin(GamePlayerJoinEvent e) {
		int island = islandManager.assignIsland(e.getPlayer());
		e.getPlayer().setMetadata(MetadataNames.PLAYER_ISLAND.name(), new FixedMetadataValue(plugin, island));
	}
}
