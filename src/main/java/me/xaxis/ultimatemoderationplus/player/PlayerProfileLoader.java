package me.xaxis.ultimatemoderationplus.player;

import me.xaxis.ultimatemoderationplus.codec.PlayerProfileCodec;
import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.validation.PlayerProfileYmlValidation;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerProfileLoader implements AutoCloseable {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final PlayerProfileCodec profileCodec = new PlayerProfileCodec();

    private final Path parentFolderPath;
    private final Logger logger;

    public PlayerProfileLoader(Path parentFolder, Logger logger) {
        this.parentFolderPath = Objects.requireNonNull(
                parentFolder,
                "Parent folder cannot be null"
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

        if (files == null) {
            throw new IllegalStateException(
                    "Unable to list player profile directory: "
                            + parentFolderPath
            );
        }

        for (File file : files) {
            if (!file.isFile()) continue;
            if (!file.getName().endsWith(".yml")) continue;

            String fileName = file.getName();
            String playerIdString = fileName.substring(
                    0,
                    fileName.length() - ".yml".length()
            );

            UUID playerId;
            try {
                playerId = UUID.fromString(playerIdString);
            } catch (IllegalArgumentException e) {
                logger.log(
                        Level.SEVERE,
                        "Found malformed UUID in profile filename: " + fileName,
                        e
                );
                continue;
            }

            if (!playerId.toString().equals(playerIdString)) {
                logger.severe(
                        "Found non-canonical UUID profile filename: "
                                + fileName
                );
                continue;
            }

            try {
                PlayerProfile profile = loadProfile(file, playerId);
                if (profile != null) {
                    profiles.add(profile);
                }
            } catch (IOException e) {
                throw new UncheckedIOException(
                        "Failed to load player profile: " + fileName,
                        e
                );
            }
        }

        return profiles;
    }

    private void ensureSupportedProfileVersion(
            File file,
            YamlConfiguration configuration
    ) {
        if (!configuration.isInt(ConfigConstants.CONFIG_VERSION_PATH)) {
            return;
        }

        int version = configuration.getInt(
                ConfigConstants.CONFIG_VERSION_PATH
        );
        int currentVersion = ConfigConstants.PLAYER_PROFILE.currentVersion();

        if (version == currentVersion) {
            return;
        }

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

    private PlayerProfile loadProfile(File file, UUID uuid) throws IOException {
        YamlConfiguration configuration = new YamlConfiguration();

        try {
            configuration.load(file);
        } catch (InvalidConfigurationException | IOException e) {
            logger.log(
                    Level.WARNING,
                    "Profile "
                            + file.getName()
                            + " could not be loaded.",
                    e
            );
            return null;
        }

        ensureSupportedProfileVersion(file, configuration);

        PlayerProfileYmlValidation validator =
                new PlayerProfileYmlValidation(
                        file.toPath(),
                        configuration
                );

        List<String> errors = validator.validate();

        if (!errors.isEmpty()) {
            errors.forEach(logger::severe);

            return null;
        }

        return profileCodec.decode(configuration, uuid);
    }

    @Override
    public void close() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.log(
                    Level.SEVERE,
                    "Failed to shutdown PlayerProfileLoader thread!",
                    e
            );
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
