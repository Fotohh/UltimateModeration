package me.xaxis.ultimatemoderation.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;

public class ConfigSettingsLoader {

    private final FileConfiguration configuration;

    public ConfigSettingsLoader(FileConfiguration configuration) {
        this.configuration = Objects.requireNonNull(configuration, "Configuration cannot be null");
    }

    private long getProfileAutoSaveInterval() {
        return configuration.getLong("profile-auto-save-interval");
    }

    private long getNoteMaxContentLength() {
        return configuration.getLong("note-max-content-length");
    }

    public ConfigSettings load() {
        return new ConfigSettings(
                getProfileAutoSaveInterval(),
                getNoteMaxContentLength()
        );
    }


}
