package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.events.GamePlayerDeathEvent;

public class GamePlayerDeathListener implements Listener {
	private final GameManager gameManager;

	public GamePlayerDeathListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler
	public void gamePlayerDeath(GamePlayerDeathEvent e) {
		int playersRemaining = gameManager.getIngamePlayers().size() - 1;
		e.setDeathMessage(e.getDeathMessage() + ChatColor.GRAY + " (" + playersRemaining + "/" + gameManager.getOptions().maxPlayers + ")");
	}
}
