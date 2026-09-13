package me.xaxis.ultimatemoderation.storage;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import me.xaxis.ultimatemoderation.file.SafeFileWrite;
import me.xaxis.ultimatemoderation.player.Note;
import me.xaxis.ultimatemoderation.player.PlayerProfileWrapper;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerProfileStorage implements AutoCloseable {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final Path profilesDirectory;
    private final Logger logger;

    public PlayerProfileStorage(Path profilesDirectory, Logger logger) {
        this.profilesDirectory = Objects.requireNonNull(
                profilesDirectory,
                "Profiles path cannot be null"
        );
        this.logger = Objects.requireNonNull(
                logger,
                "Logger cannot be null"
        );
    }

    private Map<String, Object> serializeNote(Note note) {
        return Map.of(
                "author-uuid", note.authorUUID().toString(),
                "author-name", note.authorName(),
                "content", note.content(),
                "timestamp", note.timestamp()
        );
    }

    private void save(PlayerProfileWrapper profile) throws IOException {


        Path target = profilesDirectory.resolve(
                profile.playerID().toString() + ".yml"
        );

        YamlConfiguration configuration = new YamlConfiguration();

        configuration.set(ConfigConstants.CONFIG_VERSION_PATH, ConfigConstants.PLAYER_PROFILE.currentVersion());
        configuration.set("player-id", profile.playerID().toString());
        configuration.set("player-name", profile.playerName());
        List<Map<String, Object>> notesList = profile.notes().stream()
                .map(this::serializeNote).toList();
        configuration.set("notes", notesList);

        String data = configuration.saveToString();

        SafeFileWrite.save(target, data);

    }

    public CompletableFuture<Void> saveAsync(PlayerProfileWrapper playerProfile) {
        Objects.requireNonNull(playerProfile, "Player profile cannot be null");

        return CompletableFuture.runAsync(() -> {
            try {
                save(playerProfile);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        }, executor);
    }

    @Override
    public void close() {
        executor.shutdown();

        try {
            if(!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                logger.warning(
                        "Player profile storage did not finish saving within 10 seconds. " +
                                "Forcing shutdown."
                );

                List<Runnable> dropped = executor.shutdownNow();

                logger.severe(
                        "Forced profile-storage shutdown; "
                                + dropped.size()
                                + " queued save(s) were not started."
                );
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Failed to shutdown PlayerProfileStorage thread!", e);
            List<Runnable> dropped = executor.shutdownNow();

            logger.severe(
                    "Forced profile-storage shutdown; "
                            + dropped.size()
                            + " queued save(s) were not started."
            );
            Thread.currentThread().interrupt();
        }
    }
}
