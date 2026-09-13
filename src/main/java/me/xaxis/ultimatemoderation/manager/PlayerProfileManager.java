package me.xaxis.ultimatemoderation.manager;

import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.storage.PlayerProfileStorage;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerProfileManager implements AutoCloseable {

    private final List<PlayerProfile> playerProfiles;
    private final PlayerProfileStorage storage;
    private final Logger logger;

    public PlayerProfileManager(
            List<PlayerProfile> playerProfiles,
            PlayerProfileStorage storage,
            Logger logger
    ) {
        this.playerProfiles = playerProfiles;
        this.storage = storage;
        this.logger = logger;
    }

    public void saveAll() {
        for(PlayerProfile profile : playerProfiles) {
            save(profile);
        }
    }

    public void save(PlayerProfile playerProfile) {
        storage.saveAsync(playerProfile.toWrapper()).whenComplete(
                (ignored, throwable) -> {
                    if(throwable != null) {
                        logger.log(
                                Level.SEVERE,
                                "Failed to save player profile with the id: " + playerProfile.playerId(),
                                throwable
                        );
                    }
                }
        );
    }

    @Override
    public void close() {
        storage.close();
    }
}
