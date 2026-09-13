package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.PlayerNames;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.time.Duration;
import java.util.*;
import java.util.regex.Pattern;

public final class PlayerProfileYmlValidation extends YamlValidator {

    public static final int CURRENT_CONFIG_VERSION = 1;

    private static final String PLAYER_ID_PATH = "player-id";
    private static final String PLAYER_NAME_PATH = "player-name";
    private static final String NOTES_PATH = "notes";

    private final UUID expectedPlayerId;


    public PlayerProfileYmlValidation(
            Path path,
            FileConfiguration configuration,
            UUID expectedPlayerId
    ) {
        super(
                path,
                configuration,
                CURRENT_CONFIG_VERSION
        );

        this.expectedPlayerId = Objects.requireNonNull(
                expectedPlayerId,
                "Expected player ID cannot be null"
        );

        addValidation(this::validatePlayerId);
        addValidation(this::validatePlayerName);
        addValidation(this::validateNotes);
    }

    private void validatePlayerId(List<String> errors) {

        if(!configuration.isSet(PLAYER_ID_PATH)) {
            errors.add(
                    PLAYER_ID_PATH
                            + " is not set in "
                            + path.getFileName()
            );
            return;
        }

        if(!configuration.isString(PLAYER_ID_PATH)) {
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
        } catch(IllegalArgumentException ignored) {
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

        if(!parsed.toString().equals(rawId)) {
            errors.add(
                    "Non-canonical UUID found in "
                            + PLAYER_ID_PATH
                            + " in "
                            + path.getFileName()
            );
        }

        if(!expectedPlayerId.equals(playerId)) {
            errors.add(
                    PLAYER_ID_PATH
                            + " does not match profile filename in "
                            + path.getFileName()
            );
        }
    }

    private void validatePlayerName(List<String> errors) {

        if(!configuration.isSet(PLAYER_NAME_PATH)) {
            errors.add(
                    PLAYER_NAME_PATH
                            + " is not set in "
                            + path.getFileName()
            );
            return;
        }

        if(!configuration.isString(PLAYER_NAME_PATH)) {
            errors.add(
                    PLAYER_NAME_PATH
                            + " is not of type String in "
                            + path.getFileName()
            );
            return;
        }

        String playerName =
                configuration.getString(PLAYER_NAME_PATH);

        if(!PlayerNames.isValid(playerName)) {
            errors.add(
                    "Malformed player name found in "
                            + PLAYER_NAME_PATH
                            + " in "
                            + path.getFileName()
            );
        }
    }

    private static final Set<String> NOTE_FIELDS = Set.of(
            "content",
            "timestamp",
            "author-id",
            "author-name"
    );

    private void validateNotes(List<String> errors) {

        if(!configuration.isSet(NOTES_PATH)) {
            errors.add(
                    NOTES_PATH
                            + " is not set in "
                            + path.getFileName()
            );
            return;
        }

        if(!configuration.isList(NOTES_PATH)) {
            errors.add(
                    NOTES_PATH
                            + " is not of type List in "
                            + path.getFileName()
            );
            return;
        }

        List<?> noteMaps = configuration.getList(NOTES_PATH);

        if(noteMaps == null) {
            errors.add(
                    "Null note list in "
                            + path.getFileName()
            );
            return;
        }

        for(int i = 0; i < noteMaps.size(); i++) {
            Object value = noteMaps.get(i);
            if(!(value instanceof Map<?, ?> noteMap)) {
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

            if(!NOTE_FIELDS.equals(noteMap.keySet())) {
                errors.add(
                        "Invalid note entry keys: "
                                + noteMap.keySet()
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + i
                );
            }

            for(Map.Entry<?, ?> entry : noteMap.entrySet()) {

                if(!(entry.getKey() instanceof String field)) {
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
            case "content" -> validateNoteContent(field, value, index, errors);
            case "timestamp" -> validateNoteTimestamp(field, value, index, errors);
            case "author-id" -> validateNoteAuthorId(field, value, index, errors);
            case "author-name" -> validateNoteAuthorName(field, value, index, errors);
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

    private static final int MAX_NOTE_LENGTH = 512;

    private void validateNoteContent(String path, Object value, int index, List<String> errors) {
        if(!(value instanceof String content)) {
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

        if(content.isBlank()){ // Empty notes are not allowed
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

        if(content.length() > MAX_NOTE_LENGTH) { // Arbitrary limit to prevent abuse
            errors.add(
                    "Note content exceeds maximum length for "
                            + path
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
        }
    }
    private void validateNoteAuthorName(String path, Object value, int index, List<String> errors) {
        if(!(value instanceof String content)) {
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
        if(!PlayerNames.isValid(content)) {
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
        if(!(value instanceof String content)) {
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
        if(content.isEmpty()) {
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

        try {
            UUID.fromString(content);
        } catch(IllegalArgumentException ignored) {
            errors.add(
                    "Malformed UUID found in "
                            + path
                            + " in "
                            + this.path.getFileName()
                            + " at index "
                            + index
            );
        }
    }
    private void validateNoteTimestamp(String path, Object value, int index, List<String> errors) {
        if(!(value instanceof Long timestamp)) {
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

        if(timestamp < 0) {
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
        if(timestamp > System.currentTimeMillis() + Duration.ofHours(24).toMillis()) { // Allow a 24-hour buffer for clock skew
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