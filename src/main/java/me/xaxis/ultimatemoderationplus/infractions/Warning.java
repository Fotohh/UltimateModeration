package me.xaxis.ultimatemoderationplus.infractions;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerNames;

import java.util.Objects;
import java.util.UUID;

public record Warning(
        UUID playerUUID,
        UUID moderatorUUID,
        String reason,
        long timestamp,
        String moderatorName
) {

    public Warning {
        Objects.requireNonNull(playerUUID, "Player UUID cannot be null");
        Objects.requireNonNull(moderatorUUID, "Moderator UUID cannot be null");
        Objects.requireNonNull(reason, "Warning reason cannot be null");
        Objects.requireNonNull(moderatorName, "Moderator name cannot be null");

        if (!PlayerNames.isValid(moderatorName)) {
            throw new IllegalArgumentException("Warning moderator name is invalid");
        }

        if (reason.isBlank()) {
            throw new IllegalArgumentException("Warning reason cannot be blank");
        }

        if (timestamp < 0) {
            throw new IllegalArgumentException("Warning timestamp cannot be negative");
        }

        if (timestamp > System.currentTimeMillis() + ConfigConstants.MAX_FUTURE_SKEW_MILLIS) {
            throw new IllegalArgumentException(
                    "Warning timestamp cannot be more than 24 hours in the future"
            );
        }
    }
}
