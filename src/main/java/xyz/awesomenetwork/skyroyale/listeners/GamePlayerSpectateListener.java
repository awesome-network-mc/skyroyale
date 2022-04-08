package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
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
		Player player = e.getPlayer();
		player.setGameMode(GameMode.SPECTATOR);
		// If player is in the void then teleport them back up for convenience
		if (player.getLocation().getBlockY() < 0) e.getPlayer().teleport(spawnLocation);
	}
}
