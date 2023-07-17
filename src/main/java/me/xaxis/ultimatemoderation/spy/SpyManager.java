package me.xaxis.ultimatemoderation.spy;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.UUID;

public class SpyManager extends Utils implements Listener{

    private final UMP plugin;

    private final ArrayList<UUID> uuids = new ArrayList<>();
    private final ArrayList<UUID> left = new ArrayList<>();

    public SpyManager(UMP plugin) {
        super(plugin);

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    public void addPlayer(Player player){
        uuids.add(player.getUniqueId());
    }

    public boolean containsPlayer(Player player){
        return uuids.contains(player.getUniqueId());
    }

    public void removePlayer(Player player){
        uuids.remove(player.getUniqueId());
    }

    public ArrayList<UUID> getUUIDS() {
        return uuids;
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(containsPlayer(player)){
            removePlayer(player);
            left.add(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(left.contains(player.getUniqueId())){
            unvanishPlayer(player);
            left.remove(player.getUniqueId());
        }
    }

    //TODO add on interact to quit spying when fnished n stuff using items in hotbar

}
