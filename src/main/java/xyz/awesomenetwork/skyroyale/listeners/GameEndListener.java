package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameEndEvent;
import xyz.awesomenetwork.skyroyale.leaderboard.SkyRoyaleLeaderboard;

public class GameEndListener implements Listener {
	private final Server server;
	private final SkyRoyaleLeaderboard leaderboard;

	public GameEndListener(Server server, SkyRoyaleLeaderboard leaderboard) {
		this.server = server;
		this.leaderboard = leaderboard;
	}

	@EventHandler
	public void gameEnd(GameEndEvent e) {
		server.broadcastMessage("Podium:");
		for (int i = 0; i < 3; i++) {
			String name;
			if ((name = leaderboard.getPosition(i)) != null) server.broadcastMessage(" " + (i + 1) + ": " + name);
		}
	}
}
