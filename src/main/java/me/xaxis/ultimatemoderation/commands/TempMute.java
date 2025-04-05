package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.InfractionType;
import me.xaxis.ultimatemoderation.type.Mute;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TempMute extends Utils implements CommandExecutor {

    private final UMP plugin;

    public TempMute(UMP main) {
        super(main);
        plugin = main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)) {
            commandSender.sendMessage(chat("&cYou must be a player to use this command."));
            return true;
        }

        if(!hasPermission(player, Permissions.TEMP_MUTE)) {
            message(player, Lang.NO_PERMISSION);
            return true;
        }

        if(strings.length < 2) {
            player.sendMessage(chat("&cUsage: /tempmute <player> <duration> [reason]"));
            return true;
        }

        Player target = plugin.getServer().getPlayer(strings[0]);

        if(target == null || !target.isOnline()) {
            player.sendMessage(chat("&cThat player is either offline or does not exist!"));
            return true;
        }

        String duration = strings[1];

        if (!duration.matches("\\d+[smhd]")) {
            player.sendMessage(chat("&cInvalid duration format! Use <number><s/m/h/d> (e.g., 10m, 1h, 2d)."));
            return true;
        }

        long durationMillis = 0;
        char unit = duration.charAt(duration.length() - 1);
        long value = Long.parseLong(duration.substring(0, duration.length() - 1));

        switch (unit) {
            case 's' -> durationMillis = value * 1000;
            case 'm' -> durationMillis = value * 60 * 1000;
            case 'h' -> durationMillis = value * 60 * 60 * 1000;
            case 'd' -> durationMillis = value * 24 * 60 * 60 * 1000;
            default -> player.sendMessage(chat("&cInvalid duration unit! Use s/m/h/d."));
        }

        String reason = strings.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(strings, 2, strings.length)) : "No reason provided";

        if (hasPermission(target, Permissions.MUTE_BYPASS)) {
            player.sendMessage(chat("&cYou cannot mute this player!"));
            return true;
        }

        target.sendMessage(chat("&cYou have been muted for " + duration + " by " + player.getName() + ". Reason: " + reason));

        PlayerProfile prof = PlayerProfile.getPlayerProfile(target.getUniqueId());
        if (prof == null) {
            player.sendMessage(chat("&cPlayer profile not found."));
            return true;
        }

        prof.addInfraction(InfractionType.TEMP_MUTE);

        new Mute(reason, durationMillis, target.getUniqueId(), player.getUniqueId());

        player.sendMessage(chat("&aYou have successfully muted " + target.getName() + " for " + duration + ". Reason: " + reason));
        return false;
    }
}
