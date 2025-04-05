package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.file.PlayerProfileYML;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.spy.PlayerSpy;
import me.xaxis.ultimatemoderation.spy.SpyManager;
import me.xaxis.ultimatemoderation.utils.Tuple;
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

        PlayerProfile prof = PlayerProfile.getPlayerProfile(event.getPlayer().getUniqueId());
        prof.setLastJoinDate(System.currentTimeMillis());

        if(plugin.getSpyManager().containsPlayer(event.getPlayer())){
            PlayerSpy spy = plugin.getSpyManager().getSpyHashMap().get(event.getPlayer().getUniqueId());
            spy.setLeft(false);
        }
        if(!plugin.getStaffChat().contains(event.getPlayer()) && event.getPlayer().hasPermission(Permissions.STAFF_CHAT.s())) {
            plugin.getStaffChat().add(event.getPlayer());
        }
    }
}
