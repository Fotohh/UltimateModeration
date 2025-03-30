package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
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
                player.sendMessage(chat("&c&lIncorrect command usage! /spy <username>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if(target == null || !target.isOnline()) {
                player.sendMessage(chat("&c&lThat player is either offline or does not exist!"));
                return true;
            }

            if (!main.getSpyManager().containsPlayer(player)) {
                main.getSpyManager().addPlayer(player, target, new PlayerRollbackManager().save(player));
                vanishPlayer(player, false);
                player.getInventory().setContents(main.getSpyManager().getDefaultContents());
            }else{
                player.sendMessage(chat("&c&lYou are already spying on someone!"));
                return true;
            }
            player.teleport(target.getLocation());
            player.sendMessage(chat("&aYou are now spying on " + target.getName()));

        }else{
            //message SENDER NOT PLAYER
        }

        return true;
    }
}
