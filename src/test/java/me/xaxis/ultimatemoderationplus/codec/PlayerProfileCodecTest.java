package me.xaxis.ultimatemoderationplus.codec;

import me.xaxis.ultimatemoderationplus.constants.ModerationConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderationplus.infractions.Note;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.infractions.Warning;
import me.xaxis.ultimatemoderationplus.validation.PlayerProfileYmlValidation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerProfileCodecTest {

    @Test
    void profileRoundTripsThroughYamlRepresentation() {
        UUID playerId = UUID.randomUUID();
        long now = System.currentTimeMillis();

        PlayerProfile original = new PlayerProfile(
                playerId,
                "Target123",
                List.of(
                        new Note(
                                UUID.randomUUID(),
                                "Moderator1",
                                "Possible alt account",
                                now
                        )
                ),
                List.of(
                        new Warning(
                                playerId,
                                ModerationConstants.CONSOLE_UUID,
                                "Repeated chat spam",
                                now,
                                ModerationConstants.CONSOLE_NAME
                        )
                )
        );

        PlayerProfileCodec codec = new PlayerProfileCodec();
        YamlConfiguration encoded = codec.encode(original.toWrapper());

        List<String> validationErrors = new PlayerProfileYmlValidation(
                Path.of(playerId + ".yml"),
                encoded
        ).validate();

        assertTrue(
                validationErrors.isEmpty(),
                () -> "Codec wrote a profile rejected by the validator: " + validationErrors
        );

        PlayerProfile decoded = codec.decode(encoded, playerId);

        assertEquals(original.playerId(), decoded.playerId());
        assertEquals(original.playerName(), decoded.playerName());
        assertEquals(original.notes(), decoded.notes());
        assertEquals(original.warnings(), decoded.warnings());
    }

    @Test
    void warningTargetMustMatchOwningProfile() {
        UUID playerId = UUID.randomUUID();
        long now = System.currentTimeMillis();

        PlayerProfile profile = new PlayerProfile(
                playerId,
                "Target123",
                List.of(),
                List.of(
                        new Warning(
                                playerId,
                                UUID.randomUUID(),
                                "Repeated chat spam",
                                now,
                                "Moderator1"
                        )
                )
        );

        PlayerProfileCodec codec = new PlayerProfileCodec();
        YamlConfiguration encoded = codec.encode(profile.toWrapper());

        Map<String, Object> warning = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : encoded.getMapList(PlayerProfileSchema.WARNINGS).getFirst().entrySet()) {
            warning.put((String) entry.getKey(), entry.getValue());
        }

        warning.put(
                PlayerProfileSchema.WARNING_TARGET_ID,
                UUID.randomUUID().toString()
        );
        encoded.set(PlayerProfileSchema.WARNINGS, List.of(warning));

        List<String> errors = new PlayerProfileYmlValidation(
                Path.of(playerId + ".yml"),
                encoded
        ).validate();

        assertTrue(
                errors.stream().anyMatch(error -> error.contains("target UUID does not match")),
                () -> "Expected a warning-target mismatch error, got: " + errors
        );
    }
}
