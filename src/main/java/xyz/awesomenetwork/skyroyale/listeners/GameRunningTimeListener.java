package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameRunningTimeEvent;
import xyz.awesomenetwork.skyroyale.configs.SkyRoyaleConfig;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;

public class GameRunningTimeListener implements Listener {
	private final IslandManager islandManager;
	private final World islandWorld;
	private final SkyRoyaleConfig skyRoyaleConfig;

	public GameRunningTimeListener(IslandManager islandManager, World islandWorld, SkyRoyaleConfig skyRoyaleConfig) {
		this.islandManager = islandManager;
		this.islandWorld = islandWorld;
		this.skyRoyaleConfig = skyRoyaleConfig;
	}

	@EventHandler
	public void gameRunningTime(GameRunningTimeEvent e) {
		if (e.getGameRunningTimeInSeconds() == skyRoyaleConfig.getBaseIslandCrumbleStartSeconds()) {
			islandManager.crumbleIslands();

			// Set world border to just outside the centre island
			islandWorld.getWorldBorder().setSize(skyRoyaleConfig.getDistanceBetweenIslands() * 2, skyRoyaleConfig.getIslandCrumbleSpeed() / 20);
		}
	}
}
