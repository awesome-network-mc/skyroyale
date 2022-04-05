package xyz.awesomenetwork.skyroyale.chests;

import xyz.awesomenetwork.skyroyale.loot.WeightedItemList;

public class LootTier {
	private WeightedItemList guaranteedItems, randomItems;

	public LootTier(WeightedItemList guaranteedItems, WeightedItemList randomItems) {
		this.guaranteedItems = guaranteedItems;
		this.randomItems = randomItems;
	}

	public WeightedItemList getGuaranteedItems() {
		return guaranteedItems;
	}

	public WeightedItemList getRandomItems() {
		return randomItems;
	}
}
