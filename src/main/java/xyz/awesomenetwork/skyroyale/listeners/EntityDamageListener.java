package xyz.awesomenetwork.skyroyale.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.enums.GameState;

public class EntityDamageListener implements Listener {
	private final GameManager gameManager;

	public EntityDamageListener(GameManager gameManager) {
		this.gameManager = gameManager;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void entityDamage(EntityDamageEvent e) {
		if (e.getCause() != DamageCause.FALL) return;
		if (gameManager.getGameState() != GameState.STARTED) return;
		if (gameManager.getGameRunningTimeInSeconds() > 2) return;

		e.setCancelled(true);
	}
}
