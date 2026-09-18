package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.config.ConfigSettings;
import me.xaxis.ultimatemoderationplus.constants.ModerationConstants;
import me.xaxis.ultimatemoderationplus.infractions.Ban;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import me.xaxis.ultimatemoderationplus.utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class BanCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;
    private final ConfigSettings configSettings;

    public BanCommand(LangManager langManager, PlayerProfileManager playerProfileManager, ConfigSettings configSettings) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
        this.configSettings = configSettings;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        Tuple<UUID, String> identity = validateSender(sender);
        if(identity == null) {
            sender.sendMessage(langManager.getMessage(LangKey.SENDER_NOT_VALID));
            return true;
        }

        if(!sender.hasPermission(Permissions.BAN_COMMAND.getPermission())) {
            sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return true;
        }

        if(args.length < 2) {
            sender.sendMessage(langManager.getMessage(LangKey.BAN_USAGE));
            return true;
        }

        String playerName = args[0];
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(playerName);
        if(playerProfile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, playerName
                    )
            ));
            return true;
        }

        if(playerProfileManager.getPlayerBan(playerProfile) != null) {
            sender.sendMessage(
                    langManager.replacePlaceholders(
                            langManager.getMessage(
                                    LangKey.PLAYER_ALREADY_BANNED
                            ),
                            Map.of(
                                    Placeholders.PLAYER,
                                    playerName
                            )
                    )
            );
            return true;
        }

        Player targetPlayer = Bukkit.getPlayer(playerProfile.playerId());

        if(reason.isBlank()) {
            sender.sendMessage(langManager.getMessage(LangKey.BAN_MUST_HAVE_REASON));
            return true;
        }

        if(reason.length() > configSettings.maxContentLength()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.CONTENT_TOO_LONG),
                    Map.of(
                            Placeholders.CONTENT_MAX_LENGTH, String.valueOf(configSettings.maxContentLength())
                    )
            ));
            return true;
        }

        playerProfileManager.banPlayer(playerProfile, new Ban(
                identity.first(),
                identity.second(),
                playerProfile.playerId(),
                reason,
                System.currentTimeMillis()
        ));

        if(targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.kickPlayer(reason);
        }

        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(LangKey.BANNED_PLAYER),
                Map.of(
                        Placeholders.PLAYER, playerName,
                        Placeholders.REASON, reason
                )
        ));

        return true;
    }

    private Tuple<UUID, String> validateSender(CommandSender sender) {
        if(sender instanceof Player player) {
            return new Tuple<>(player.getUniqueId(), player.getName());
        } else if(sender instanceof ConsoleCommandSender) {
            return new Tuple<>(ModerationConstants.CONSOLE_UUID, ModerationConstants.CONSOLE_NAME);
        } else return null;
    }
}
