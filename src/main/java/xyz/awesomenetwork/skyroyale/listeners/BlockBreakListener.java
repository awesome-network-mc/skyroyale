package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;

public class BlockBreakListener implements Listener {
	private final GameManager gameManager;
	private final int buildHeightLimit;

	public BlockBreakListener(GameManager gameManager, int buildHeightLimit) {
		this.gameManager = gameManager;
		this.buildHeightLimit = buildHeightLimit;
	}
	
	@EventHandler
	public void blockBreak(BlockBreakEvent e) {
		// Cancel block breaking before the game starts
		if (gameManager.getGameState() == GameState.PREGAME || gameManager.getGameState() == GameState.COUNTDOWN) {
			e.setCancelled(true);
			return;
		}

		// Don't allow breaking at or above height limit
		e.setCancelled(e.getBlock().getY() >= buildHeightLimit);
	}
}
