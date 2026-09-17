package me.xaxis.ultimatemoderationplus.player;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerNames;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public record Note(
        UUID authorUUID,
        String authorName,
        String content,
        long timestamp
) {

    public Note {
        Objects.requireNonNull(
                authorUUID,
                "Author UUID cannot be null"
        );

        Objects.requireNonNull(
                authorName,
                "Author name cannot be null"
        );

        Objects.requireNonNull(
                content,
                "Content cannot be null"
        );

        if (!PlayerNames.isValid(authorName)) {
            throw new IllegalArgumentException(
                    "Note author name is invalid"
            );
        }

        if (content.isBlank()) {
            throw new IllegalArgumentException(
                    "Note content cannot be blank"
            );
        }

        if (timestamp < 0) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be negative"
            );
        }

        if (timestamp >
                System.currentTimeMillis()
                        + ConfigConstants.MAX_FUTURE_SKEW_MILLIS) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be more than 24 hours in the future"
            );
        }
    }
}
