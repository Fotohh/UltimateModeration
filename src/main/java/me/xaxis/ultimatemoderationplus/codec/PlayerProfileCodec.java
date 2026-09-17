package me.xaxis.ultimatemoderationplus.codec;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderationplus.player.Mute;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileWrapper;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerProfileCodec {

    private final NoteCodec noteCodec = new NoteCodec();
    private final WarningCodec warningCodec = new WarningCodec();
    private final MuteCodec muteCodec = new MuteCodec();

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
        configuration.set(
                PlayerProfileSchema.MUTE,
                profile.playerMute() == null
                        ? null
                        : muteCodec.encode(profile.playerMute())
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

        Map<String, Object> map = configuration.isConfigurationSection(PlayerProfileSchema.MUTE)
                ? configuration.getConfigurationSection(PlayerProfileSchema.MUTE).getValues(false)
                : null;
        Mute mute = map == null ? null : muteCodec.decode(map);

        return new PlayerProfile(
                expectedPlayerId,
                playerName,
                noteCodec.decodeAll(configuration.getMapList(PlayerProfileSchema.NOTES)),
                mute,
                warningCodec.decodeAll(configuration.getMapList(PlayerProfileSchema.WARNINGS))
        );
    }
}
