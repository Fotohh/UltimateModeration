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

import java.util.*;

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

        if (!sender.hasPermission(Permissions.WARN_COMMAND_VIEW.getPermission())) {
            sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return true;
        }

        var identity = handleSender(sender);
        if (identity == null) {
            sender.sendMessage(langManager.getMessage(LangKey.UNABLE_TO_EXECUTE_COMMAND));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(langManager.getMessage(LangKey.WARN_COMMAND_USAGE));
            return true;
        }

        String type = args[0].toLowerCase(Locale.ROOT);

        switch (type) {
            case "add" -> {
                if (args.length < 3) {
                    sender.sendMessage(langManager.getMessage(LangKey.WARN_ADD_COMMAND_USAGE));
                    return true;
                }
                
                handleAdd(sender, identity, args[1], String.join(" ", Arrays.copyOfRange(args, 2, args.length)));
            }

            case "delete" -> {
                if (args.length < 3) {
                    sender.sendMessage(langManager.getMessage(LangKey.WARN_DELETE_COMMAND_USAGE));
                    return true;
                }

                handleDelete(sender, args[1], args[2]);
            }

            case "list" -> {
                if (args.length < 2) {
                    sender.sendMessage(langManager.getMessage(LangKey.WARN_LIST_COMMAND_USAGE));
                    return true;
                }

                handleList(sender, args[1]);
            }

            default -> sender.sendMessage(langManager.getMessage(LangKey.WARN_COMMAND_USAGE));
        }

        return true;
    }

    private void handleAdd(CommandSender sender, Tuple<UUID, String> identity, String targetName, String reason) {

        if (reason.isBlank()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.WARN_MUST_HAVE_REASON),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }

        if (reason.length() > configSettings.maxContentLength()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.CONTENT_TOO_LONG),
                    Map.of(
                            Placeholders.NOTE_MAX_LENGTH, String.valueOf(configSettings.maxContentLength())
                    )
            ));
            return;
        }

        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(targetName);

        if (playerProfile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }


        playerProfileManager.addWarningToProfile(
                playerProfile,
                new Warning(
                        playerProfile.playerId(),
                        identity.first(),
                        reason,
                        System.currentTimeMillis(),
                        identity.second()
                )
        );
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(LangKey.WARN_ADDED),
                Map.of(
                        Placeholders.PLAYER, targetName
                )
        ));

    }

    private void handleDelete(CommandSender sender, String targetName, String indexStr) {
        PlayerProfile profile = playerProfileManager.getPlayerProfile(targetName);
        if (profile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }

        int displayIndex;

        try {
            displayIndex = Integer.parseInt(indexStr);
        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.INVALID_NUMBER),
                    Map.of(
                            Placeholders.VALUE, indexStr
                    )
            ));
            return;
        }

        int actualIndex = displayIndex - 1;

        if (actualIndex < 0) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.INVALID_WARN_INDEX),
                    Map.of(
                            Placeholders.VALUE, String.valueOf(displayIndex),
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }

        int size = playerProfileManager.getWarningsFromProfile(profile).size();

        if (actualIndex >= size) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.INVALID_WARN_INDEX),
                    Map.of(
                            Placeholders.VALUE, String.valueOf(displayIndex),
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }

        playerProfileManager.removeWarningFromProfile(profile, actualIndex);
        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(LangKey.WARN_DELETED),
                Map.of(
                        Placeholders.PLAYER, targetName,
                        Placeholders.VALUE, String.valueOf(displayIndex)
                )
        ));
    }

    private void handleList(CommandSender sender, String targetName) {
        PlayerProfile profile = playerProfileManager.getPlayerProfile(targetName);

        if (profile == null) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.PLAYER_NOT_FOUND),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }

        List<Warning> warnings = playerProfileManager.getWarningsFromProfile(profile);

        if (warnings.isEmpty()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.NO_NOTES),
                    Map.of(
                            Placeholders.PLAYER, targetName
                    )
            ));
            return;
        }

        sender.sendMessage(langManager.replacePlaceholders(
                langManager.getMessage(LangKey.WARN_LIST_HEADER),
                Map.of(
                        Placeholders.PLAYER, targetName
                )
        ));

        for (int i = 0; i < warnings.size(); i++) {
            int displayIndex = i + 1;
            String warnContent = warnings.get(i).reason();
            String authorName = warnings.get(i).moderatorName();
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.WARN_LIST_ENTRY),
                    Map.of(
                            Placeholders.WARN_AUTHOR, authorName,
                            Placeholders.WARN_CONTENT, warnContent,
                            Placeholders.VALUE, String.valueOf(displayIndex)
                    )
            ));
        }

    }

    private Tuple<UUID, String> handleSender(CommandSender sender) {
        if (sender instanceof Player player) {
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
