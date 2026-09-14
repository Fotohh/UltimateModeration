package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.List;

public final class MainConfigValidation extends YamlValidator {

    private static final String PROFILE_AUTO_SAVE_INTERVAL_PATH =
            "profile-auto-save-interval";

    private static final String NOTE_MAX_CONTENT_LENGTH_PATH =
            "note-max-content-length";

    private static final long MIN_AUTO_SAVE_INTERVAL_TICKS = 20L * 60L;

    public MainConfigValidation(Path path, FileConfiguration configuration) {
        super(path, configuration, ConfigConstants.MAIN.currentVersion());
    }

    @Override
    protected void validateFile(List<String> errors) {
        validateAutoSaveInterval(errors);
        validateNoteMaxContentLength(errors);
    }

    private void validateNoteMaxContentLength(List<String> errors) {

        if (!configuration.isSet(NOTE_MAX_CONTENT_LENGTH_PATH)) {
            errors.add(
                    "The '" + NOTE_MAX_CONTENT_LENGTH_PATH + "' is missing."
                            + " in the configuration file. Please add it with a value of at least 1."
            );
            return;
        }

        Object rawValue = configuration.get(NOTE_MAX_CONTENT_LENGTH_PATH);

        if (!(rawValue instanceof Integer) && !(rawValue instanceof Long)) {

            errors.add(
                    "The '" + NOTE_MAX_CONTENT_LENGTH_PATH + "' must be a whole number."
            );

            return;
        }

        long noteMaxContentLength = ((Number) rawValue).longValue();

        if (noteMaxContentLength < 1) {

            errors.add(
                    "The '" + NOTE_MAX_CONTENT_LENGTH_PATH + "' must be at least 1."
            );
        }
    }

    private void validateAutoSaveInterval(List<String> errors) {

        if (!configuration.isSet(PROFILE_AUTO_SAVE_INTERVAL_PATH)) {
            errors.add(
                    "The '" + PROFILE_AUTO_SAVE_INTERVAL_PATH + "' is missing."
                            + " in the configuration file. Please add it with a value of at least "
                            + MIN_AUTO_SAVE_INTERVAL_TICKS + " seconds."
            );
            return;
        }

        Object rawValue = configuration.get(PROFILE_AUTO_SAVE_INTERVAL_PATH);

        if (!(rawValue instanceof Integer) && !(rawValue instanceof Long)) {

            errors.add(
                    "The '" + PROFILE_AUTO_SAVE_INTERVAL_PATH + "' must be a whole number of seconds."
            );

            return;
        }

        long autoSaveIntervalSeconds = ((Number) rawValue).longValue();

        if (autoSaveIntervalSeconds < MIN_AUTO_SAVE_INTERVAL_TICKS) {

            errors.add(
                    "The '" + PROFILE_AUTO_SAVE_INTERVAL_PATH + "' must be at least "
                            + MIN_AUTO_SAVE_INTERVAL_TICKS + " seconds."
            );

            return;
        }

        if (autoSaveIntervalSeconds > Long.MAX_VALUE / 20L) {

            errors.add(
                    "The '" + PROFILE_AUTO_SAVE_INTERVAL_PATH + "' is too large."
            );
        }
    }

}
