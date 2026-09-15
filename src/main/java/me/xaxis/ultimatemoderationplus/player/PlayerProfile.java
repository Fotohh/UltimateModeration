package me.xaxis.ultimatemoderationplus.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlayerProfile {
    private final UUID playerId;
    private String playerName;
    private final List<Note> notes;
    private final List<Warning> warnings;

    public PlayerProfile(UUID playerId, String playerName, List<Note> notes, List<Warning> warnings) {
        this.playerId = Objects.requireNonNull(playerId, "Player ID cannot be null");
        this.playerName = Objects.requireNonNull(playerName, "Player name cannot be null");
        this.notes = new ArrayList<>(
                Objects.requireNonNull(notes, "Notes cannot be null")
        );
        this.warnings = new ArrayList<>(
                Objects.requireNonNull(warnings, "Warnings cannot be null")
        );
    }

    public static PlayerProfile create(UUID playerId, String playerName) {
        return new PlayerProfile(
                playerId,
                playerName,
                List.of(),
                List.of()
        );
    }

    public UUID playerId() {
        return playerId;
    }

    public String playerName() {
        return playerName;
    }

    /**
     * Do not access from here. Use {@link PlayerProfileManager#getWarningsFromProfile(PlayerProfile)}
     * @return immutable list of warnings
     */
    public List<Warning> warnings() {
        return List.copyOf(warnings);
    }

    /**
     * Do not access from here. Use {@link PlayerProfileManager#getNotesFromProfile(PlayerProfile)}
     * @return immutable list of notes
     */
    public List<Note> notes() {
        return List.copyOf(notes);
    }

    protected void updatePlayerName(String newName) {
        newName = Objects.requireNonNull(newName, "New name cannot be null");
        this.playerName = newName;
    }

    protected void removeNote(int index) {
        if (index < 0 || index >= notes.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for notes list.");
        }
        notes.remove(index);
    }

    protected void addNote(Note note) {
        if (note == null) return;
        notes.add(note);
    }

    protected void addWarning(Warning warning) {
        if (warning == null) return;
        warnings.add(warning);
    }

    protected void removeWarning(int index) {
        if (index < 0 || index >= warnings.size()) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of bounds for warnings list.");
        }
        warnings.remove(index);
    }

    public PlayerProfileWrapper toWrapper() {
        return new PlayerProfileWrapper(playerId, playerName, notes, warnings);
    }


}
