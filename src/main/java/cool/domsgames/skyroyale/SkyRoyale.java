package cool.domsgames.skyroyale;

import org.bukkit.plugin.java.JavaPlugin;

import xyz.awesomenetwork.minigametemplate.GameManagerOptions;
import xyz.awesomenetwork.minigametemplate.MinigameTemplate;

public class SkyRoyale extends JavaPlugin {

    public void onEnable() {
        MinigameTemplate game = this.getServer().getServicesManager().getRegistration(MinigameTemplate.class).getProvider();

        GameManagerOptions options = new GameManagerOptions.GameManagerOptionsBuilder().build();
        game.createGameManager(options);
    }

}
