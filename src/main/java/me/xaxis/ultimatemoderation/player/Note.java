package me.xaxis.ultimatemoderation.player;

import me.xaxis.ultimatemoderation.constants.PlayerNames;

import java.util.Objects;
import java.util.UUID;

public record Note(
        UUID authorUUID,
        String authorName,
        String content,
        long timestamp
) {
    public Note {
        Objects.requireNonNull(authorUUID, "Author UUID cannot be null");
        Objects.requireNonNull(authorName, "Author name cannot be null");
        Objects.requireNonNull(content, "Content cannot be null");

        if(authorName.isEmpty()) {
            throw new IllegalArgumentException(
                    "Note author name cannot be blank"
            );
        }

        if(authorName.length() > 16) {
            throw new IllegalArgumentException(
                    "Note author name cannot exceed 16 characters"
            );
        }

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

        if(content.length() > 512) {
            throw new IllegalArgumentException(
                    "Note content cannot exceed 512 characters"
            );
        }

        if(timestamp < 0) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be negative"
            );
        }
    }
}
