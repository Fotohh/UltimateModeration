package me.xaxis.ultimatemoderationplus.player;

import me.xaxis.ultimatemoderationplus.infractions.Ban;
import me.xaxis.ultimatemoderationplus.infractions.Mute;
import me.xaxis.ultimatemoderationplus.infractions.Note;
import me.xaxis.ultimatemoderationplus.infractions.Warning;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record PlayerProfileWrapper(
        UUID playerID,
        String playerName,
        List<Note> notes,
        Mute playerMute,
        List<Warning> warnings,
        Ban playerBan
) { }
