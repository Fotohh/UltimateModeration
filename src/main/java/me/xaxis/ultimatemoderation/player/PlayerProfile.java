package me.xaxis.ultimatemoderation.player;

import java.util.List;
import java.util.UUID;

public record PlayerProfile(UUID playerId, String playerName, List<String> notes) {
}
