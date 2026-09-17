package me.xaxis.ultimatemoderationplus.player;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerNames;

import java.util.Objects;
import java.util.UUID;

public record Mute(
        UUID staffId,
        String staffName,
        String reason,
        long timestamp,
        UUID targetId
) {

    public Mute {
        Objects.requireNonNull(targetId, "Player UUID cannot be null");
        Objects.requireNonNull(staffId, "Moderator UUID cannot be null");
        Objects.requireNonNull(reason, "Mute reason cannot be null");
        Objects.requireNonNull(staffName, "Moderator name cannot be null");

        if (!PlayerNames.isValid(staffName)) {
            throw new IllegalArgumentException("Mute moderator name is invalid");
        }

        if (reason.isBlank()) {
            throw new IllegalArgumentException("Mute reason cannot be blank");
        }

        if (timestamp < 0) {
            throw new IllegalArgumentException("Mute timestamp cannot be negative");
        }

        if (timestamp > System.currentTimeMillis() + ConfigConstants.MAX_FUTURE_SKEW_MILLIS) {
            throw new IllegalArgumentException(
                    "Mute timestamp cannot be more than 24 hours in the future"
            );
        }
    }
}
