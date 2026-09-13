package me.xaxis.ultimatemoderation.player;

import java.util.UUID;

public record Note(UUID authorUUID, String authorName, String content, long timestamp) {
}
