package me.xaxis.ultimatemoderation.codec;

import me.xaxis.ultimatemoderation.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderation.player.Warning;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class WarningCodec implements ProfileEntryCodec<Warning> {

    @Override
    public Map<String, Object> encode(Warning warning) {
        Objects.requireNonNull(warning, "Warning cannot be null");

        Map<String, Object> values = new LinkedHashMap<>();
        values.put(PlayerProfileSchema.WARNING_TARGET_ID, warning.playerUUID().toString());
        values.put(PlayerProfileSchema.WARNING_STAFF_ID, warning.moderatorUUID().toString());
        values.put(PlayerProfileSchema.WARNING_STAFF_NAME, warning.moderatorName());
        values.put(PlayerProfileSchema.WARNING_CONTENT, warning.reason());
        values.put(PlayerProfileSchema.WARNING_TIMESTAMP, warning.timestamp());
        return values;
    }

    @Override
    public Warning decode(Map<?, ?> values) {
        Objects.requireNonNull(values, "Warning values cannot be null");

        return new Warning(
                UUID.fromString((String) values.get(PlayerProfileSchema.WARNING_TARGET_ID)),
                UUID.fromString((String) values.get(PlayerProfileSchema.WARNING_STAFF_ID)),
                (String) values.get(PlayerProfileSchema.WARNING_CONTENT),
                ((Number) values.get(PlayerProfileSchema.WARNING_TIMESTAMP)).longValue(),
                (String) values.get(PlayerProfileSchema.WARNING_STAFF_NAME)
        );
    }
}
