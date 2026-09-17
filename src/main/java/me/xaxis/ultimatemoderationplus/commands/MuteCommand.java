package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.constants.ModerationConstants;
import me.xaxis.ultimatemoderationplus.infractions.Mute;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import me.xaxis.ultimatemoderationplus.utils.Tuple;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class MuteCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;

    public MuteCommand(LangManager langManager, PlayerProfileManager playerProfileManager) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        Tuple<UUID, String> identity = validateSender(sender);

        if (identity == null) {
            sender.sendMessage(langManager.getMessage(LangKey.SENDER_NOT_VALID));
            return true;
        }

        if (!sender.hasPermission(Permissions.MUTE_COMMAND.getPermission())) {
            sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(langManager.getMessage(LangKey.MUTE_USAGE));
            return true;
        }

        String targetName = args[0];
        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(targetName);
        if (playerProfile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return true;
        }

        String reason = String.join(
                args[1],
                Arrays.copyOfRange(args, 1, args.length)
        );

        if(reason.isBlank()) {
            sender.sendMessage(langManager.getMessage(LangKey.MUTE_MUST_HAVE_REASON));
            return true;
        }

        playerProfileManager.muteProfile(playerProfile, new Mute(
                identity.first(),
                identity.second(),
                reason,
                System.currentTimeMillis(),
                playerProfile.playerId()
        ));
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(LangKey.MUTED_PLAYER),
                Map.of(
                        Placeholders.PLAYER, targetName,
                        Placeholders.REASON, reason
                )
        ));

        return true;
    }

    private Tuple<UUID, String> validateSender(CommandSender sender) {
        if (sender instanceof Player player) {
            return new Tuple<>(player.getUniqueId(), player.getName());
        } else if (sender instanceof ConsoleCommandSender) {
            return new Tuple<>(ModerationConstants.CONSOLE_UUID, ModerationConstants.CONSOLE_NAME);
        } else return null;
    }
}
