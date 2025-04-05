package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.InfractionType;
import me.xaxis.ultimatemoderation.type.Mute;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MuteCommand extends Utils implements CommandExecutor {

    private UMP plugin;

    public MuteCommand(UMP instance) {
        super(instance);
        this.plugin = instance;
    }

    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
            return true;
        }

        if (!hasPermission(player, Permissions.MUTE)) {
            message(player, "&cYou do not have permission to use this command.");
            return true;
        }

        if (strings.length == 0) {
            message(player, "&cUsage: /mute <player>");
            return true;
        }

        if (strings.length == 1) {
            message(player, "&cPlease specify a reason for muting " + strings[0]);
            return true;
        }

        Player target = Bukkit.getPlayer(strings[0]);

        if (target == null || !target.isOnline()) {
            message(player, "&cThat player is not online or does not exist.");
            return true;
        }

        if(hasPermission(target, Permissions.MUTE_BYPASS)) {
            message(player, "&cYou cannot mute this player because they have the MUTE_BYPASS permission.");
            return true;
        }

        if (plugin.getMuteManager().isMuted(target)) {
            message(player, "&c" + target.getName() + " is already muted.");
            return true;
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(strings, 1, strings.length));

        PlayerProfile profile = PlayerProfile.getPlayerProfile(target.getUniqueId());
        if (profile == null) {
            message(player, "&cPlayer profile not found.");
            return true;
        }

        profile.addInfraction(InfractionType.MUTE);

        plugin.getMuteManager().addMutedPlayer(target.getUniqueId());

        target.sendMessage(chat("&cYou have been muted by " + player.getName() + ": " + reason));

        message(player, "&aYou have muted " + target.getName() + " for: " + reason);

        new Mute(reason, 1000L*60*60*24*7*4*12*100, target.getUniqueId(), player.getUniqueId());

        return false;
    }
}
