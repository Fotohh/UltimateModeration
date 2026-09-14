package me.xaxis.ultimatemoderation.commands;

import me.xaxis.ultimatemoderation.config.ConfigSettings;
import me.xaxis.ultimatemoderation.constants.ModerationConstants;
import me.xaxis.ultimatemoderation.lang.LangKey;
import me.xaxis.ultimatemoderation.lang.LangManager;
import me.xaxis.ultimatemoderation.lang.Placeholders;
import me.xaxis.ultimatemoderation.permissions.Permissions;
import me.xaxis.ultimatemoderation.player.Note;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.player.PlayerProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class NoteCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;
    private final ConfigSettings configSettings;

    public NoteCommand(LangManager langManager, PlayerProfileManager playerProfileManager, ConfigSettings configSettings) {
        this.langManager = Objects.requireNonNull(
                langManager,
                "Lang manager cannot be null"
        );

        this.playerProfileManager =
                Objects.requireNonNull(
                        playerProfileManager,
                        "Player profile manager cannot be null"
                );

        this.configSettings = Objects.requireNonNull(
                configSettings,
                "Config settings cannot be null"
        );
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {

        if (sender instanceof Player player && !player.hasPermission(Permissions.NOTE_COMMAND.getPermission())) {
            player.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(langManager.getMessage(LangKey.NOTE_COMMAND_USAGE));
            return true;
        }

        String subcommand = args[0].toLowerCase(Locale.ROOT);

        if(!subcommand.equals("add") && !subcommand.equals("list") && !subcommand.equals("delete")) {

            sender.sendMessage(langManager.getMessage(LangKey.NOTE_COMMAND_USAGE));

            return true;
        }

        String targetName = args[1];

        PlayerProfile targetProfile = playerProfileManager.getPlayerProfile(targetName);

        if (targetProfile == null) {
            sender.sendMessage(langManager.getMessage(LangKey.PLAYER_NOT_FOUND));
            return true;
        }

        if (sender instanceof Player player) {
            handlePlayerNoteCommand(player, targetProfile, args);
        } else if (sender instanceof ConsoleCommandSender) {
            handleConsoleNoteCommand(sender, targetProfile, args);
        } else {
            sender.sendMessage(langManager.getMessage(LangKey.UNABLE_TO_EXECUTE_COMMAND));
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

        if (!player.hasPermission(Permissions.NOTE_COMMAND.getPermission())) {
            player.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
            return;
        }

        handleArgs(playerUUID, playerName, player, targetProfile, args);
    }

    private void handleArgs(UUID authorId, String authorName, CommandSender sender, PlayerProfile target, String[] args) {
        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "add" -> {
                if (!sender.hasPermission(Permissions.NOTE_COMMAND_EDIT.getPermission())) {
                    sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
                    return;
                }
                handleAddSubcommand(authorId, authorName, sender, target, args);
            }
            case "list" -> {
                if (!sender.hasPermission(Permissions.NOTE_COMMAND_VIEW.getPermission())) {
                    sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
                    return;
                }
                handleListSubcommand(sender, target);
            }
            case "delete" -> {
                if (!sender.hasPermission(Permissions.NOTE_COMMAND_DELETE.getPermission())) {
                    sender.sendMessage(langManager.getMessage(LangKey.NO_PERMISSION));
                    return;
                }
                handleDeleteSubcommand(sender, target, args);
            }
            default -> sender.sendMessage(langManager.getMessage(LangKey.NOTE_COMMAND_USAGE));
        }
    }

    private void handleAddSubcommand(UUID authorId, String authorName, CommandSender sender, PlayerProfile target, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(langManager.getMessage(LangKey.NOTE_ADD_COMMAND_USAGE));
            return;
        }

        String noteContent = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        if (noteContent.isBlank()) {
            sender.sendMessage(langManager.getMessage(LangKey.NOTE_MUST_HAVE_CONTENT));
            return;
        }
        if(noteContent.length() > configSettings.noteMaxContentLength()) {
            sender.sendMessage(langManager.replacePlaceholders(
                    langManager.getMessage(LangKey.NOTE_TOO_LONG),
                    Map.of(
                            Placeholders.NOTE_MAX_LENGTH,
                            String.valueOf(configSettings.noteMaxContentLength())
                    )
            ));
            return;
        }
        Note note = new Note(authorId, authorName, noteContent, System.currentTimeMillis());
        playerProfileManager.addNoteToProfile(target, note);
        sender.sendMessage(
                langManager.replacePlaceholders(
                        langManager.getMessage(LangKey.NOTE_ADDED),
                        Map.of(
                                Placeholders.PLAYER, target.playerName()
                        )
                )
        );
    }

    private void handleListSubcommand(CommandSender sender, PlayerProfile target) {
        //todo maybe paginated gui?

        List<Note> notes = playerProfileManager.getNotesFromProfile(target);

        if(notes.isEmpty()) {
            sender.sendMessage(
                    langManager.replacePlaceholders(
                            langManager.getMessage(
                                    LangKey.NO_NOTES
                            ),
                            Map.of(
                                    Placeholders.PLAYER,
                                    target.playerName()
                            )
                    )
            );
        }
        sender.sendMessage(
                langManager.replacePlaceholders(
                        langManager.getMessage(
                                LangKey.NOTE_LIST_HEADER
                        ),
                        Map.of(
                                Placeholders.PLAYER,
                                target.playerName()
                        )
                )
        );

        for (int i = 0; i < notes.size(); i++) {
            sender.sendMessage(
                    langManager.replacePlaceholders(
                            langManager.getMessage(
                                    LangKey.NOTE_LIST_ENTRY
                            ),
                            Map.of(
                                    Placeholders.PLAYER,
                                    target.playerName(),
                                    Placeholders.NOTE_INDEX,
                                    String.valueOf(i + 1),
                                    Placeholders.NOTE_AUTHOR,
                                    notes.get(i).authorName(),
                                    Placeholders.NOTE_CONTENT,
                                    notes.get(i).content()
                            )
                    )
            );
        }
    }

    private void handleDeleteSubcommand(CommandSender sender, PlayerProfile target, String[] args) {
        if (args.length < 3) {
            //todo maybe paginated gui with delete buttons?
            sender.sendMessage(langManager.getMessage(LangKey.NO_DELETE_MESSAGE_INDEX));
            return;
        }

        //todo maybe add confirmation system to prevent accidental deletion of notes
        //todo maybe add a way to delete notes by content instead of index
        //todo maybe add a way to delete notes by author instead of index
        //todo maybe log note deletions to a file for auditing purposes

        try {
            int noteIndex = Integer.parseInt(args[2]) - 1; // Convert to 0-based index
            List<Note> notes = playerProfileManager.getNotesFromProfile(target);
            if (noteIndex < 0 || noteIndex >= notes.size()) {
                sender.sendMessage(langManager.getMessage(LangKey.NO_DELETE_MESSAGE_INDEX));
                return;
            }

            playerProfileManager.removeNoteFromProfile(target, noteIndex);
            sender.sendMessage(langManager.replacePlaceholders(langManager.getMessage(LangKey.NOTE_DELETED),
                    Map.of(
                            Placeholders.PLAYER, target.playerName(),
                            Placeholders.NOTE_INDEX, String.valueOf(noteIndex)
                    )
            ));
        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.replacePlaceholders(langManager.getMessage(LangKey.INVALID_NOTE_INDEX),
                    Map.of(
                            Placeholders.PLAYER, target.playerName(),
                            Placeholders.NOTE_INDEX, args[2]
                    )
            ));
        }

    }
}
