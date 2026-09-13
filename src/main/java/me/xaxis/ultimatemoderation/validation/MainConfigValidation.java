package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;

public final class MainConfigValidation extends YamlValidator{

    public MainConfigValidation(Path path, FileConfiguration configuration) {
        super(path, configuration, ConfigConstants.MAIN.currentVersion());
    }
}
