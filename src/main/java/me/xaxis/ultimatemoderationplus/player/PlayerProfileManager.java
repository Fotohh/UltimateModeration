package me.xaxis.ultimatemoderationplus.player;

import me.xaxis.ultimatemoderationplus.config.ConfigSettings;
import me.xaxis.ultimatemoderationplus.constants.ModerationConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerNames;
import me.xaxis.ultimatemoderationplus.storage.PlayerProfileStorage;

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

    public void removeWarningFromProfile(PlayerProfile profile, int index) {
        profile.removeWarning(index);
        save(profile);
    }

    public void addWarningToProfile(PlayerProfile profile, Warning warning) {
        Objects.requireNonNull(profile, "Profile cannot be null");
        Objects.requireNonNull(warning, "Warning cannot be null");

        if (!profile.playerId().equals(warning.playerUUID())) {
            throw new IllegalArgumentException(
                    "Warning target UUID does not match profile UUID"
            );
        }

        profile.addWarning(warning);
        save(profile);
    }

    public List<Warning> getWarningsFromProfile(PlayerProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        return profile.warnings();
    }

    public PlayerProfile getPlayerProfile(String playerName) {
        Objects.requireNonNull(
                playerName,
                "Player name cannot be null"
        );

        UUID playerId =
                idsByName.get(
                        normalizeName(playerName)
                );

        if(playerId == null) {
            return null;
        }

        PlayerProfile profile =
                playerProfiles.get(playerId);

        if(profile == null) {
            logger.severe(
                    "Name index contains player "
                            + playerName
                            + " mapped to "
                            + playerId
                            + ", but no profile exists."
            );
        }

        return profile;
    }

    public void addNoteToProfile(PlayerProfile profile, Note note) {
        if (note.content().length() > configSettings.maxContentLength()) {
            throw new IllegalArgumentException("Note content cannot exceed " + configSettings.maxContentLength() + " characters");
        }
        profile.addNote(note);
        save(profile);
    }

    public void removeNoteFromProfile(PlayerProfile profile, int index) {
        profile.removeNote(index);
        save(profile);
    }

    public List<Note> getNotesFromProfile(PlayerProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }
        return profile.notes();
    }

    public List<Note> getNotesFromProfile(UUID playerId) {
        PlayerProfile profile = playerProfiles.get(playerId);
        return getNotesFromProfile(profile);
    }

    private void addNameMapping(PlayerProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("Profile cannot be null");
        }

        if (profile.playerName().equals(ModerationConstants.UNKNOWN_PLAYER_NAME)) {
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

    private UUID getUUIDFromName(String playerArgument) {
        Objects.requireNonNull(
                playerArgument,
                "Player name cannot be null"
        );

        return idsByName.get(
                normalizeName(playerArgument)
        );
    }
}
