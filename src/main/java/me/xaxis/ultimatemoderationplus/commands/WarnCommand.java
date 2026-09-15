package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.config.ConfigSettings;
import me.xaxis.ultimatemoderationplus.constants.ModerationConstants;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.permissions.Permissions;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import me.xaxis.ultimatemoderationplus.player.Warning;
import me.xaxis.ultimatemoderationplus.utils.Tuple;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class WarnCommand implements CommandExecutor {

    private final ConfigSettings configSettings;
    private final PlayerProfileManager playerProfileManager;
    private final LangManager langManager;

    public WarnCommand(ConfigSettings configSettings, PlayerProfileManager playerProfileManager, LangManager langManager) {
        this.configSettings = configSettings;
        this.playerProfileManager = playerProfileManager;
        this.langManager = langManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String @NonNull [] args) {

        if(!sender.hasPermission(Permissions.WARN_COMMAND_VIEW.getPermission())) {
            sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return true;
        }

        var values = handleSender(sender);
        if(values == null) {
            sender.sendMessage(langManager.getMessage(LangKey.UNABLE_TO_EXECUTE_COMMAND));
            return true;
        }

        if(args.length < 1) {
            sender.sendMessage(langManager.getMessage(LangKey.WARN_COMMAND_USAGE));
            return true;
        }

        UUID id = values.first();
        String name = values.second();

        String type = args[0].toLowerCase(Locale.ROOT);

        switch (type) {
            case "add" -> {
                if(args.length < 3) {
                    sender.sendMessage(langManager.getMessage(LangKey.WARN_ADD_COMMAND_USAGE));
                    return true;
                }
                var addValues = handleAdd(sender, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
                playerProfileManager.addWarningToProfile(addValues.first(), addValues.second());
            }

            case "delete" -> {
                if(args.length < 3) {
                    sender.sendMessage(langManager.getMessage(LangKey.WARN_DELETE_COMMAND_USAGE));
                }
            }

            case "list" -> {

            }

            default -> sender.sendMessage(langManager.getMessage(LangKey.WARN_COMMAND_USAGE));
        }

        return true;
    }

    private Tuple<PlayerProfile, Warning> handleAdd(CommandSender sender, String targetName, String reason) {
        Tuple<PlayerProfile, Warning> val;

        if(reason.isBlank()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.WARN_MUST_HAVE_REASON),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
        }

    }

    private void handleDelete() {

    }

    private void handleList() {

    }

    private Tuple<UUID, String> handleSender(CommandSender sender) {
        if(sender instanceof Player player) {
            UUID playerId = player.getUniqueId();
            String playerName = player.getName();
            return new Tuple<>(playerId, playerName);
        } else if (sender instanceof ConsoleCommandSender) {
            UUID consoleId = ModerationConstants.CONSOLE_UUID;
            String consoleName = ModerationConstants.CONSOLE_NAME;
            return new Tuple<>(consoleId, consoleName);
        }
        return null;
    }
}
