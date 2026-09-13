package me.xaxis.ultimatemoderation.player;

import me.xaxis.ultimatemoderation.constants.PlayerNames;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;

public record Note(
        UUID authorUUID,
        String authorName,
        String content,
        long timestamp
) {

    public static final int MAX_CONTENT_LENGTH = 512;
    public static final long MAX_FUTURE_SKEW_MILLIS = Duration.ofHours(24).toMillis();

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

        if(!PlayerNames.isValid(authorName)) {
            throw new IllegalArgumentException(
                    "Note author name is invalid"
            );
        }

        if(content.isBlank()) {
            throw new IllegalArgumentException(
                    "Note content cannot be blank"
            );
        }

        if(content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                    "Note content cannot exceed "
                            + MAX_CONTENT_LENGTH
                            + " characters"
            );
        }

        if(timestamp < 0) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be negative"
            );
        }

        if(timestamp >
                System.currentTimeMillis()
                        + MAX_FUTURE_SKEW_MILLIS) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be more than 24 hours in the future"
            );
        }
    }
}
