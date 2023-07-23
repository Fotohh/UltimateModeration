package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener{
    private final UMP plugin;

    public PlayerJoin(UMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        if(!PlayerProfile.containsPlayer(event.getPlayer().getUniqueId())){
            new PlayerProfile(event.getPlayer(), plugin);
        }
    }
}
