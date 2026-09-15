package me.xaxis.ultimatemoderationplus.validation;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class YamlValidator {

    protected final Path path;
    protected final FileConfiguration configuration;

    private final int expectedVersion;

    public final List<String> validate() {
        List<String> errors = new ArrayList<>();

        validateConfigVersion(errors);
        validateFile(errors);

        return List.copyOf(errors);
    }

    protected abstract void validateFile(
            List<String> errors
    );

    public YamlValidator(
            Path path,
            FileConfiguration configuration,
            int expectedVersion
    ) {
        this.configuration = Objects.requireNonNull(
                configuration,
                "Configuration cannot be null"
        );

        this.path = Objects.requireNonNull(
                path,
                "Path cannot be null"
        );

        if(expectedVersion <= 0) {
            throw new IllegalArgumentException(
                    "Expected config version must be greater than 0, got "
                            + expectedVersion
            );
        }

        this.expectedVersion = expectedVersion;
    }

    private void validateConfigVersion(List<String> errors) {

        if(!configuration.isSet(ConfigConstants.CONFIG_VERSION_PATH)) {
            errors.add(
                    "Missing " + ConfigConstants.CONFIG_VERSION_PATH + " in "
                            + path.getFileName()
            );
            return;
        }

        if(!configuration.isInt(ConfigConstants.CONFIG_VERSION_PATH)) {
            errors.add(
                    ConfigConstants.CONFIG_VERSION_PATH + " must be an integer in "
                            + path.getFileName()
            );
            return;
        }

        int version =
                configuration.getInt(ConfigConstants.CONFIG_VERSION_PATH);

        if(version != expectedVersion) {
            errors.add(
                    "Expected " + ConfigConstants.CONFIG_VERSION_PATH + ": "
                            + expectedVersion
                            + ", found "
                            + version
                            + " in "
                            + path.getFileName()
            );
        }
    }
}