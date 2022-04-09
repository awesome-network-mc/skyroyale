package xyz.awesomenetwork.skyroyale.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.inventory.ItemStack;

public class WeightedItemList {
	private final List<WeightedItem> weightedItems;
	private final List<ItemStack> calculatedItems = new ArrayList<>();

	private int calculatedItemsSize;

	public WeightedItemList(List<WeightedItem> weightedItems) {
		this.weightedItems = weightedItems;

		// I feel like there's a better way to calculate item weights than this but this works for now
		weightedItems.forEach(item -> {
			calculatedItemsSize += item.getWeight();
			for (int i = 0; i < item.getWeight(); i++) calculatedItems.add(item.getItem());
		});
	}

	public List<WeightedItem> getItems() {
		return weightedItems;
	}

	public ItemStack getRandomItem() {
		return calculatedItems.get(ThreadLocalRandom.current().nextInt(calculatedItemsSize));
	}
}
