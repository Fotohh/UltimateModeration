package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.infractions.Ban;
import me.xaxis.ultimatemoderationplus.lang.Lang;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class UnbanCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;

    public UnbanCommand(LangManager langManager, PlayerProfileManager playerProfileManager) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if(!sender.hasPermission(Permissions.UNBAN_COMMAND.getPermission())) {
            sender.sendMessage(langManager.getMessage(Lang.NO_PERMISSION));
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(langManager.getMessage(Lang.UNBAN_USAGE));
            return true;
        }

        String targetName = args[0];

        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(targetName);
        if(playerProfile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(Lang.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return true;
        }

        Ban ban = playerProfileManager.getPlayerBan(playerProfile);
        if(ban == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(Lang.PLAYER_NOT_BANNED),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return true;
        }

        playerProfileManager.unbanPlayer(playerProfile);
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(Lang.UNBANNED_PLAYER),
                Map.of(
                        Placeholders.PLAYER, targetName
                )
        ));

        return true;
    }
}
