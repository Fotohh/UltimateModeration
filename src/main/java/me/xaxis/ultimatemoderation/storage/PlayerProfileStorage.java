package me.xaxis.ultimatemoderation.storage;

import me.xaxis.ultimatemoderation.file.AtomicWrite;
import me.xaxis.ultimatemoderation.player.PlayerProfileWrapper;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.nio.file.Path;
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

    private void save(PlayerProfileWrapper profile) throws IOException {

        Path target = profilesDirectory.resolve(
                profile.playerID() + ".yml"
        );

        YamlConfiguration configuration = new YamlConfiguration();

        configuration.set("config-version", 1);
        configuration.set("player-id", profile.playerID().toString());
        configuration.set("player-name", profile.playerName());
        configuration.set("notes", profile.notes());

        String data = configuration.saveToString();

        AtomicWrite.save(target, data);

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

                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Failed to shutdown PlayerProfileStorage thread!", e);
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
