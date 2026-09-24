package me.xaxis.ultimatemoderationplus.codec;

import me.xaxis.ultimatemoderationplus.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderationplus.infractions.Mute;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MuteCodec implements ProfileEntryCodec<Mute> {
    @Override
    public Map<String, Object> encode(Mute value) {
        Objects.requireNonNull(
                value,
                "Mute cannot be null"
        );
        Map<String, Object> values = new HashMap<>();
        values.put(PlayerProfileSchema.MUTE_STAFF_ID, value.staffId().toString());
        values.put(PlayerProfileSchema.MUTE_STAFF_NAME, value.staffName());
        values.put(PlayerProfileSchema.MUTE_REASON, value.reason());
        values.put(PlayerProfileSchema.MUTE_TIMESTAMP, value.timestamp());
        values.put(PlayerProfileSchema.MUTE_TARGET_ID, value.targetId().toString());
        values.put(PlayerProfileSchema.TIME_UNTIL, value.timeUntil());

        return values;
    }

    @Override
    public Mute decode(Map<?, ?> values) {
        Objects.requireNonNull(values, "Mute values cannot be null");

        return new Mute(
                UUID.fromString((String) values.get(PlayerProfileSchema.MUTE_STAFF_ID)),
                (String) values.get(PlayerProfileSchema.MUTE_STAFF_NAME),
                (String) values.get(PlayerProfileSchema.MUTE_REASON),
                ((Number) values.get(PlayerProfileSchema.MUTE_TIMESTAMP)).longValue(),
                UUID.fromString((String) values.get(PlayerProfileSchema.MUTE_TARGET_ID)),
                ((Number) values.get(PlayerProfileSchema.TIME_UNTIL)).longValue()
        );
    }
}
