package me.xaxis.ultimatemoderation.loader;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import me.xaxis.ultimatemoderation.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderation.file.SafeFileWrite;
import me.xaxis.ultimatemoderation.player.Note;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.validation.PlayerProfileYmlValidation;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerProfileLoader implements AutoCloseable{

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private static final String UNKNOWN_PLAYER_NAME = "Unknown";
    private static final String PLAYER_NAME_PATH = "player-name";

    private final Path parentFolderPath;
    private final Logger logger;

    public PlayerProfileLoader(Path parentFolder, Logger logger) {
        this.parentFolderPath = Objects.requireNonNull(
                parentFolder,
                "Parent Folder cannot be null"
        );
        this.logger = Objects.requireNonNull(
                logger,
                "Logger cannot be null"
        );

    }

    private List<PlayerProfile> loadProfiles() {
        List<PlayerProfile> profiles = new ArrayList<>();

        File parentFolder = parentFolderPath.toFile();
        File[] files = parentFolder.listFiles();

        if(files == null) {
            throw new IllegalStateException(
                    "Unable to list player profile directory: "
                            + parentFolderPath
            );
        }

        for(File file : files) {
            if(!file.isFile()) continue;
            if(!file.getName().endsWith(".yml")) continue;
            String fileName = file.getName();
            String playerIdString = fileName.substring(0, fileName.length() - ".yml".length());
            UUID playerId;

            try {
                playerId = UUID.fromString(playerIdString);
            } catch(IllegalArgumentException e) {
                logger.log(
                        Level.SEVERE,
                        "Found malformed UUID in profile filename: " + fileName,
                        e
                );

                quarantineOrFail(file);
                continue;
            }

            if(!playerId.toString().equals(playerIdString)) {
                logger.severe(
                        "Found non-canonical UUID profile filename: "
                                + fileName
                );

                quarantineOrFail(file);
                continue;
            }

            PlayerProfile profile;
            try {
                profile = loadProfile(file, playerId);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Failed to load profile: " + fileName + " | Skipping", e);
                continue;
            }

            if(profile == null) {
                logger.log(Level.SEVERE, "Failed to load profile and/or create a backup: " + fileName + " | Skipping", new Exception());
                continue;
            }

            profiles.add(profile);

        }

        return profiles;
    }

    private void ensureSupportedProfileVersion(
            File file,
            YamlConfiguration configuration
    ) {
        if(!configuration.isInt(
                ConfigConstants.CONFIG_VERSION_PATH
        )) {
            return;
        }

        int version =
                configuration.getInt(
                        ConfigConstants.CONFIG_VERSION_PATH
                );

        int currentVersion =
                ConfigConstants.PLAYER_PROFILE.currentVersion();

        if(version == currentVersion) {
            return;
        }

        // TODO: Add explicit profile schema migrations before
        // changing a released schema.
        // Until then, leave version-mismatched data untouched
        // and fail startup safely.
        throw new IllegalStateException(
                "Unsupported player profile schema version "
                        + version
                        + " in "
                        + file.getName()
                        + "; expected "
                        + currentVersion
                        + ". The file was left untouched."
        );
    }

    public CompletableFuture<List<PlayerProfile>> loadProfilesAsync() {
        return CompletableFuture.supplyAsync(
                this::loadProfiles,
                executor
        );
    }

    private YamlConfiguration createFreshProfile(File file, UUID uuid) throws IOException {

        YamlConfiguration configuration = new YamlConfiguration();

        configuration.set(ConfigConstants.CONFIG_VERSION_PATH, ConfigConstants.PLAYER_PROFILE.currentVersion());
        configuration.set(
                ConfigConstants.CONFIG_VERSION_PATH,
                ConfigConstants.PLAYER_PROFILE.currentVersion()
        );

        configuration.set(
                PlayerProfileSchema.PLAYER_ID,
                uuid.toString()
        );

        configuration.set(
                PlayerProfileSchema.PLAYER_NAME,
                UNKNOWN_PLAYER_NAME
        );

        configuration.set(
                PlayerProfileSchema.NOTES,
                List.of()
        );

        SafeFileWrite.save(
                file.toPath(),
                configuration.saveToString()
        );

        return configuration;
    }

    private Path quarantineProfile(File file) throws IOException {
        Path original = file.toPath();
        Path backup = getAvailableBackupPath(original);

        Files.move(original, backup);

        logger.info(
                "Quarantined profile: "
                        + file.getName()
                        + " -> "
                        + backup.getFileName()
        );

        return backup;
    }

    private void quarantineOrFail(File file) {
        try {
            quarantineProfile(file);
        } catch(IOException e) {
            throw new UncheckedIOException(
                    "Failed to quarantine profile "
                            + file.getName(),
                    e
            );
        }
    }

    private Path getAvailableBackupPath(Path file) {
        Path parent = file.getParent();
        String baseName = file.getFileName() + ".bak";
        Path backup = parent.resolve(baseName);
        int count = 1;
        while(Files.exists(backup)) {
            backup = parent.resolve(baseName + "." + count++);
        }
        return backup;
    }

    private PlayerProfile loadProfile(File file, UUID uuid) throws IOException {

        YamlConfiguration configuration = new YamlConfiguration();

        try {
            configuration.load(file);
        } catch(InvalidConfigurationException | IOException e) {
            logger.log(
                    Level.WARNING,
                    "Profile "
                            + file.getName()
                            + " could not be loaded. Attempting regeneration.",
                    e
            );

            quarantineProfile(file);
            configuration = createFreshProfile(file, uuid);
        }

        ensureSupportedProfileVersion(file, configuration);

        PlayerProfileYmlValidation validator =
                new PlayerProfileYmlValidation(
                        file.toPath(),
                        configuration,
                        uuid
                );

        List<String> errors = validator.validate();

        if(!errors.isEmpty()) {

            errors.forEach(logger::severe);

            try {
                quarantineProfile(file);
                configuration = createFreshProfile(file, uuid);
            } catch(IOException e) {
                logger.log(
                        Level.SEVERE,
                        "Failed to regenerate invalid profile " + file.getName(),
                        e
                );

                return null;
            }
        }

        List<Note> notes = parseNotes(configuration);
        return new PlayerProfile(
                uuid,
                configuration.getString(PLAYER_NAME_PATH),
                notes
        );
    }

    private List<Note> parseNotes(
            YamlConfiguration configuration
    ) {
        List<Note> notes = new ArrayList<>();

        List<Map<?, ?>> noteMaps =
                configuration.getMapList(
                        PlayerProfileSchema.NOTES
                );

        for(Map<?, ?> noteMap : noteMaps) {
            notes.add(parseNoteFromMap(noteMap));
        }

        return notes;
    }

    private Note parseNoteFromMap(Map<?, ?> noteMap) {
        Number timestampNumber =
                (Number) noteMap.get(
                        PlayerProfileSchema.NOTE_TIMESTAMP
                );

        return new Note(
                UUID.fromString(
                        (String) noteMap.get(
                                PlayerProfileSchema.NOTE_AUTHOR_ID
                        )
                ),
                (String) noteMap.get(
                        PlayerProfileSchema.NOTE_AUTHOR_NAME
                ),
                (String) noteMap.get(
                        PlayerProfileSchema.NOTE_CONTENT
                ),
                timestampNumber.longValue()
        );
    }


    @Override
    public void close() {
        executor.shutdown();

        try {
            if(!executor.awaitTermination(
                    10,
                    TimeUnit.SECONDS
            )) {
                executor.shutdownNow();
            }
        } catch(InterruptedException e) {
            logger.log(Level.SEVERE, "Failed to shutdown PlayerProfileLoader thread!", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
