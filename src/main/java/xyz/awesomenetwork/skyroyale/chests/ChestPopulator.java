package xyz.awesomenetwork.skyroyale.chests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import xyz.awesomenetwork.skyroyale.loot.LootTier;
import xyz.awesomenetwork.skyroyale.loot.WeightedItem;

public class ChestPopulator {
	public void populate(LootTier loot, int itemsPerChest, Block chest) {
		populate(loot, itemsPerChest, Arrays.asList(chest));
	}
	public void populate(LootTier loot, int itemsPerChest, List<Block> blocks) {
		/*
		 * Iterates through each chest to spread the items evenly
		 * Puts "guaranteed" items in the chests first
		*/
		
		int itemsSpawned = 0;
		int chestIndex = 0;
		int totalChests = blocks.size();
		Chest[] chests = new Chest[totalChests];
		for (int i = 0; i < totalChests; i++) chests[i] = (Chest) blocks.get(i).getState();
		int totalItems = totalChests * itemsPerChest;

		for (WeightedItem item : loot.getGuaranteedItems().getItems()) {
			populateRandomChestSlot(chests[chestIndex].getBlockInventory(), item.getItem());
			itemsSpawned++;
			chestIndex++;
			if (chestIndex >= totalChests) chestIndex = 0;
		}

		for (int i = itemsSpawned; i < totalItems; i++) {
			populateRandomChestSlot(chests[chestIndex].getBlockInventory(), loot.getRandomItems().getRandomItem());
			chestIndex++;
			if (chestIndex >= totalChests) chestIndex = 0;
		}
	}

	public void populateRandomChestSlot(Inventory inventory, ItemStack item) {
		// Find available chest slots
		List<Integer> availableSlots = new ArrayList<Integer>();
		int i = 0;
		for (ItemStack inventoryItem : inventory.getStorageContents()) {
			if (inventoryItem == null) availableSlots.add(i);
			i++;
		}

		// Place item in random available slot
		inventory.setItem(availableSlots.get(ThreadLocalRandom.current().nextInt(availableSlots.size())), item);
	}
}
