package me.xaxis.ultimatemoderation.player;

import java.util.List;
import java.util.UUID;

public class PlayerProfile {
    private final UUID playerId;
    private String playerName;
    private final List<Note> notes;

    public PlayerProfile(UUID playerId, String playerName, List<Note> notes) {
        this.playerId = playerId;
        this.playerName = playerName;
        this.notes = notes;
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    public List<Note> notes() {
        return notes;
    }

    public void updatePlayerName(String newName) {
        if (newName.isBlank()) return;
        this.playerName = newName;
    }

    public void addNote(Note note) {
        if (note == null) return;
        notes.add(note);
    }

    public PlayerProfileWrapper toWrapper() {
        return new PlayerProfileWrapper(playerId, playerName, notes);
    }


}
