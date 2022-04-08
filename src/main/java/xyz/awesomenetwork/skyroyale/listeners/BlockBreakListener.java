package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;

public class BlockBreakListener implements Listener {
	private final GameManager gameManager;

	public BlockBreakListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		// Cancel block breaking before the game starts
		e.setCancelled(gameManager.getGameState() == GameState.PREGAME || gameManager.getGameState() == GameState.COUNTDOWN);
	}
}
