package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameRunningTimeEvent;

public class GameRunningTimeListener implements Listener {
	@EventHandler
	public void gameRunningTime(GameRunningTimeEvent e) {
		// TODO when game time hits a certain threshold then start crumbling the islands and shrinking the world border
	}
}
