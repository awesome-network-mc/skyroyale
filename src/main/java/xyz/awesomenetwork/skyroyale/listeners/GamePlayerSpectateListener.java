package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GamePlayerSpectateEvent;

public class GamePlayerSpectateListener implements Listener {
	private final Location spawnLocation;

	public GamePlayerSpectateListener(World islandWorld, int spawnY) {
		spawnLocation = new Location(islandWorld, 0.5, spawnY, 0.5);
	}

	@EventHandler
	public void gamePlayerSpectate(GamePlayerSpectateEvent e) {
		e.getPlayer().teleport(spawnLocation);
	}
}
