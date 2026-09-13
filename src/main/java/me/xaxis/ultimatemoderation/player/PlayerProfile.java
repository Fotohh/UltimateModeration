package me.xaxis.ultimatemoderation.player;

import java.util.List;
import java.util.UUID;

public record PlayerProfile(UUID playerId, String playerName, List<String> notes) {

    public void addNote(String note) {
        if (note.isBlank()) return;
        notes.add(note);
    }

    public PlayerProfileWrapper toWrapper() {
        return new PlayerProfileWrapper(playerId, playerName, notes);
    }


}
