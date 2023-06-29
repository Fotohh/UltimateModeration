package me.xaxis.ultimatemoderation.chat;

import me.xaxis.ultimatemoderation.UltimateModeration;

import java.util.ArrayList;
import java.util.UUID;

public class StaffChat {

    private final UltimateModeration plugin;

    private final ArrayList<UUID> players = new ArrayList<>();

    public StaffChat(UltimateModeration plugin){
        this.plugin = plugin;
    }

    public ArrayList<UUID> getPlayers() {
        return players;
    }

}
