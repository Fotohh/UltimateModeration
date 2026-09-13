package me.xaxis.ultimatemoderation;

import me.xaxis.ultimatemoderation.lang.LangManager;
import me.xaxis.ultimatemoderation.validation.LangValidator;
import me.xaxis.ultimatemoderation.lang.LangYml;
import me.xaxis.ultimatemoderation.listener.PlayerJoin;
import me.xaxis.ultimatemoderation.loader.PlayerProfileLoader;
import me.xaxis.ultimatemoderation.manager.PlayerProfileManager;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.storage.PlayerProfileStorage;
import me.xaxis.ultimatemoderation.validation.MainConfigValidation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class UltimateModeration extends JavaPlugin {

    private PlayerProfileLoader playerProfileLoader;
    private PlayerProfileManager playerProfileManager;
    private LangManager langManager;
    private BukkitTask profileSaveTask;

    public LangManager getLangManager() {
        return langManager;
    }

    @Override
    public void onEnable() {

        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdirs()) {
                getLogger().severe("Failed to create the necessary directories!");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }

        saveDefaultConfig();

        MainConfigValidation configValidation =
                new MainConfigValidation(
                        getDataFolder().toPath().resolve("config.yml"),
                        getConfig()
                );

        List<String> errors = configValidation.validate();

        if (!errors.isEmpty()) {
            errors.forEach(getLogger()::severe);
            getPluginLoader().disablePlugin(this);
            return;
        }

        File profileFolder = getDataFolder().toPath().resolve("player-data").toFile();
        if (!profileFolder.exists()) {
            if (!profileFolder.mkdirs()) {
                getLogger().severe("Unable to make player_data");
            }
        }

        playerProfileLoader = new PlayerProfileLoader(profileFolder.toPath(), getLogger());
        playerProfileLoader.loadProfilesAsync().whenComplete((profiles, throwable) ->
                getServer().getScheduler().runTask(this, () -> {

                    if (throwable != null) {
                        getLogger().log(Level.SEVERE, "Failed to load profiles!", throwable);
                        return;
                    }

                    onPlayerProfileLoaderCompletion(profiles);

                }));
        //nothing should be written under this loadProfilesAsync()
        //as player profiles are a vital part of the plugin
    }

    private void onPlayerProfileLoaderCompletion(List<PlayerProfile> profiles) {

        PlayerProfileStorage playerProfileStorage = new PlayerProfileStorage(
                getDataFolder().toPath().resolve("player-data"),
                getLogger()
        );
        playerProfileManager = new PlayerProfileManager(profiles, playerProfileStorage, getLogger());

        saveResource("lang.yml", false);
        YamlConfiguration langConfiguration = YamlConfiguration.loadConfiguration(
                getDataFolder().toPath().resolve("lang.yml").toFile()
        );
        LangValidator langValidator = new LangValidator(
                getDataFolder().toPath().resolve("lang.yml"),
                langConfiguration
        );
        List<String> langErrors = langValidator.validate();

        if(!langErrors.isEmpty()) {
            langErrors.forEach(getLogger()::severe);
            getPluginLoader().disablePlugin(this);
            return;
        }

        LangYml langYml = new LangYml(langConfiguration);
        langManager = new LangManager(langYml);

        profileSaveTask = getServer().getScheduler().runTaskTimer(this, () -> playerProfileManager.saveAll(), 0L, 20L * 60 * 5);
        for(Player player : getServer().getOnlinePlayers()) {
            if(!playerProfileManager.hasPlayerProfile(player.getUniqueId())) {
                playerProfileManager.addPlayerProfile(
                        new PlayerProfile(player.getUniqueId(), player.getName(), new ArrayList<>())
                );
            }
        }
        getServer().getPluginManager().registerEvents(new PlayerJoin(playerProfileManager), this);
    }

    @Override
    public void onDisable() {

        if (playerProfileLoader != null) {
            playerProfileLoader.close();
        }

        if (playerProfileManager != null) {
            playerProfileManager.saveAll();
            playerProfileManager.close();
        }

        if(profileSaveTask != null) {
            profileSaveTask.cancel();
        }

    }
}