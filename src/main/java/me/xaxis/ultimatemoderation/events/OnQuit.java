package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.spy.PlayerSpy;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnQuit extends Utils implements Listener {
    private final UMP plugin;

    public OnQuit(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void leave(PlayerQuitEvent event){
        if(plugin.getSpyManager().containsPlayer(event.getPlayer())){
            PlayerSpy spy = plugin.getSpyManager().getSpyHashMap().get(event.getPlayer().getUniqueId());
            spy.setLeft(true);
        }

        PlayerProfile profile = PlayerProfile.getPlayerProfile(event.getPlayer().getUniqueId());
        profile.save();
        if(profile.isVanished()) {
            event.setQuitMessage(null);
            vanishPlayer(event.getPlayer(), false);
        }
    }
}
