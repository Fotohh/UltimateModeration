package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.constants.ModerationConstants;
import me.xaxis.ultimatemoderation.lang.LangKey;
import me.xaxis.ultimatemoderation.lang.LangManager;
import me.xaxis.ultimatemoderation.permissions.Permissions;
import me.xaxis.ultimatemoderation.player.Note;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.player.PlayerProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.UUID;

public class NoteCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;

    public NoteCommand(LangManager langManager, PlayerProfileManager playerProfileManager) {
        this.playerProfileManager = playerProfileManager;
        this.langManager = langManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if(args.length < 2) {
            sender.sendMessage(langManager.getMessage(LangKey.NOTE_COMMAND_USAGE));
            return true;
        }

        String playerArgument = args[1]; // todo add index system

        UUID targetUUID = playerProfileManager.getUUIDFromName(playerArgument);

        if(targetUUID == null) {
            sender.sendMessage(langManager.getMessage(LangKey.PLAYER_NOT_FOUND));
            return true;
        }

        PlayerProfile targetProfile = playerProfileManager.getPlayerProfile(targetUUID);

        if(targetProfile == null) {
            sender.sendMessage(langManager.getMessage(LangKey.PLAYER_NOT_FOUND));
            return true;
        }

        if (sender instanceof Player player) {
            handlePlayerNoteCommand(player, targetProfile, args);
        } else {
            handleConsoleNoteCommand(sender, targetProfile, args);
        }

        return true;
    }

    private void handleConsoleNoteCommand(CommandSender sender, PlayerProfile targetProfile, String[] args) {
        String consoleName = ModerationConstants.CONSOLE_NAME;
        UUID consoleUUID = ModerationConstants.CONSOLE_UUID;

        handleArgs(consoleUUID, consoleName, sender, targetProfile, args);
    }
    private void handlePlayerNoteCommand(Player player, PlayerProfile targetProfile, String[] args) {
        UUID playerUUID = player.getUniqueId();
        String playerName = player.getName();

        if(!player.hasPermission(Permissions.NOTE_COMMAND.getPermission())) {
            player.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return;
        }

        switch (args[0].toLowerCase()) {
         case "add" ->  {
                if(!player.hasPermission(Permissions.NOTE_COMMAND_EDIT.getPermission())) {
                    player.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
                    return;
                }
            }
            case "list" -> {
                if(!player.hasPermission(Permissions.NOTE_COMMAND_VIEW.getPermission())) {
                    player.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
                    return;
                }
            }
            case "delete" -> {
                if(!player.hasPermission(Permissions.NOTE_COMMAND_DELETE.getPermission())) {
                    player.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
                    return;
                }
            }
            default -> {
                player.sendMessage(langManager.getMessage(LangKey.NOTE_COMMAND_USAGE));
                return;
            }
        }

        handleArgs(playerUUID, playerName, player, targetProfile, args);
    }

    private void handleArgs(UUID authorId, String authorName, CommandSender sender, PlayerProfile target, String[] args) {
        switch (args[0].toLowerCase()) {
            case "add" -> handleAddSubcommand(authorId, authorName, sender, target, args);
            case "list" -> handleListSubcommand(authorId, authorName, sender, target, args);
            case "delete" -> handleDeleteSubcommand(authorId, authorName, sender, target, args);
            default -> sender.sendMessage(langManager.getMessage(LangKey.NOTE_COMMAND_USAGE));
        }
    }

    private void handleAddSubcommand(UUID authorId, String authorName, CommandSender sender, PlayerProfile target, String[] args) {
        if(args.length < 3) {
            sender.sendMessage(langManager.getMessage(LangKey.NOTE_ADD_COMMAND_USAGE));
            return;
        }

        String noteContent = String.join(" ", Arrays.copyOfRange(args, 2, args.length));

        Note note = new Note(authorId, authorName, noteContent, System.currentTimeMillis());

        playerProfileManager.addNoteToProfile(target, note);

    }

    private void handleListSubcommand(UUID authorId, String authorName, CommandSender sender, PlayerProfile target, String[] args) {
        // Implement list logic here
    }

    private void handleDeleteSubcommand(UUID authorId, String authorName, CommandSender sender, PlayerProfile target, String[] args) {
        // Implement delete logic here
    }
}
