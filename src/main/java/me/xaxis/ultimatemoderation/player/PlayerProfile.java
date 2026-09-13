package me.xaxis.ultimatemoderation.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerProfile {
    private final UUID playerId;
    private String playerName;
    private final List<Note> notes;

    public PlayerProfile(UUID playerId, String playerName, List<Note> notes) {
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.playerName = Objects.requireNonNull(playerName, "Player name cannot be null");
        this.notes = new ArrayList<>(
                Objects.requireNonNull(notes, "Notes cannot be null")
        );
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    public List<Note> notes() {
        return List.copyOf(notes);
    }

    protected void updatePlayerName(String newName) {
        newName = Objects.requireNonNull(newName, "New name cannot be null");
        this.playerName = newName;
    }

    protected void removeNote(Note note) {
        if (note == null) return;
        notes.remove(note);
    }

    protected void addNote(Note note) {
        if (note == null) return;
        notes.add(note);
    }

    public PlayerProfileWrapper toWrapper() {
        return new PlayerProfileWrapper(playerId, playerName, notes);
    }


}
