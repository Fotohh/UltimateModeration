package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.infractions.Mute;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class UnmuteCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;

    public UnmuteCommand(LangManager langManager, PlayerProfileManager playerProfileManager) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if(!(sender instanceof Player) && (!(sender instanceof ConsoleCommandSender))) {
            sender.sendMessage(langManager.getMessage(LangKey.SENDER_NOT_VALID));
            return true;
        }

        if(!sender.hasPermission(Permissions.MUTE_COMMAND.getPermission())) {
            sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(langManager.getMessage(LangKey.UNMUTE_USAGE));
            return true;
        }

        String targetName = args[0];

        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(targetName);
        if(playerProfile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return true;
        }

        Mute mute = playerProfileManager.getMute(playerProfile);

        if(mute == null) {
            sender.sendMessage(langManager.getMessage(LangKey.PLAYER_NOT_MUTED));
            return true;
        }

        playerProfileManager.unmuteProfile(playerProfile);
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(LangKey.PLAYER_UNMUTED),
                Map.of(
                        Placeholders.PLAYER, targetName
                )
        ));

        return true;
    }


}
