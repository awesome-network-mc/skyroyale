package xyz.awesomenetwork.skyroyale.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.skyroyale.loot.WeightedItem;
import xyz.awesomenetwork.skyroyale.loot.WeightedItemList;

public class ChestLootConfig extends YamlConfigBase {
	private final String TIERS_KEY = "tiers";
	private final String SERIALISED_KEY = "serialised";
	private final String WEIGHT_KEY = "weight";

	private final String GUARANTEED_ITEMS_KEY = "guaranteed";
	private final String WEIGHTED_ITEMS_KEY = "random";

	private final HashMap<Integer, HashMap<String, WeightedItemList>> weightedItems = new HashMap<>();

	public ChestLootConfig(JavaPlugin plugin) {
		super(plugin, "chestloot.yml", true);

		ConfigurationSection tiersSection = getConfig().getConfigurationSection(TIERS_KEY);
		tiersSection.getKeys(false).forEach(tierId -> {
			final int tier = Integer.parseInt(tierId);

			ConfigurationSection tierIdSection = tiersSection.getConfigurationSection(tierId);
			tierIdSection.getKeys(false).forEach(itemSection -> {
				WeightedItemList items = readConfigSection(tier, itemSection);
				
				if (!weightedItems.containsKey(tier)) weightedItems.put(tier, new HashMap<>());
				weightedItems.get(tier).put(itemSection, items);
			});
		});
	}

	private WeightedItemList readConfigSection(int tier, String itemSection) {
		ArrayList<WeightedItem> items = new ArrayList<>();
		Gson gson = new Gson();

		ConfigurationSection tierSection = getConfig().getConfigurationSection(TIERS_KEY + "." + tier);
		tierSection.getMapList(itemSection).forEach(itemInfo -> {
			String serialisedItemJson = itemInfo.get(SERIALISED_KEY).toString();
			Map<String, Object> serialisedItemMap = gson.fromJson(serialisedItemJson, Map.class);
			
			ItemStack item = ItemStack.deserialize(serialisedItemMap);
			int weight = itemInfo.containsKey(WEIGHT_KEY) ? (int) itemInfo.get(WEIGHT_KEY) : 1;

			items.add(new WeightedItem(item, weight));
		});

		return new WeightedItemList(items);
	}

	private WeightedItemList getTierItemSection(int tier, String itemSection) {
		if (!weightedItems.containsKey(tier)) return null;
		if (!weightedItems.get(tier).containsKey(itemSection)) return null;

		return weightedItems.get(tier).get(itemSection);
	}

	public WeightedItemList getTierGuaranteedItems(int tier) {
		return getTierItemSection(tier, GUARANTEED_ITEMS_KEY);
	}

	public WeightedItemList getTierRandomItems(int tier) {
		return getTierItemSection(tier, WEIGHTED_ITEMS_KEY);
	}
}
