package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GamePlayerJoinEvent;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GamePlayerJoinListener implements Listener {
	private final IslandManager islandManager;

	public GamePlayerJoinListener(IslandManager islandManager) {
		this.islandManager = islandManager;
	}

	@EventHandler
	public void gamePlayerJoin(GamePlayerJoinEvent e) {
		islandManager.assignIsland(e.getPlayer());
	}
}
