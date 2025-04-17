package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnmuteCommand extends Utils implements CommandExecutor {

    private final UMP plugin;

    public UnmuteCommand(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage("You must be a player to use this command!");
            return true;
        }

        if (!hasPermission(player, Permissions.UNMUTE)) {
            player.sendMessage(chat("&cYou do not have permission to use this command."));
            return true;
        }

        if (strings.length == 0) {
            player.sendMessage(chat("&cUsage: /unmute <player>"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(strings[0]);

        if (target == null || !target.isOnline()) {
            player.sendMessage(chat("&cThat player is not online or does not exist."));
            return true;
        }

        if (!plugin.getMuteManager().isMuted(target)) {
            player.sendMessage(chat("&c" + target.getName() + " is not muted."));
            return true;
        }

        plugin.getMuteManager().removeMutedPlayer(target.getUniqueId());
        player.sendMessage(chat("&aYou have unmuted " + target.getName() + "."));
        return false;
    }
}
