package me.xaxis.ultimatemoderation.codec;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import me.xaxis.ultimatemoderation.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.player.PlayerProfileWrapper;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Objects;
import java.util.UUID;

public final class PlayerProfileCodec {

    private final NoteCodec noteCodec = new NoteCodec();
    private final WarningCodec warningCodec = new WarningCodec();

    public YamlConfiguration encode(PlayerProfileWrapper profile) {
        Objects.requireNonNull(profile, "Player profile cannot be null");

        YamlConfiguration configuration = new YamlConfiguration();
        configuration.set(
                ConfigConstants.CONFIG_VERSION_PATH,
                ConfigConstants.PLAYER_PROFILE.currentVersion()
        );
        configuration.set(
                PlayerProfileSchema.PLAYER_ID,
                profile.playerID().toString()
        );
        configuration.set(
                PlayerProfileSchema.PLAYER_NAME,
                profile.playerName()
        );
        configuration.set(
                PlayerProfileSchema.NOTES,
                noteCodec.encodeAll(profile.notes())
        );
        configuration.set(
                PlayerProfileSchema.WARNINGS,
                warningCodec.encodeAll(profile.warnings())
        );

        return configuration;
    }

    public PlayerProfile decode(YamlConfiguration configuration, UUID expectedPlayerId) {
        Objects.requireNonNull(configuration, "Configuration cannot be null");
        Objects.requireNonNull(expectedPlayerId, "Expected player ID cannot be null");

        String playerName = Objects.requireNonNull(
                configuration.getString(PlayerProfileSchema.PLAYER_NAME),
                "Validated profile is missing player-name"
        );

        return new PlayerProfile(
                expectedPlayerId,
                playerName,
                noteCodec.decodeAll(configuration.getMapList(PlayerProfileSchema.NOTES)),
                warningCodec.decodeAll(configuration.getMapList(PlayerProfileSchema.WARNINGS))
        );
    }
}
