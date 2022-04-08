package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.boss.BossBar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import xyz.awesomenetwork.minigametemplate.events.GameRunningTimeEvent;

public class GameRunningTimeListener implements Listener {
	private final BossBar islandCrumbleBar;
	private final int crumbleStartSeconds;

	public GameRunningTimeListener(BossBar islandCrumbleBar, int crumbleStartSeconds) {
		this.islandCrumbleBar = islandCrumbleBar;
		this.crumbleStartSeconds = crumbleStartSeconds;
	}

	@EventHandler
	public void gameTimer(GameRunningTimeEvent e) {
		long seconds = e.getGameRunningTimeInSeconds();
		if (seconds > crumbleStartSeconds) return;
		
		seconds = crumbleStartSeconds - seconds;
		long minutes = seconds / 60;
		seconds -= minutes * 60;

		String secondString = seconds >= 10 ? "" + seconds : "0" + seconds;
		String minuteString = minutes >= 10 ? "" + minutes : "0" + minutes;

		islandCrumbleBar.setTitle("Islands crumble in " + minuteString + ":" + secondString);
		islandCrumbleBar.setProgress(0.99 - (0.99 / crumbleStartSeconds * e.getGameRunningTimeInSeconds()));
	}
}
