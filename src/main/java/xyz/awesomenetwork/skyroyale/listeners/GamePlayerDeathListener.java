package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerDeathEvent;
import xyz.awesomenetwork.skyroyale.leaderboard.SkyRoyaleLeaderboard;

public class GamePlayerDeathListener implements Listener {
	private final GameManager gameManager;
	private final SkyRoyaleLeaderboard leaderboard;

	public GamePlayerDeathListener(GameManager gameManager, SkyRoyaleLeaderboard leaderboard) {
		this.gameManager = gameManager;
		this.leaderboard = leaderboard;
	}

	@EventHandler
	public void gamePlayerDeath(GamePlayerDeathEvent e) {
		int playersRemaining = gameManager.getIngamePlayers().size() - 1;
		e.setDeathMessage(e.getDeathMessage() + ChatColor.GRAY + " (" + playersRemaining + "/" + gameManager.getOptions().maxPlayers + ")");

		leaderboard.setPosition(playersRemaining, e.getVictim().getName());
		if (playersRemaining == 1) {
			String winner = gameManager.getIngamePlayers().iterator().next().getName();
			leaderboard.setPosition(0, winner);
		}
	}
}
