package me.xaxis.ultimatemoderationplus.infractions;

import java.util.UUID;

public record Mute(
        UUID staffId,
        String staffName,
        String reason,
        long timestamp,
        UUID targetId,
        long timeUntil
) {
}
