package me.xaxis.ultimatemoderation.chat;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class StaffChat {

    private final ArrayList<UUID> players = new ArrayList<>();

    public ArrayList<UUID> getPlayers() {
        return players;
    }

    public void add(Player player){
        players.add(player.getUniqueId());
    }

    public void remove(Player player){
        players.remove(player.getUniqueId());
    }

    public boolean contains(Player player){
        return players.contains(player.getUniqueId());
    }

}
