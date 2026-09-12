package me.xaxis.ultimatemoderation.validation;

import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class YamlValidator {

    protected static final String CONFIG_VERSION_PATH = "config-version";

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

        if(!configuration.contains(CONFIG_VERSION_PATH)) {
            errors.add(
                    "Missing config-version in "
                            + path.getFileName()
            );
            return;
        }

        if(!configuration.isInt(CONFIG_VERSION_PATH)) {
            errors.add(
                    "config-version must be an integer in "
                            + path.getFileName()
            );
            return;
        }

        int version =
                configuration.getInt(CONFIG_VERSION_PATH);

        if(version != expectedVersion) {
            errors.add(
                    "Expected config version "
                            + expectedVersion
                            + ", found "
                            + version
                            + " in "
                            + path.getFileName()
            );
        }
    }
}