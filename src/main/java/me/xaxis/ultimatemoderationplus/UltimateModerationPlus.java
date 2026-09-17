package me.xaxis.ultimatemoderationplus;

import me.xaxis.ultimatemoderationplus.commands.KickCommand;
import me.xaxis.ultimatemoderationplus.commands.NoteCommand;
import me.xaxis.ultimatemoderationplus.commands.WarnCommand;
import me.xaxis.ultimatemoderationplus.config.ConfigSettingsLoader;
import me.xaxis.ultimatemoderationplus.config.ConfigSettings;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.LangYml;
import me.xaxis.ultimatemoderationplus.listener.PlayerJoin;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileLoader;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import me.xaxis.ultimatemoderationplus.storage.PlayerProfileStorage;
import me.xaxis.ultimatemoderationplus.validation.LangValidator;
import me.xaxis.ultimatemoderationplus.validation.MainConfigValidation;
import org.bstats.bukkit.Metrics;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

public class UltimateModerationPlus extends JavaPlugin {


    private PlayerProfileLoader playerProfileLoader;
    private PlayerProfileManager playerProfileManager;
    private ConfigSettings configSettings;
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

        ConfigSettingsLoader loader = new ConfigSettingsLoader(getConfig());
        configSettings = loader.load();

        File profileFolder = getDataFolder().toPath().resolve("player-data").toFile();
        if (!profileFolder.exists()) {
            if (!profileFolder.mkdirs()) {
                getLogger().severe("Unable to make player-data folder, disabling plugin!");
                getPluginLoader().disablePlugin(this);
                return;
            }
        }

        playerProfileLoader = new PlayerProfileLoader(profileFolder.toPath(), getLogger());
        playerProfileLoader
                .loadProfilesAsync()
                .whenComplete((profiles, throwable) -> {

                    if (!isEnabled()) {
                        return;
                    }

                    getServer().getScheduler().runTask(
                            this,
                            () -> {
                                if (playerProfileLoader != null) {
                                    playerProfileLoader.close();
                                    playerProfileLoader = null;
                                }

                                if (throwable != null) {
                                    getLogger().log(
                                            Level.SEVERE,
                                            "Failed to load profiles! "
                                                    + "Disabling plugin.",
                                            throwable
                                    );

                                    getPluginLoader()
                                            .disablePlugin(this);
                                    return;
                                }

                                try {
                                    onPlayerProfileLoaderCompletion(
                                            profiles
                                    );
                                } catch (RuntimeException e) {
                                    getLogger().log(
                                            Level.SEVERE,
                                            "Failed to finish initialization. "
                                                    + "Disabling plugin.",
                                            e
                                    );

                                    getPluginLoader().disablePlugin(this);
                                }
                            }
                    );
                });
        //nothing should be written under this loadProfilesAsync()
        //as player profiles are a vital part of the plugin
    }

    private void onPlayerProfileLoaderCompletion(List<PlayerProfile> profiles) {

        PlayerProfileStorage playerProfileStorage = new PlayerProfileStorage(
                getDataFolder().toPath().resolve("player-data"),
                getLogger()
        );
        playerProfileManager = new PlayerProfileManager(profiles, playerProfileStorage, configSettings, getLogger());

        saveResource("lang.yml", false);
        YamlConfiguration langConfiguration = YamlConfiguration.loadConfiguration(
                getDataFolder().toPath().resolve("lang.yml").toFile()
        );
        LangValidator langValidator = new LangValidator(
                getDataFolder().toPath().resolve("lang.yml"),
                langConfiguration
        );
        List<String> langErrors = langValidator.validate();

        if (!langErrors.isEmpty()) {
            langErrors.forEach(getLogger()::severe);
            getPluginLoader().disablePlugin(this);
            return;
        }

        LangYml langYml = new LangYml(langConfiguration);
        langManager = new LangManager(langYml.loadMessages());

        long autoSaveTicks = configSettings.profileAutoSaveInterval();

        profileSaveTask = getServer().getScheduler().runTaskTimer(
                this,
                playerProfileManager::saveAll,
                autoSaveTicks,
                autoSaveTicks
        );

        for (Player player : getServer().getOnlinePlayers()) {
            PlayerProfile existing = playerProfileManager.getPlayerProfile(player.getUniqueId());
            if (existing == null) {
                playerProfileManager.addPlayerProfile(
                        PlayerProfile.create(player.getUniqueId(), player.getName())
                );
            } else if (!existing.playerName().equals(player.getName())) {
                playerProfileManager.changeName(existing, player.getName());
            }
        }

        int bstatsPluginId = 34047;
        new Metrics(this, bstatsPluginId);

        getServer().getPluginManager().registerEvents(new PlayerJoin(playerProfileManager), this);
        getCommand("note").setExecutor(new NoteCommand(langManager, playerProfileManager, configSettings));
        getCommand("warn").setExecutor(new WarnCommand(configSettings, playerProfileManager, langManager));
        getCommand("kick").setExecutor(new KickCommand(langManager, playerProfileManager));
        //todo getCommand("noteuuid").setExecutor(new NoteUUIDCommand(langManager, playerProfileManager));
    }

    @Override
    public void onDisable() {

        if (profileSaveTask != null) {
            profileSaveTask.cancel();
        }

        if (playerProfileManager != null) {
            playerProfileManager.saveAll();
            playerProfileManager.close();
        }

        if (playerProfileLoader != null) {
            playerProfileLoader.close();
        }

    }
}