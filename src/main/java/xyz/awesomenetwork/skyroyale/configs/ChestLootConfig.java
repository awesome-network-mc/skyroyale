package xyz.awesomenetwork.skyroyale.configs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.skyroyale.loot.LootTier;
import xyz.awesomenetwork.skyroyale.loot.WeightedItem;
import xyz.awesomenetwork.skyroyale.loot.WeightedItemList;

public class ChestLootConfig extends YamlConfigBase {
	private final String TIERS_KEY = "tiers";
	private final String SERIALISED_KEY = "serialised";
	private final String WEIGHT_KEY = "weight";

	private final String GUARANTEED_ITEMS_KEY = "guaranteed";
	private final String RANDOM_ITEMS_KEY = "random";

	private final Map<Integer, Map<String, WeightedItemList>> weightedItems = new HashMap<>();

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
		List<WeightedItem> items = new ArrayList<>();
		Gson gson = new Gson();

		// Load all the items from this tier's section (should be "guaranteed" or "random")
		ConfigurationSection tierSection = getConfig().getConfigurationSection(TIERS_KEY + "." + tier);
		tierSection.getMapList(itemSection).forEach(itemInfo -> {
			String serialisedItemJson = itemInfo.get(SERIALISED_KEY).toString();
			Map<String, Object> serialisedItemMap = (Map<String, Object>) gson.fromJson(serialisedItemJson, Map.class); // Serialised item is stored as JSON because it's so much nicer than YAML
			
			ItemStack item = ItemStack.deserialize(serialisedItemMap);
			int weight = itemInfo.containsKey(WEIGHT_KEY) ? Integer.parseInt(itemInfo.get(WEIGHT_KEY).toString()) : 1; // Parse int here because maven fails to compile otherwise, it really doesn't like converting Object to int but very intermittently

			items.add(new WeightedItem(item, weight));
		});

		return new WeightedItemList(items);
	}

	public LootTier getTier(int tier) {
		WeightedItemList guaranteedItems = weightedItems.get(tier).get(GUARANTEED_ITEMS_KEY);
		WeightedItemList randomItems = weightedItems.get(tier).get(RANDOM_ITEMS_KEY);

		return new LootTier(guaranteedItems, randomItems);
	}
}
