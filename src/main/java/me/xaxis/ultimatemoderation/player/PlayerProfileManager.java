package me.xaxis.ultimatemoderation.player;

import me.xaxis.ultimatemoderation.config.ConfigSettings;
import me.xaxis.ultimatemoderation.constants.PlayerNames;
import me.xaxis.ultimatemoderation.storage.PlayerProfileStorage;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerProfileManager implements AutoCloseable {

    private final Map<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    private final Map<String, UUID> idsByName = new HashMap<>();
    private final PlayerProfileStorage storage;
    private final Logger logger;
    private final ConfigSettings configSettings;

    public PlayerProfileManager(
            List<PlayerProfile> playerProfiles,
            PlayerProfileStorage storage,
            ConfigSettings configSettings,
            Logger logger
    ) {
        Objects.requireNonNull(
                playerProfiles,
                "Player profiles cannot be null"
        );

        this.storage = Objects.requireNonNull(
                storage,
                "Storage cannot be null"
        );

        this.configSettings = Objects.requireNonNull(
                configSettings,
                "Config settings cannot be null"
        );

        this.logger = Objects.requireNonNull(
                logger,
                "Logger cannot be null"
        );

        playerProfiles.forEach(profile -> {
            PlayerProfile previous =
                    this.playerProfiles.putIfAbsent(
                            profile.playerId(),
                            profile
                    );

            if (previous != null) {
                throw new IllegalStateException(
                        "Duplicate profile UUID: "
                                + profile.playerId()
                );
            }

            addNameMapping(profile);
        });
    }

    public void addNoteToProfile(PlayerProfile profile, Note note) {
        if (note.content().length() > configSettings.noteMaxContentLength()) {
            throw new IllegalArgumentException("Note content cannot exceed " + configSettings.noteMaxContentLength() + " characters");
        }
        profile.addNote(note);
        save(profile);
    }

    public void removeNoteFromProfile(PlayerProfile profile, Note note) {
        profile.removeNote(note);
        save(profile);
    }

    public List<Note> getNotesFromProfile(UUID playerId) {
        PlayerProfile profile = playerProfiles.get(playerId);
        if (profile == null) {
            throw new IllegalArgumentException("No profile found for player ID: " + playerId);
        }
        return profile.notes();
    }

    private void addNameMapping(PlayerProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }

        if (profile.playerName().equalsIgnoreCase("unknown")) {
            logger.warning("Player name 'unknown' is reserved and should not be used. Player ID: " + profile.playerId());
            return;
        }

        UUID existing = this.idsByName.putIfAbsent(normalizeName(profile.playerName()), profile.playerId());
        if (existing != null && !existing.equals(profile.playerId())) {
            logger.warning(
                    "Duplicate player name detected: " + profile.playerName()
                            + ". Existing UUID: " + existing
                            + ", New UUID: " + profile.playerId()
            );
        }
    }

    public void changeName(
            PlayerProfile profile,
            String newName
    ) {
        Objects.requireNonNull(
                profile,
                "Unable to change player name. Profile cannot be null."
        );

        Objects.requireNonNull(
                newName,
                "New player name cannot be null."
        );

        if(!PlayerNames.isValid(newName)) {
            throw new IllegalArgumentException(
                    "Invalid Minecraft player name: " + newName
            );
        }

        String oldName = profile.playerName();

        if(oldName.equals(newName)) {
            return;
        }

        profile.updatePlayerName(newName);

        idsByName.remove(
                normalizeName(oldName),
                profile.playerId()
        );

        addNameMapping(profile);

        save(profile);
    }

    private String normalizeName(String name) {
        return name.toLowerCase(Locale.ROOT);
    }

    public PlayerProfile getPlayerProfile(UUID playerId) {
        return playerProfiles.get(playerId);
    }

    public void addPlayerProfile(PlayerProfile playerProfile) {
        PlayerProfile previous = playerProfiles.putIfAbsent(playerProfile.playerId(), playerProfile);

        if (previous != null) {
            throw new IllegalStateException("Profile already exists for " + playerProfile.playerId());
        }

        addNameMapping(playerProfile);
    }

    public void saveAll() {
        for (PlayerProfile profile : playerProfiles.values()) {
            save(profile);
        }
    }

    public void save(PlayerProfile playerProfile) {
        storage.saveAsync(playerProfile.toWrapper()).whenComplete((ignored, throwable) -> {
            if (throwable != null) {
                logger.log(Level.SEVERE, "Failed to save player profile with the id: " + playerProfile.playerId(), throwable);
            }
        });
    }

    @Override
    public void close() {
        storage.close();
    }

    public UUID getUUIDFromName(String playerArgument) {
        Objects.requireNonNull(
                playerArgument,
                "Player name cannot be null"
        );

        return idsByName.get(
                normalizeName(playerArgument)
        );
    }
}
