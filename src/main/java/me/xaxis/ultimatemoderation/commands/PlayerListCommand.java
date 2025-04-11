package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.gui.PlayerListGUI;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerListCommand extends Utils implements CommandExecutor {

    private final UMP instance;

    public PlayerListCommand(UMP instance){
        super(instance);
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if (!hasPermission(player, Permissions.PLAYER_LIST)) {
            player.sendMessage(chat("&c&lYou do not have permission to use this command."));
            return true;
        }

        new PlayerListGUI(player).openGUI();

        return true;
    }
}
