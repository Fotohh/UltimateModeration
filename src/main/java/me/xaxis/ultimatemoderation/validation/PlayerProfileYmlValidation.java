package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.config.ConfigSettings;
import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import me.xaxis.ultimatemoderation.constants.ModerationConstants;
import me.xaxis.ultimatemoderation.constants.PlayerNames;
import me.xaxis.ultimatemoderation.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderation.player.Note;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.*;


public final class PlayerProfileYmlValidation extends YamlValidator {

    private static final String PLAYER_ID_PATH = "player-id";
    private static final String PLAYER_NAME_PATH = "player-name";
    private static final String NOTES_PATH = "notes";
    private static final Set<String> NOTE_FIELDS = Set.of(
            "content",
            "timestamp",
            "author-id",
            "author-name"
    );
    private static final int MAX_NOTE_LENGTH = 512;
    private final UUID expectedPlayerId;
    private final ConfigSettings configSettings;

    public PlayerProfileYmlValidation(
            Path path,
            FileConfiguration configuration,
            UUID expectedPlayerId,
            ConfigSettings configSettings
    ) {
        super(
                path,
                configuration,
                ConfigConstants.PLAYER_PROFILE.currentVersion()
        );

        this.expectedPlayerId = Objects.requireNonNull(
                expectedPlayerId,
                "Expected player ID cannot be null"
        );
        this.configSettings = Objects.requireNonNull(
                configSettings,
                "Config settings cannot be null"
        );
    }

    @Override
    protected void validateFile(List<String> errors) {
        validatePlayerId(errors);
        validatePlayerName(errors);
        validateNotes(errors);
    }

    private void validatePlayerId(List<String> errors) {

        if (!configuration.isSet(PLAYER_ID_PATH)) {
            errors.add(
                    PLAYER_ID_PATH
                            + " is not set in "
                            + path.getFileName()
            );
            return;
        }

        if (!configuration.isString(PLAYER_ID_PATH)) {
            errors.add(
                    "Expected type String from "
                            + PLAYER_ID_PATH
                            + ", got an invalid data type in "
                            + path.getFileName()
            );
            return;
        }

        UUID playerId;

        try {
            playerId = UUID.fromString(
                    configuration.getString(PLAYER_ID_PATH)
            );
        } catch (IllegalArgumentException ignored) {
            errors.add(
                    "Malformed UUID found in "
                            + PLAYER_ID_PATH
                            + " in "
                            + path.getFileName()
            );
            return;
        }

        String rawId = configuration.getString(PLAYER_ID_PATH);
        UUID parsed = UUID.fromString(rawId);

        if (!parsed.toString().equals(rawId)) {
            errors.add(
                    "Non-canonical UUID found in "
                            + PLAYER_ID_PATH
                            + " in "
                            + path.getFileName()
            );
        }

        if (!expectedPlayerId.equals(playerId)) {
            errors.add(
                    PLAYER_ID_PATH
                            + " does not match profile filename in "
                            + path.getFileName()
            );
        }
    }

    private void validatePlayerName(List<String> errors) {

        if (!configuration.isSet(PLAYER_NAME_PATH)) {
            errors.add(
                    PLAYER_NAME_PATH
                            + " is not set in "
                            + path.getFileName()
            );
            return;
        }

        if (!configuration.isString(PLAYER_NAME_PATH)) {
            errors.add(
                    PLAYER_NAME_PATH
                            + " is not of type String in "
                            + path.getFileName()
            );
            return;
        }

        String playerName =
                configuration.getString(PLAYER_NAME_PATH);

        if (!playerName.equals(
                ModerationConstants.UNKNOWN_PLAYER_NAME
        ) && !PlayerNames.isValid(playerName)) {
            errors.add(
                    "Malformed player name found in "
                            + PLAYER_NAME_PATH
                            + " in "
                            + path.getFileName()
            );
        }
    }

