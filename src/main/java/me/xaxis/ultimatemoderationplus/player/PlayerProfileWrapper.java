package me.xaxis.ultimatemoderationplus.player;

import me.xaxis.ultimatemoderationplus.infractions.Mute;
import me.xaxis.ultimatemoderationplus.infractions.Note;
import me.xaxis.ultimatemoderationplus.infractions.Warning;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PlayerProfileWrapper(
        UUID playerID,
        String playerName,
        List<Note> notes,
        Mute playerMute,
        List<Warning> warnings
) {
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
        Objects.requireNonNull(
                playerMute,
                "Player Mute cannot be null"
        );
        warnings = List.copyOf(
                Objects.requireNonNull(
                        warnings,
                        "Warnings cannot be null"
                )
        );
    }
}
