package xyz.awesomenetwork.skyroyale;

import java.io.IOException;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.GameManager;
import xyz.awesomenetwork.minigametemplate.GameManagerOptions;
import xyz.awesomenetwork.minigametemplate.MinigameTemplate;
import xyz.awesomenetwork.schematics.SchematicHandler;
import xyz.awesomenetwork.schematics.data.LoadedSchematic;
import xyz.awesomenetwork.skyroyale.configs.MapsConfig;
import xyz.awesomenetwork.skyroyale.configs.SkyRoyaleConfig;
import xyz.awesomenetwork.skyroyale.islands.IslandManager;
import xyz.awesomenetwork.skyroyale.listeners.BlockBreakListener;
import xyz.awesomenetwork.skyroyale.listeners.GameCountdownListener;
import xyz.awesomenetwork.skyroyale.listeners.GamePlayerJoinListener;
import xyz.awesomenetwork.skyroyale.listeners.GamePlayerLeaveListener;
import xyz.awesomenetwork.skyroyale.listeners.GamePlayerSpectateListener;
import xyz.awesomenetwork.skyroyale.listeners.GameRunningTimeListener;
import xyz.awesomenetwork.skyroyale.listeners.GameStartListener;
import xyz.awesomenetwork.skyroyale.maps.MapManager;
import xyz.awesomenetwork.voidgenerator.VoidGenerator;

public class SkyRoyale extends JavaPlugin {
    
    public void onEnable() {
        MinigameTemplate game = this.getServer().getServicesManager().getRegistration(MinigameTemplate.class).getProvider();
        SchematicHandler schematicHandler = getServer().getServicesManager().getRegistration(SchematicHandler.class).getProvider();

        GameManagerOptions options = new GameManagerOptions.GameManagerOptionsBuilder().build();
        GameManager gameManager = game.createGameManager(options);

        SkyRoyaleConfig skyRoyaleConfig = new SkyRoyaleConfig(this);
        MapsConfig mapsConfig = new MapsConfig(this);

        MapManager mapManager = null;
        try {
            mapManager = new MapManager(mapsConfig, schematicHandler);
        } catch (IOException e) {
            e.printStackTrace();
            getServer().shutdown();
            return;
        }

        // Generate islands world
        // NOTE this world could be pre-generated and become the default world in future, however it's easier for now to just generate on server startup
        WorldCreator islandWorldCreator = new WorldCreator(skyRoyaleConfig.getIslandsWorldName());
        islandWorldCreator.environment(mapManager.getLoadedMap().getEnvironment());
        islandWorldCreator.generator(new VoidGenerator());
        islandWorldCreator.generateStructures(false);
        World islandWorld = getServer().createWorld(islandWorldCreator);
        islandWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        islandWorld.setGameRule(GameRule.DISABLE_RAIDS, true);
        islandWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        islandWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        islandWorld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);

        LoadedSchematic spawnBoxSchematic = null;
        IslandManager islandManager = null;
        try {
            spawnBoxSchematic = schematicHandler.loadSchematic(skyRoyaleConfig.getIslandSpawnBoxSchematicName());
            islandManager = new IslandManager(gameManager, schematicHandler, mapManager, islandWorld, skyRoyaleConfig.getIslandSpawnBoxSchematicName(), skyRoyaleConfig.getIslandY(), skyRoyaleConfig.getIslandSpawnBoxY(), skyRoyaleConfig.getDistanceBetweenIslands(), skyRoyaleConfig.getDefaultIslandGenerateSpeedTicks(), spawnBoxSchematic);
        } catch (IOException e) {
            e.printStackTrace();
            getServer().shutdown();
            return;
        }

        getServer().getPluginManager().registerEvents(new BlockBreakListener(gameManager), this);
        getServer().getPluginManager().registerEvents(new GameCountdownListener(islandManager, skyRoyaleConfig.getDefaultIslandGenerateSpeedTicks(), skyRoyaleConfig.getIslandGenerateSpeedMultiplierStart(), skyRoyaleConfig.getIslandGenerateSpeedMultiplierEnd()), this);
        getServer().getPluginManager().registerEvents(new GamePlayerJoinListener(islandManager, this), this);
        getServer().getPluginManager().registerEvents(new GamePlayerLeaveListener(islandManager), this);
        getServer().getPluginManager().registerEvents(new GamePlayerSpectateListener(islandWorld, skyRoyaleConfig.getIslandSpawnBoxY()), this);
        getServer().getPluginManager().registerEvents(new GameRunningTimeListener(), this);
        getServer().getPluginManager().registerEvents(new GameStartListener(islandManager, schematicHandler, spawnBoxSchematic), this);
    }
}
