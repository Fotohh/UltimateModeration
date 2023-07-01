package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerListCommand implements CommandExecutor {

    private final UMP instance;

    public PlayerListCommand(UMP instance){
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(sender instanceof Player){

            Player player = (Player) sender;

            if(player.hasPermission("")){



            }else{



            }

        }else{



        }

        return true;
    }
}
