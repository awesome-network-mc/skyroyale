package xyz.awesomenetwork.skyroyale;

import java.io.IOException;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.GameManagerOptions;
import xyz.awesomenetwork.minigametemplate.MinigameTemplate;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.configs.ChestLootConfig;
import xyz.awesomenetwork.skyroyale.configs.MapsConfig;
import xyz.awesomenetwork.skyroyale.configs.SkyRoyaleConfig;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;
import xyz.awesomenetwork.skyroyale.listeners.BlockBreakListener;
import xyz.awesomenetwork.skyroyale.listeners.EntityDamageListener;
import xyz.awesomenetwork.skyroyale.listeners.GameCountdownListener;
import xyz.awesomenetwork.skyroyale.listeners.GamePlayerJoinListener;
import xyz.awesomenetwork.skyroyale.listeners.GamePlayerLeaveListener;
import xyz.awesomenetwork.skyroyale.listeners.GamePlayerSpectateListener;
import xyz.awesomenetwork.skyroyale.listeners.GameStartListener;
import xyz.awesomenetwork.skyroyale.maps.MapManager;

public class SkyRoyale extends JavaPlugin {
    
    public void onEnable() {
        MinigameTemplate game = this.getServer().getServicesManager().getRegistration(MinigameTemplate.class).getProvider();
        SchematicHandler schematicHandler = getServer().getServicesManager().getRegistration(SchematicHandler.class).getProvider();

        GameManagerOptions options = new GameManagerOptions.GameManagerOptionsBuilder().build();
        GameManager gameManager = game.createGameManager(options);

        SkyRoyaleConfig skyRoyaleConfig = new SkyRoyaleConfig(this);
        MapsConfig mapsConfig = new MapsConfig(this);
        ChestLootConfig chestConfig = new ChestLootConfig(this);

        MapManager mapManager = null;
        try {
            mapManager = new MapManager(mapsConfig, schematicHandler);
        } catch (IOException e) {
            e.printStackTrace();
            getServer().shutdown();
            return;
        }
        getLogger().info("Chosen map: " + mapManager.getLoadedMap().getName() + ", Island schematics: " + mapManager.getLoadedMap().getIslandSchematics().size());

        // Change settings for default world
        World islandWorld = getServer().getWorlds().get(0);
        islandWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        islandWorld.setGameRule(GameRule.DISABLE_RAIDS, true);
        islandWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        islandWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        islandWorld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);

        LoadedSchematic spawnBoxSchematic = null;
        IslandManager islandManager = null;
        try {
            spawnBoxSchematic = schematicHandler.loadSchematic(skyRoyaleConfig.getIslandSpawnBoxSchematicName());
            islandManager = new IslandManager(gameManager, schematicHandler, mapManager, islandWorld, spawnBoxSchematic, chestConfig, skyRoyaleConfig);
        } catch (IOException e) {
            e.printStackTrace();
            getServer().shutdown();
            return;
        }

        getServer().getPluginManager().registerEvents(new BlockBreakListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new EntityDamageListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new GameCountdownListener(islandManager, skyRoyaleConfig.getDefaultIslandGenerateSpeedTicks(), skyRoyaleConfig.getIslandGenerateSpeedMultiplierStart(), skyRoyaleConfig.getIslandGenerateSpeedMultiplierEnd()), this);
        getServer().getPluginManager().registerEvents(new GamePlayerJoinListener(islandManager), this);
        getServer().getPluginManager().registerEvents(new GamePlayerLeaveListener(islandManager), this);
        getServer().getPluginManager().registerEvents(new GamePlayerSpectateListener(islandWorld, skyRoyaleConfig.getIslandSpawnBoxY()), this);
        getServer().getPluginManager().registerEvents(new GameStartListener(this, islandManager, schematicHandler, spawnBoxSchematic, islandWorld), this);
    }
}
