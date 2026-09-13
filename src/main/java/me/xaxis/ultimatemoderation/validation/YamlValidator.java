package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class YamlValidator {

    protected final Path path;
    protected final FileConfiguration configuration;

    private final int expectedVersion;

    private final List<Consumer<List<String>>> validations =
            new ArrayList<>();

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

        addValidation(this::validateConfigVersion);
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();

        validations.forEach(
                validation -> validation.accept(errors)
        );

        return List.copyOf(errors);
    }

    protected void addValidation(
            Consumer<List<String>> validation
    ) {
        validations.add(
                Objects.requireNonNull(
                        validation,
                        "Validation cannot be null"
                )
        );
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