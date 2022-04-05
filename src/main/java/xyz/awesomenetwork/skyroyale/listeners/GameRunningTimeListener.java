package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameRunningTimeEvent;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GameRunningTimeListener implements Listener {
	private final IslandManager islandManager;
	private final int islandCrumbleStartSeconds;

	public GameRunningTimeListener(IslandManager islandManager, int islandCrumbleStartSeconds) {
		this.islandManager = islandManager;
		this.islandCrumbleStartSeconds = islandCrumbleStartSeconds;
	}

	@EventHandler
	public void gameRunningTime(GameRunningTimeEvent e) {
		if (e.getGameRunningTimeInSeconds() == islandCrumbleStartSeconds) {
			islandManager.crumbleIslands();
		}
	}
}
