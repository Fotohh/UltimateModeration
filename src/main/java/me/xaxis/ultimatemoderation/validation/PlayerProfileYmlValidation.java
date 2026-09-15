package me.xaxis.ultimatemoderation.validation;

import me.xaxis.ultimatemoderation.constants.ConfigConstants;
import me.xaxis.ultimatemoderation.constants.ModerationConstants;
import me.xaxis.ultimatemoderation.constants.PlayerNames;
import me.xaxis.ultimatemoderation.constants.PlayerProfileSchema;
import me.xaxis.ultimatemoderation.player.Note;
import me.xaxis.ultimatemoderation.player.Warning;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class PlayerProfileYmlValidation extends YamlValidator {

    private final UUID expectedPlayerId;

    public PlayerProfileYmlValidation(
            Path path,
            FileConfiguration configuration,
            UUID expectedPlayerId
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
    }

    @Override
    protected void validateFile(List<String> errors) {
        validatePlayerId(errors);
        validatePlayerName(errors);
        validateNotes(errors);
        validateWarnings(errors);
    }

    private void validatePlayerId(List<String> errors) {
        String field = PlayerProfileSchema.PLAYER_ID;

        if (!configuration.isSet(field)) {
            errors.add(field + " is not set in " + path.getFileName());
            return;
        }

        if (!configuration.isString(field)) {
            errors.add(
                    "Expected type String from "
                            + field
                            + ", got an invalid data type in "
                            + path.getFileName()
            );
            return;
        }

        String rawId = configuration.getString(field);
        UUID playerId = parseCanonicalUuid(rawId, field, -1, "profile", errors);

        if (playerId != null && !expectedPlayerId.equals(playerId)) {
            errors.add(
                    field
                            + " does not match profile filename in "
                            + path.getFileName()
            );
        }
    }

    private void validatePlayerName(List<String> errors) {
        String field = PlayerProfileSchema.PLAYER_NAME;

        if (!configuration.isSet(field)) {
            errors.add(field + " is not set in " + path.getFileName());
            return;
        }

        if (!configuration.isString(field)) {
            errors.add(field + " is not of type String in " + path.getFileName());
            return;
        }

        String playerName = configuration.getString(field);

        if (!ModerationConstants.UNKNOWN_PLAYER_NAME.equals(playerName)
                && !PlayerNames.isValid(playerName)) {
            errors.add(
                    "Malformed player name found in "
                            + field
                            + " in "
                            + path.getFileName()
            );
        }
    }

    private void validateNotes(List<String> errors) {
        String listPath = PlayerProfileSchema.NOTES;

        if (!configuration.isSet(listPath)) {
            errors.add(listPath + " is not set in " + path.getFileName());
            return;
        }

        if (!configuration.isList(listPath)) {
            errors.add(listPath + " is not of type List in " + path.getFileName());
            return;
        }

        List<?> entries = configuration.getList(listPath);
        if (entries == null) {
            errors.add("Null note list in " + path.getFileName());
            return;
        }

        for (int index = 0; index < entries.size(); index++) {
            Object entry = entries.get(index);

            if (!(entry instanceof Map<?, ?> noteMap)) {
                errors.add(
                        "Invalid note entry type: "
                                + entry
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + index
                );
                continue;
            }

            if (!PlayerProfileSchema.NOTE_FIELDS.equals(noteMap.keySet())) {
                errors.add(
                        "Invalid note entry keys: "
                                + noteMap.keySet()
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + index
                );
            }

            validateNoteField(
                    PlayerProfileSchema.NOTE_AUTHOR_ID,
                    noteMap.get(PlayerProfileSchema.NOTE_AUTHOR_ID),
                    index,
                    errors
            );
            validateNoteField(
                    PlayerProfileSchema.NOTE_AUTHOR_NAME,
                    noteMap.get(PlayerProfileSchema.NOTE_AUTHOR_NAME),
                    index,
                    errors
            );
            validateNoteField(
                    PlayerProfileSchema.NOTE_CONTENT,
                    noteMap.get(PlayerProfileSchema.NOTE_CONTENT),
                    index,
                    errors
            );
            validateNoteField(
                    PlayerProfileSchema.NOTE_TIMESTAMP,
                    noteMap.get(PlayerProfileSchema.NOTE_TIMESTAMP),
                    index,
                    errors
            );
        }
    }

    private void validateNoteField(
            String field,
            Object value,
            int index,
            List<String> errors
    ) {
        switch (field) {
            case PlayerProfileSchema.NOTE_AUTHOR_ID ->
                    validateUuidValue(value, field, index, "note", errors);
            case PlayerProfileSchema.NOTE_AUTHOR_NAME ->
                    validateMinecraftName(value, field, index, "note", errors);
            case PlayerProfileSchema.NOTE_CONTENT ->
                    validateNonBlankString(value, field, index, "note", errors);
            case PlayerProfileSchema.NOTE_TIMESTAMP ->
                    validateTimestamp(
                            value,
                            field,
                            index,
                            "note",
                            Note.MAX_FUTURE_SKEW_MILLIS,
                            errors
                    );
            default -> throw new IllegalStateException("Unknown note field: " + field);
        }
    }

    private void validateWarnings(List<String> errors) {
        String listPath = PlayerProfileSchema.WARNINGS;

        if (!configuration.isSet(listPath)) {
            errors.add(listPath + " is not set in " + path.getFileName());
            return;
        }

        if (!configuration.isList(listPath)) {
            errors.add(listPath + " is not of type List in " + path.getFileName());
            return;
        }

        List<?> entries = configuration.getList(listPath);
        if (entries == null) {
            errors.add("Null warning list in " + path.getFileName());
            return;
        }

        for (int index = 0; index < entries.size(); index++) {
            Object entry = entries.get(index);

            if (!(entry instanceof Map<?, ?> warningMap)) {
                errors.add(
                        "Invalid warning entry type: "
                                + entry
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + index
                );
                continue;
            }

            if (!PlayerProfileSchema.WARNING_FIELDS.equals(warningMap.keySet())) {
                errors.add(
                        "Invalid warning entry keys: "
                                + warningMap.keySet()
                                + " in "
                                + path.getFileName()
                                + " at index "
                                + index
                );
            }

            validateWarningTargetId(
                    warningMap.get(PlayerProfileSchema.WARNING_TARGET_ID),
                    index,
                    errors
            );
            validateUuidValue(
                    warningMap.get(PlayerProfileSchema.WARNING_STAFF_ID),
                    PlayerProfileSchema.WARNING_STAFF_ID,
                    index,
                    "warning",
                    errors
            );
            validateMinecraftName(
                    warningMap.get(PlayerProfileSchema.WARNING_STAFF_NAME),
                    PlayerProfileSchema.WARNING_STAFF_NAME,
                    index,
                    "warning",
                    errors
            );
            validateNonBlankString(
                    warningMap.get(PlayerProfileSchema.WARNING_CONTENT),
                    PlayerProfileSchema.WARNING_CONTENT,
                    index,
                    "warning",
                    errors
            );
            validateTimestamp(
                    warningMap.get(PlayerProfileSchema.WARNING_TIMESTAMP),
                    PlayerProfileSchema.WARNING_TIMESTAMP,
                    index,
                    "warning",
                    Warning.MAX_FUTURE_SKEW_MILLIS,
                    errors
            );
        }
    }

    private void validateWarningTargetId(Object value, int index, List<String> errors) {
        if (!(value instanceof String rawId)) {
            errors.add(
                    "Invalid warning entry value type for "
                            + PlayerProfileSchema.WARNING_TARGET_ID
                            + ": "
                            + value
                            + " in "
                            + path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        UUID targetId = parseCanonicalUuid(
                rawId,
                PlayerProfileSchema.WARNING_TARGET_ID,
                index,
                "warning",
                errors
        );

        if (targetId != null && !expectedPlayerId.equals(targetId)) {
            errors.add(
                    "Warning target UUID does not match profile UUID in "
                            + path.getFileName()
                            + " at index "
                            + index
            );
        }
    }

    private void validateUuidValue(
            Object value,
            String field,
            int index,
            String entryType,
            List<String> errors
    ) {
        if (!(value instanceof String rawId)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + " in "
                            + path.getFileName()
                            + " at index "
                            + index
            );
            return;
        }

        parseCanonicalUuid(rawId, field, index, entryType, errors);
    }

    private UUID parseCanonicalUuid(
            String rawId,
            String field,
            int index,
            String entryType,
            List<String> errors
    ) {
        if (rawId == null || rawId.isBlank()) {
            errors.add(
                    "Empty "
                            + entryType
                            + " UUID for "
                            + field
                            + locationSuffix(index)
            );
            return null;
        }

        UUID parsed;
        try {
            parsed = UUID.fromString(rawId);
        } catch (IllegalArgumentException ignored) {
            errors.add(
                    "Malformed UUID found in "
                            + field
                            + locationSuffix(index)
            );
            return null;
        }

        if (!parsed.toString().equals(rawId)) {
            errors.add(
                    "Non-canonical UUID found in "
                            + field
                            + locationSuffix(index)
            );
        }

        return parsed;
    }

    private void validateMinecraftName(
            Object value,
            String field,
            int index,
            String entryType,
            List<String> errors
    ) {
        if (!(value instanceof String name)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + locationSuffix(index)
            );
            return;
        }

        if (!PlayerNames.isValid(name)) {
            errors.add(
                    "Invalid Minecraft username entry for "
                            + field
                            + ": "
                            + name
                            + locationSuffix(index)
            );
        }
    }

    private void validateNonBlankString(
            Object value,
            String field,
            int index,
            String entryType,
            List<String> errors
    ) {
        if (!(value instanceof String content)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + locationSuffix(index)
            );
            return;
        }

        if (content.isBlank()) {
            errors.add(
                    "Blank "
                            + entryType
                            + " content for "
                            + field
                            + locationSuffix(index)
            );
        }
    }

    private void validateTimestamp(
            Object value,
            String field,
            int index,
            String entryType,
            long maxFutureSkewMillis,
            List<String> errors
    ) {
        if (!(value instanceof Long timestamp)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + locationSuffix(index)
            );
            return;
        }

        if (timestamp < 0) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " timestamp for "
                            + field
                            + ": "
                            + timestamp
                            + locationSuffix(index)
            );
        }

        if (timestamp > System.currentTimeMillis() + maxFutureSkewMillis) {
            errors.add(
                    entryType
                            + " timestamp is too far in the future for "
                            + field
                            + ": "
                            + timestamp
                            + locationSuffix(index)
            );
        }
    }

    private String locationSuffix(int index) {
        if (index < 0) {
            return " in " + path.getFileName();
        }

        return " in " + path.getFileName() + " at index " + index;
    }
}
