package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.spy.PlayerSpy;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnQuit implements Listener {
    private final UMP plugin;

    public OnQuit(UMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void leave(PlayerQuitEvent event){
        if(plugin.getSpyManager().containsPlayer(event.getPlayer())){
            PlayerSpy spy = plugin.getSpyManager().getSpyHashMap().get(event.getPlayer().getUniqueId());
            spy.setLeft(true);
        }
    }
}
