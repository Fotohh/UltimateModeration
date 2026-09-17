package me.xaxis.ultimatemoderationplus.constants;

import java.time.Duration;

public final class ConfigConstants {

    private ConfigConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static final String CONFIG_VERSION_PATH = "config-version";
    public static final ConfigType PLAYER_PROFILE = new ConfigType(1);
    public static final ConfigType MAIN = new ConfigType(1);
    public static final ConfigType LANG = new ConfigType(1);
    public static final long MAX_FUTURE_SKEW_MILLIS = Duration.ofHours(24).toMillis();

    public record ConfigType(
            int currentVersion
    ) {
        public ConfigType {
            if(currentVersion <= 0) {
                throw new IllegalArgumentException(
                        "Config version must be greater than 0"
                );
            }
        }
    }
}