    private void validateNotes(List<String> errors) {

        if (!configuration.isSet(NOTES_PATH)) {
            errors.add(
                    NOTES_PATH
                            + " is not set in "
                            + path.getFileName()
            );
            return;
        }

        if (!configuration.isList(NOTES_PATH)) {
            errors.add(
                    NOTES_PATH
                            + " is not of type List in "
                            + path.getFileName()
            );
            return;
        }

        List<?> noteMaps = configuration.getList(NOTES_PATH);

        if (noteMaps == null) {
            errors.add(
                    "Null note list in "
                            + path.getFileName()
            );
            return;
        }

        for (int i = 0; i < noteMaps.size(); i++) {
            Object value = noteMaps.get(i);
            if (!(value instanceof Map<?, ?> noteMap)) {
                errors.add(
                        "Invalid note entry type: "
                                + value
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + i
                );
                continue;
            }

            if (!NOTE_FIELDS.equals(noteMap.keySet())) {
                errors.add(
                        "Invalid note entry keys: "
                                + noteMap.keySet()
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + i
                );
            }

            for (Map.Entry<?, ?> entry : noteMap.entrySet()) {

                if (!(entry.getKey() instanceof String field)) {
                    errors.add(
                            "Invalid note entry key type: "
                                    + entry.getKey()
                                    + " at index "
                                    + i
                                    + " in "
                                    + path.getFileName()
                    );
                    continue;
                }


                validateNoteEntry(
                        field,
                        entry.getValue(),
                        errors,
                        i
                );
            }
        }
    }

    private void validateNoteEntry(String field, Object value, List<String> errors, int index) {

        switch (field) {
            case PlayerProfileSchema.NOTE_CONTENT -> validateNoteContent(
                    field, value, index, errors
            );

            case PlayerProfileSchema.NOTE_TIMESTAMP -> validateNoteTimestamp(
                    field, value, index, errors
            );

            case PlayerProfileSchema.NOTE_AUTHOR_ID -> validateNoteAuthorId(
                    field, value, index, errors
            );

            case PlayerProfileSchema.NOTE_AUTHOR_NAME -> validateNoteAuthorName(
                    field, value, index, errors
            );

            default -> errors.add(
                    "Unknown note entry path: "
                            + field
                            + " in "
                            + path.getFileName()
                            + " at index "
                            + index
            );
        }
    }

    private void validateNoteContent(String path, Object value, int index, List<String> errors) {
        if (!(value instanceof String content)) {
            errors.add(
                    "Invalid note entry value type for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        if (content.isBlank()) { // Empty notes are not allowed
            errors.add(
                    "Empty note content for "
                            + path
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        if (content.length() > configSettings.noteMaxContentLength()) {
            errors.add(
                    "Note content exceeds maximum length of "
                            + configSettings.noteMaxContentLength()
                            + " characters..."
            );
        }
    }

    private void validateNoteAuthorName(String path, Object value, int index, List<String> errors) {
        if (!(value instanceof String content)) {
            errors.add(
                    "Invalid note entry value type for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }
        if (!PlayerNames.isValid(content)) {
            errors.add(
                    "Invalid Minecraft username entry for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
        }
    }

    private void validateNoteAuthorId(String path, Object value, int index, List<String> errors) {
        if (!(value instanceof String content)) {
            errors.add(
                    "Invalid note entry value type for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }
        if (content.isEmpty()) {
            errors.add(
                    "Empty note entry found in "
                            + path
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        UUID authorId;

        try {
            authorId = UUID.fromString(content);
        } catch (IllegalArgumentException ignored) {
            errors.add(
                    "Malformed UUID found in "
                            + path
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        if (!authorId.toString().equals(content)) {
            errors.add(
                    "Non-canonical UUID found in "
                            + path
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
        }

        try {
            UUID.fromString(content);
        } catch (IllegalArgumentException ignored) {

        }
    }

    private void validateNoteTimestamp(String path, Object value, int index, List<String> errors) {
        if (!(value instanceof Long timestamp)) {
            errors.add(
                    "Invalid note entry value type for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        if (timestamp < 0) {
            errors.add(
                    "Invalid note entry timestamp for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
        }
        if (timestamp > System.currentTimeMillis() + Note.MAX_FUTURE_SKEW_MILLIS) { // Allow a 24-hour buffer for clock skew
            errors.add(
                    "Note entry timestamp is in the future for "
                            + path
                            + ": "
                            + value
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
        }
    }
}