package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Vanish extends Utils implements CommandExecutor {

    private final UMP plugin;

    public Vanish(UMP main) {
        super(main);
        plugin = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage("You must be a player to use this command.");
            return true;
        }

        Player player = (Player) sender;

        if(!hasPermission(player, Permissions.VANISH)) {
            message(player, "&cYou do not have permission to use this command.");
            return true;
        }

        PlayerProfile profile = PlayerProfile.getPlayerProfile(player.getUniqueId());

        if(args.length == 0) {
            if(profile.isVanished()) {
                profile.setVanished(false);
                vanishPlayer(player, false);
                message(player, "&7You are now &cvisible&7.");
            } else {
                profile.setVanished(true);
                vanishPlayer(player, false);
                message(player, "&7You are now &avanished&7.");
            }
            return true;
        }

        player.sendMessage(chat("&cUsage: /vanish"));
        
        return false;
    }
}
