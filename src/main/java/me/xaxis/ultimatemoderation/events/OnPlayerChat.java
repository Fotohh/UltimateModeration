package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class OnPlayerChat extends Utils implements UMPListener {

    private final UMP plugin;

    public OnPlayerChat(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event){

        if(!hasPermission(event.getPlayer(), Permissions.STAFF_CHAT)) return;

        Player player = event.getPlayer();

        if(event.getMessage().startsWith("*") && !plugin.getStaffChat().contains(player)) {

            event.setCancelled(true);

            String msg = Utils.chat(plugin.getLangYML().getString(Lang.STAFF_CHAT_PREFIX) + event.getMessage());

            for (UUID playerUUID : plugin.getStaffChat().getPlayers()) {

                Player target = Bukkit.getPlayer(playerUUID);

                if (target == null || !target.isOnline()) continue;

                target.sendMessage(msg);

            }

            plugin.getStaffChat().add(player);

            message(player, Lang.STAFF_CHAT_TOGGLED);

        }else{

            plugin.getStaffChat().remove(player);

            message(player, Lang.STAFF_CHAT_UNTOGGLED);

        }
    }

    @Override
    public boolean isDependent() {
        return false;
    }

    @Override
    public String getDependentPlugin(){
        return null;
    }

}
