package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SettingsCommand extends Utils implements CommandExecutor {

    private final UMP main;

    public SettingsCommand(UMP main) {
        super(main);
        this.main = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage("You must be a player to use this command.");
            return true;
        }

        if(!hasPermission(player, Permissions.SETTINGS)) {
            message(player, Lang.NO_PERMISSION);
            return true;
        }

        if(strings.length == 0) {
            //TODO gui
            return true;
        }

        if(strings[0].equalsIgnoreCase("togglesc") || strings[0].equalsIgnoreCase("togglestaffchat") || strings[0].equalsIgnoreCase("tsc")) {
            main.getStaffChat().setVisibility(player, !main.getStaffChat().chatIsVisible(player));
            if (main.getStaffChat().chatIsVisible(player)) {
                player.sendMessage(chat("&7Staff chat visibility &aenabled"));
            } else {
                player.sendMessage(chat("&7Staff chat visibility &cdisabled"));
            }
            return true;
        }

        return false;
    }
}
