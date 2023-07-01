package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpyCommand extends Utils implements CommandExecutor {

    private final UMP main;

    public SpyCommand(UMP main) {
        super(main);
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(sender instanceof Player player){

            if(!hasPermission(player, Permissions.SPY)){
                message(player, Lang.NO_PERMISSION);
                return true;
            }

            if(args.length != 1){
                //message INCORRECT USAGE /HELP
                return true;
            }

            vanishPlayer(player);



        }else{
            //message SENDER NOT PLAYER
        }

        return true;
    }
}
