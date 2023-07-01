package me.xaxis.ultimatemoderation.chat;

import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class StaffChat {

    private final UMP plugin;

    private final ArrayList<UUID> players = new ArrayList<>();

    public StaffChat(UMP plugin){
        this.plugin = plugin;
    }

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
