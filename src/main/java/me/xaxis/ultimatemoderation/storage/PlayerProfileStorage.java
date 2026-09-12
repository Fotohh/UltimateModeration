package me.xaxis.ultimatemoderation.storage;

import me.xaxis.ultimatemoderation.player.PlayerProfile;

import java.nio.file.Path;
import java.util.Objects;

public class PlayerProfileStorage {

    private final Path profilesDirectory;

    public PlayerProfileStorage(Path profilesDirectory) {
        this.profilesDirectory =
                Objects.requireNonNull(profilesDirectory);
    }

    public void save(PlayerProfile profile) {
        Path target = profilesDirectory.resolve(
                profile.playerId() + ".yml"
        );

        // create isolated YamlConfiguration
        // copy PlayerProfile values into it
        // write temp file
        // atomic move temp -> target
    }

}
