package me.xaxis.ultimatemoderation.player;

import java.util.UUID;

public record Warning(
        UUID playerUUID,
        UUID moderatorUUID,
        String reason,
        long timestamp,
        String moderatorName) {

}
