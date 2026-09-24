package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.config.ConfigSettings;
import me.xaxis.ultimatemoderationplus.lang.Lang;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;

public class KickCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;
    private final ConfigSettings configSettings;

    public KickCommand(LangManager langManager, PlayerProfileManager playerProfileManager, ConfigSettings configSettings) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
        this.configSettings = configSettings;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if (!sender.hasPermission(Permissions.KICK_COMMAND.getPermission())) {
            sender.sendMessage(langManager.getMessage(Lang.NO_PERMISSION));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(langManager.getMessage(Lang.KICK_COMMAND_USAGE));
            return true;
        }

        String targetName = args[0];
        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(targetName);
        if (playerProfile == null) {
            sender.sendMessage(langManager.getMessage(Lang.PLAYER_NOT_FOUND));
            return true;
        }

        String reason = String.join(
                " ",
                Arrays.copyOfRange(
                        args,
                        1,
                        args.length
                )
        );
        if (reason.isBlank()) {
            sender.sendMessage(langManager.getMessage(Lang.KICK_MUST_HAVE_REASON));
            return true;
        }

        if(reason.length() > configSettings.maxContentLength()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(Lang.CONTENT_TOO_LONG),
                    Map.of(
                            Placeholders.CONTENT_MAX_LENGTH, String.valueOf(configSettings.maxContentLength())
                    )
            ));
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(playerProfile.playerId());
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            sender.sendMessage(langManager.getMessage(Lang.PLAYER_NOT_ONLINE));
            return true;
        }

        targetPlayer.kickPlayer("You have been kicked for: " + reason);
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(Lang.KICKED_PLAYER),
                Map.of(
                        Placeholders.PLAYER, targetName,
                        Placeholders.REASON, reason
                )
        ));

        return true;
    }
}
