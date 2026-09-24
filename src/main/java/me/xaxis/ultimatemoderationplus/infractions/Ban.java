package me.xaxis.ultimatemoderationplus.infractions;

import java.util.UUID;

public record Ban(
        UUID staffId,
        String staffName,
        UUID playerId,
        String reason,
        long timestamp,
        long timeUntil
) { }
//todo ban