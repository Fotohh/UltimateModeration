package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.config.ConfigSettings;
import me.xaxis.ultimatemoderationplus.constants.ModerationConstants;
import me.xaxis.ultimatemoderationplus.infractions.Ban;
import me.xaxis.ultimatemoderationplus.infractions.Mute;
import me.xaxis.ultimatemoderationplus.lang.Lang;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import me.xaxis.ultimatemoderationplus.utils.Tuple;
import me.xaxis.ultimatemoderationplus.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class TempMuteCommand implements CommandExecutor {
    private final LangManager langManager;
    private final ConfigSettings configSettings;
    private final PlayerProfileManager playerProfileManager;
    public TempMuteCommand(LangManager langManager, ConfigSettings configSettings, PlayerProfileManager playerProfileManager) {
        this.langManager = langManager;
        this.configSettings = configSettings;
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {
        Tuple<UUID, String> identity;
        if((identity = handleSender(sender)) == null) {
            sender.sendMessage(langManager.getMessage(Lang.SENDER_NOT_VALID));
            return true;
        }

        if(!sender.hasPermission(Permissions.TEMPMUTE_COMMAND.getPermission())){
            sender.sendMessage(langManager.getMessage(Lang.NO_PERMISSION));
            return true;
        }

        if(args.length < 3) {
            sender.sendMessage(langManager.getMessage(Lang.TEMPMUTE_USAGE));
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

        if(playerProfileManager.getMute(playerProfile) != null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(Lang.PLAYER_ALREADY_MUTED),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return true;
        }

        String unparsedDuration = args[1];

        long parsedDuration = Utils.parseDuration(unparsedDuration);
        if(parsedDuration == -1) {
            sender.sendMessage(langManager.getMessage(Lang.INVALID_DURATION));
            return true;
        }

        String reason = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        if(reason.isBlank()) {
            sender.sendMessage(langManager.getMessage(Lang.MUTE_MUST_HAVE_REASON));
            return true;
        }

        if(reason.length() > configSettings.maxContentLength()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(Lang.CONTENT_TOO_LONG),
                    Map.of(
                            Placeholders.CONTENT_MAX_LENGTH,
                            String.valueOf(configSettings.maxContentLength())
                    )
            ));
            return true;
        }

        playerProfileManager.muteProfile(playerProfile, new Mute(
                identity.first(),
                identity.second(),
                reason,
                System.currentTimeMillis(),
                playerProfile.playerId(),
                System.currentTimeMillis() + parsedDuration
        ));
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(Lang.TEMPMUTED_PLAYER),
                Map.of(
                        Placeholders.PLAYER, targetName,
                        Placeholders.REASON, reason,
                        Placeholders.DURATION, Utils.formatDuration(parsedDuration)
                )
        ));

        return true;
    }

    private Tuple<UUID, String> handleSender(CommandSender sender) {

        if (sender instanceof Player player) {
            return new Tuple<>(player.getUniqueId(), player.getName());
        } else if (sender instanceof ConsoleCommandSender unused) {
            return new Tuple<>(ModerationConstants.CONSOLE_UUID, ModerationConstants.CONSOLE_NAME);
        } else {
            return null;
        }
    }
}
