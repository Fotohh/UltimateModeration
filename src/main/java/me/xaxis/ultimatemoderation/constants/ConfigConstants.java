package me.xaxis.ultimatemoderation.constants;

import java.util.Objects;

public final class ConfigConstants {

    private ConfigConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final String CONFIG_VERSION_PATH = "config-version";
    public static final ConfigType PLAYER_PROFILE = new ConfigType("player-profile", 1);
    public static final ConfigType MAIN = new ConfigType("config", 1);
    public static final ConfigType LANG = new ConfigType("lang", 1);

    public record ConfigType(
            String name,
            int currentVersion
    ) {
        public ConfigType {
            Objects.requireNonNull(name, "Config name cannot be null");

            if(name.isBlank()) {
                throw new IllegalArgumentException(
                        "Config name cannot be blank"
                );
            }

            if(currentVersion <= 0) {
                throw new IllegalArgumentException(
                        "Config version must be greater than 0"
                );
            }
        }

        public String fileName() {
            return name + ".yml";
        }
    }
}
