package me.xaxis.ultimatemoderation.validation;

import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;

public final class MainConfigValidation extends YamlValidator{

    private static final int CURRENT_CONFIG_VERSION = 1;

    public MainConfigValidation(Path path, FileConfiguration configuration) {
        super(path, configuration, CURRENT_CONFIG_VERSION);
    }
}
