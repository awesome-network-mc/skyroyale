package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GamePlayerLeaveEvent;
import xyz.awesomenetwork.skyroyale.MetadataNames;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GamePlayerLeaveListener implements Listener {
	private final IslandManager islandManager;

	public GamePlayerLeaveListener(IslandManager islandManager) {
		this.islandManager = islandManager;
	}

	@EventHandler
	public void gamePlayerLeave(GamePlayerLeaveEvent e) {
		int island = e.getPlayer().getMetadata(MetadataNames.PLAYER_ISLAND.name()).get(0).asInt();
		islandManager.unassignIsland(island);
	}
}
