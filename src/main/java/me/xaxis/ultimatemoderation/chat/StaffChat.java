package me.xaxis.ultimatemoderation.chat;

import me.xaxis.ultimatemoderation.utils.Tuple;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class StaffChat {

    /**
     * A HashMap to store the players in the staff chat.
     * Left is if the player is chatting in staff chat, right is if the staff chat visibility is enabled
     */
    private final HashMap<UUID, Tuple<Boolean, Boolean>> players = new HashMap<>();

    public HashMap<UUID, Tuple<Boolean, Boolean>> getPlayers() {
        return players;
    }

    public void add(Player player){
        players.putIfAbsent(player.getUniqueId(), new Tuple<>(true, false));
    }

    public boolean chatIsVisible(Player player) {
        add(player);
        return players.get(player.getUniqueId()).getRight();
    }

    public boolean isInChat(Player player) {
        add(player);
        return players.get(player.getUniqueId()).getLeft();
    }

    public void setVisibility(Player player, boolean enabled) {
        add(player);
        players.replace(player.getUniqueId(), Tuple.of(isInChat(player), enabled));
    }

    public void setInChat(Player player, boolean enabled) {
        add(player);
        players.replace(player.getUniqueId(), Tuple.of(enabled, chatIsVisible(player)));
    }

    public void remove(Player player){
        players.remove(player.getUniqueId());
    }

    public boolean contains(Player player){
        return players.containsKey(player.getUniqueId());
    }

}
