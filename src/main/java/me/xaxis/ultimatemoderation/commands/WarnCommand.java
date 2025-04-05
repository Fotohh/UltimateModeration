package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.InfractionType;
import me.xaxis.ultimatemoderation.type.Warning;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarnCommand extends Utils implements CommandExecutor {

    private final UMP plugin;

    public WarnCommand(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(!(commandSender instanceof Player player)){
            commandSender.sendMessage("This command can only be run by a player.");
            return true;
        }
        if(!hasPermission(player, Permissions.WARN)){
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }
        if(strings.length == 0){
            player.sendMessage("Please specify a player to warn.");
            return true;
        }
        if(strings.length == 1){
            player.sendMessage("Please specify a reason for warning " + strings[0]);
            return true;
        }
        Player target = plugin.getServer().getPlayer(strings[0]);
        if(target == null){
            player.sendMessage("Player " + strings[0] + " not found.");
            return true;
        }

        String reason = String.join(" ", java.util.Arrays.copyOfRange(strings, 1, strings.length));
        target.sendMessage("You have been warned by " + player.getName() + ": " + reason);
        player.sendMessage("You have warned " + target.getName() + " for: " + reason);

        PlayerProfile prof = PlayerProfile.getPlayerProfile(target.getUniqueId());
        if(prof == null){
            player.sendMessage("Player profile not found.");
            return true;
        }

        prof.addInfraction(InfractionType.WARNING);
        new Warning(reason, player.getUniqueId(), target.getUniqueId());
        return true;
    }
}
