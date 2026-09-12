package me.xaxis.ultimatemoderation.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ConfigValidation {

    private static final int CURRENT_CONFIG_VERSION = 1;

    private final Path path;
    private final FileConfiguration configuration;

    public ConfigValidation (Path path, FileConfiguration configuration) {
        this.configuration = Objects.requireNonNull(
                configuration,
                "Configuration cannot be null");
        this.path = Objects.requireNonNull(
                path,
                "Path cannot be null");
    }

    public List<String> validate() {
        List<String> errors = new ArrayList<>();
        validateConfigVersion(errors);

        return List.copyOf(errors);
    }

    private void validateConfigVersion(List<String> errors) {

        if(!configuration.contains("config-version")) {
            errors.add("Missing config-version in " + path.getFileName());
            return;
        }

        if(!configuration.isInt("config-version")) {
            errors.add("config-version must be an integer in " + path.getFileName());
            return;
        }

        int version = configuration.getInt("config-version");

        if(version != CURRENT_CONFIG_VERSION) {
            errors.add(
                    "Expected config version "
                            + CURRENT_CONFIG_VERSION
                            + ", found "
                            + version + " in " + path.getFileName()
            );
        }
    }

}
