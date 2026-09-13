package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.List;

public final class MainConfigValidation extends YamlValidator{

    public static final String PROFILE_AUTO_SAVE_INTERVAL_PATH =
            "profile-auto-save-interval";

    private static final long MIN_AUTO_SAVE_INTERVAL_SECONDS =
            60L;

    public MainConfigValidation(Path path, FileConfiguration configuration) {
        super(path, configuration, ConfigConstants.MAIN.currentVersion());
    }

    @Override
    protected void validateFile(List<String> errors) {
        validateAutoSaveInterval(errors);
    }

    private void validateAutoSaveInterval(List<String> errors) {
        Object rawValue =
                configuration.get(
                        PROFILE_AUTO_SAVE_INTERVAL_PATH
                );

        if(!(rawValue instanceof Integer)
                && !(rawValue instanceof Long)) {

            errors.add(
                    "The '"
                            + PROFILE_AUTO_SAVE_INTERVAL_PATH
                            + "' must be a whole number of seconds."
            );

            return;
        }

        long autoSaveIntervalSeconds =
                ((Number) rawValue).longValue();

        if(autoSaveIntervalSeconds
                < MIN_AUTO_SAVE_INTERVAL_SECONDS) {

            errors.add(
                    "The '"
                            + PROFILE_AUTO_SAVE_INTERVAL_PATH
                            + "' must be at least "
                            + MIN_AUTO_SAVE_INTERVAL_SECONDS
                            + " seconds."
            );

            return;
        }

        if(autoSaveIntervalSeconds
                > Long.MAX_VALUE / 20L) {

            errors.add(
                    "The '"
                            + PROFILE_AUTO_SAVE_INTERVAL_PATH
                            + "' is too large."
            );
        }
    }
}
