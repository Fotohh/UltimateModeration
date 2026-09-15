package me.xaxis.ultimatemoderationplus.player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PlayerProfileWrapper(UUID playerID, String playerName, List<Note> notes, List<Warning> warnings) {
    public PlayerProfileWrapper {
        Objects.requireNonNull(
                playerID,
                "Player ID cannot be null"
        );
        Objects.requireNonNull(
                playerName,
                "Player name cannot be null"
        );
        notes = List.copyOf(
                Objects.requireNonNull(
                        notes,
                        "Notes cannot be null"
                )
        );
        warnings = List.copyOf(
                Objects.requireNonNull(
                        warnings,
                        "Warnings cannot be null"
                )
        );
    }
}
