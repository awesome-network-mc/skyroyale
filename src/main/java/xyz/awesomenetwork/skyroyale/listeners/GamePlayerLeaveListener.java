package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerLeaveEvent;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;
import xyz.awesomenetwork.skyroyale.leaderboard.SkyRoyaleLeaderboard;

public class GamePlayerLeaveListener implements Listener {
	private final GameManager gameManager;
	private final IslandManager islandManager;
	private final SkyRoyaleLeaderboard leaderboard;

	public GamePlayerLeaveListener(GameManager gameManager, IslandManager islandManager, SkyRoyaleLeaderboard leaderboard) {
		this.gameManager = gameManager;
		this.islandManager = islandManager;
		this.leaderboard = leaderboard;
	}

	@EventHandler
	public void gamePlayerLeave(GamePlayerLeaveEvent e) {
		if (gameManager.getGameState() == GameState.PREGAME || gameManager.getGameState() == GameState.COUNTDOWN) {
			// If the game hasn't started, unassign island from player and potentially reassign it to someone else
			islandManager.unassignIsland(e.getPlayer());
		} else if (gameManager.getGameState() == GameState.STARTED) {
			// Leaving and dying are effectively the same thing when autoRespawn is false, so this captures all occurances when players should be set in the scoreboard
			int playersRemaining = gameManager.getIngamePlayers().size();
			leaderboard.setPosition(playersRemaining, e.getPlayer().getName());

			if (playersRemaining == 1) {
				String winner = leaderboard.getPosition(1);
				for (Player player : gameManager.getIngamePlayers()) {
					if (player.getName().contentEquals(winner)) continue;
					winner = player.getName();
					break;
				}
				leaderboard.setPosition(0, winner);
			}
		}
	}
}
