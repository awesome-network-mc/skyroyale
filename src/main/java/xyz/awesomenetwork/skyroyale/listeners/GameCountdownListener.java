package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameCountdownEvent;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GameCountdownListener implements Listener {
	private final IslandManager islandManager;
	private final int defaultIslandGenerateSpeedTicks, islandGenerateSpeedMultiplierStart, islandGenerateSpeedMultiplierEnd;

	public GameCountdownListener(IslandManager islandManager, int defaultIslandGenerateSpeedTicks, int islandGenerateSpeedMultiplierStart, int islandGenerateSpeedMultiplierEnd) {
		this.islandManager = islandManager;
		this.defaultIslandGenerateSpeedTicks = defaultIslandGenerateSpeedTicks;
		this.islandGenerateSpeedMultiplierStart = islandGenerateSpeedMultiplierStart;
		this.islandGenerateSpeedMultiplierEnd = islandGenerateSpeedMultiplierEnd;
	}

	@EventHandler
	public void gameCountdown(GameCountdownEvent e) {
		// Speed up island generation between start and end points
		// When below the minimum bound it's always 0 (negative values are the same as 0)
		long countdownSeconds = e.getCountdownSeconds();
		if (countdownSeconds > islandGenerateSpeedMultiplierStart) return;

		int ticksToComplete = 0;
		if (countdownSeconds >= islandGenerateSpeedMultiplierEnd) {
			ticksToComplete = defaultIslandGenerateSpeedTicks / (int) (islandGenerateSpeedMultiplierStart - islandGenerateSpeedMultiplierEnd) * (int) (countdownSeconds - islandGenerateSpeedMultiplierEnd);
		}

		islandManager.setGlobalIslandGenerateSpeed(ticksToComplete);
	}
}
