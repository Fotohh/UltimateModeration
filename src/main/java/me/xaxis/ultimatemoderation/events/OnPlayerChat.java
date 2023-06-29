package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UltimateModeration;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class OnPlayerChat implements Listener {

    private final UltimateModeration plugin;

    public OnPlayerChat(UltimateModeration plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event){
        if(!event.getPlayer().hasPermission(Permissions.STAFF_CHAT.s())) return;
        if(!event.getMessage().startsWith("*")) return;
        String msg = Utils.chat("&7<&6Staff Chat&7> &b" + event.getMessage());
        for(UUID playerUUID : plugin.getStaffChat().getPlayers()){
            Player target = Bukkit.getPlayer(playerUUID);
            if(target == null || !target.isOnline()) continue;
            target.sendMessage(msg);
        }
    }
}
