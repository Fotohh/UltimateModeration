package me.xaxis.ultimatemoderationplus.codec;

import me.xaxis.ultimatemoderationplus.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderationplus.infractions.Ban;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BanCodec implements ProfileEntryCodec<Ban>{

    @Override
    public Map<String, Object> encode(Ban value) {
        Map<String, Object> entries = new HashMap<>();
        entries.put(PlayerProfileSchema.BAN_REASON, value.reason());
        entries.put(PlayerProfileSchema.BAN_STAFF_NAME, value.staffName());
        entries.put(PlayerProfileSchema.BAN_TARGET_ID, value.playerId());
        entries.put(PlayerProfileSchema.BAN_STAFF_ID, value.staffId());
        entries.put(PlayerProfileSchema.BAN_TIMESTAMP, value.timestamp());
        return entries;
    }

    @Override
    public Ban decode(Map<?, ?> values) {
        return new Ban(
                UUID.fromString((String) values.get(PlayerProfileSchema.BAN_STAFF_ID)),
                (String) values.get(PlayerProfileSchema.BAN_STAFF_NAME),
                UUID.fromString((String) values.get(PlayerProfileSchema.BAN_TARGET_ID)),
                (String) values.get(PlayerProfileSchema.BAN_REASON),
                ((Number) values.get(PlayerProfileSchema.BAN_TIMESTAMP)).longValue()
        );
    }
}
