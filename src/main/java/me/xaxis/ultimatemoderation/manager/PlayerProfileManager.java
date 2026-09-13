package me.xaxis.ultimatemoderation.manager;

import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.storage.PlayerProfileStorage;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerProfileManager implements AutoCloseable {

    private final Map<UUID, PlayerProfile> playerProfiles;
    private final PlayerProfileStorage storage;
    private final Logger logger;

    public PlayerProfileManager(
            List<PlayerProfile> playerProfiles,
            PlayerProfileStorage storage,
            Logger logger
    ) {
        this.playerProfiles = playerProfiles.stream().collect(
                Collectors.toMap(PlayerProfile::playerId, profile -> profile)
        );
        this.storage = storage;
        this.logger = logger;
    }

    public boolean hasPlayerProfile(UUID playerId) {
        return playerProfiles.containsKey(playerId);
    }

    public PlayerProfile getPlayerProfile(UUID playerId) {
        return playerProfiles.get(playerId);
    }

    public void addPlayerProfile(PlayerProfile playerProfile) {
        playerProfiles.put(playerProfile.playerId(), playerProfile);
    }

    //maybe add a removePlayerProfile method if needed

    public void saveAll() {
        for(PlayerProfile profile : playerProfiles.values()) {
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
