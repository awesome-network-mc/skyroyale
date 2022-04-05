package xyz.awesomenetwork.skyroyale.chests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.awesomenetwork.skyroyale.loot.WeightedItem;

public class ChestPopulator {
	public void populate(LootTier loot, int itemsPerChest, List<Chest> chests) {
		int itemsSpawned = 0;
		int chestIndex = 0;
		int totalChests = chests.size();
		int totalItems = totalChests * itemsPerChest;

		for (WeightedItem item : loot.getGuaranteedItems().getItems()) {
			populateRandomChestSlot(chests.get(chestIndex).getBlockInventory(), item.getItem());
			itemsSpawned++;
			chestIndex++;
			if (chestIndex >= totalChests) chestIndex = 0;
		}

		for (int i = itemsSpawned; i < totalItems; i++) {
			populateRandomChestSlot(chests.get(chestIndex).getBlockInventory(), loot.getRandomItems().getRandomItem());
			chestIndex++;
			if (chestIndex >= totalChests) chestIndex = 0;
		}
	}

	public void populateRandomChestSlot(Inventory inventory, ItemStack item) {
		// Find available chest slots
		ArrayList<Integer> availableSlots = new ArrayList<Integer>();
		int i = 0;
		for (ItemStack inventoryItem : inventory.getStorageContents()) {
			if (inventoryItem == null) availableSlots.add(i);
			i++;
		}

		// Place item in random available slot
		inventory.setItem(availableSlots.get(ThreadLocalRandom.current().nextInt(availableSlots.size())), item);
	}
}
