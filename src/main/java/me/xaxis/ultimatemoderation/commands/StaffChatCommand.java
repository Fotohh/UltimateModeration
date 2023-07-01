package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class StaffChatCommand extends Utils implements CommandExecutor {

    private final UMP plugin;

    public StaffChatCommand(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player){

            if(hasPermission(player, Permissions.STAFF_CHAT)){

                if(args.length == 0){
                    if(plugin.getStaffChat().contains(player)){
                        message(player,Lang.STAFF_CHAT_UNTOGGLED);
                    }else{
                        message(player,Lang.STAFF_CHAT_TOGGLED);
                    }
                }else{
                    StringBuilder sb = new StringBuilder();
                    for (String arg : args) {
                        sb.append(arg).append(" ");
                    }

                    sb.insert(0,plugin.getLangYML().getString(Lang.STAFF_CHAT_PREFIX));

                    String msg = chat(sb.toString());

                    plugin.getStaffChat().getPlayers().forEach(p ->{
                        Bukkit.getPlayer(p).sendMessage(msg);
                    });

                }

            }else{
                message(player, Lang.NO_PERMISSION);
            }

        }else{
            //message SENDER NOT PLAYER
        }

        return true;
    }
}
