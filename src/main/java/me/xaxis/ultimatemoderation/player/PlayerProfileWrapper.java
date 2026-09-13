package me.xaxis.ultimatemoderation.player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PlayerProfileWrapper(UUID playerID, String playerName, List<Note> notes) {
    public PlayerProfileWrapper {
        Objects.requireNonNull(
                playerID,
                "Player ID cannot be null"
        );
        Objects.requireNonNull(
                playerName,
                "Player name cannot be null"
        );
        notes = Objects.requireNonNull(
                List.copyOf(notes),
                "Notes cannot be null"
        );
    }
}
