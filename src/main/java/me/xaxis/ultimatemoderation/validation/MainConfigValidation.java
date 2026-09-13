package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.List;

public final class MainConfigValidation extends YamlValidator{

    public MainConfigValidation(Path path, FileConfiguration configuration) {
        super(path, configuration, ConfigConstants.MAIN.currentVersion());

        addValidation(this::validateAutoSaveInterval);
    }

    private void validateAutoSaveInterval(List<String> errors) {
        if(!configuration.isSet("profile-auto-save-interval")) {
            errors.add("The 'profile-auto-save-interval' is not set in the configuration.");
            return;
        }

        if(!configuration.isLong("profile-auto-save-interval")) {
            errors.add("The 'profile-auto-save-interval' must be a number.");
            return;
        }

        long autoSaveInterval = configuration.getLong("profile-auto-save-interval");
        if (autoSaveInterval < 20 * 60) { // 20 ticks * 60 seconds = 1 minute
            errors.add("The 'profile-auto-save-interval' must be greater than 1 minute.");
        }
    }
}
