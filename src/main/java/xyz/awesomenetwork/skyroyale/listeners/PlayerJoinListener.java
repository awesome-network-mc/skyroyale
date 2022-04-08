package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	private final BossBar islandCrumbleBar;

	public PlayerJoinListener(BossBar islandCrumbleBar) {
		this.islandCrumbleBar = islandCrumbleBar;
	}

	@EventHandler
	public void playerJoin(PlayerJoinEvent e) {
		islandCrumbleBar.addPlayer(e.getPlayer());
	}
}
