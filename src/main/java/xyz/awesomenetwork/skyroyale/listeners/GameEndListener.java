package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.Server;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
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
		String[] podium = new String[] {
			ChatColor.AQUA + "❶",
			ChatColor.DARK_AQUA + "❷",
			ChatColor.BLUE + "❸"
		};

		server.broadcastMessage(ChatColor.GRAY + "≡≡≡ " + ChatColor.WHITE + "LEADERBOARD" + ChatColor.GRAY + " ≡≡≡");
		server.broadcastMessage("");
		for (int i = 0; i < podium.length; i++) {
			String name;
			if ((name = leaderboard.getPosition(i)) != null && i < podium.length) {
				server.broadcastMessage(" " + podium[i] + "  " + name);
				
			}
		}
		server.broadcastMessage("");
		server.broadcastMessage(ChatColor.GRAY + "≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡≡");
	}
}
