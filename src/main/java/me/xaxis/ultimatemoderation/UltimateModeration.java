package me.xaxis.ultimatemoderation;

import me.xaxis.ultimatemoderation.config.ConfigValidation;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class UltimateModeration extends JavaPlugin {

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()){
            if(!getDataFolder().mkdirs()) {
                getLogger().severe("Failed to create the necessary directories!");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }

        saveDefaultConfig();

        ConfigValidation configValidation = new ConfigValidation(getDataFolder().toPath().resolve("config.yml"), getConfig());
        List<String> errors = configValidation.validate();
        if(!errors.isEmpty()) {
            errors.forEach(error -> getLogger().severe(error));
            getPluginLoader().disablePlugin(this);
            return;
        }

    }

    @Override
    public void onDisable() {

    }
}