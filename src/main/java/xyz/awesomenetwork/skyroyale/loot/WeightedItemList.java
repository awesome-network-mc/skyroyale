package xyz.awesomenetwork.skyroyale.loot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.inventory.ItemStack;

public class WeightedItemList {
	private final List<WeightedItem> weightedItems;
	private final ArrayList<ItemStack> calculatedItems = new ArrayList<>();

	private int calculatedItemsSize;

	public WeightedItemList(List<WeightedItem> weightedItems) {
		this.weightedItems = weightedItems;

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