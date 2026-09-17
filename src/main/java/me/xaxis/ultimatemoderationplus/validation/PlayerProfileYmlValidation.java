package me.xaxis.ultimatemoderationplus.validation;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.constants.PlayerProfileSchema;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public final class PlayerProfileYmlValidation extends YamlValidator {

    public PlayerProfileYmlValidation(
            Path path,
            FileConfiguration configuration
    ) {
        super(
                path,
                configuration,
                ConfigConstants.PLAYER_PROFILE.currentVersion()
        );
    }

    @Override
    protected void validateFile(List<String> errors) {
        validatePlayerId(errors);
        validatePlayerName(errors);
        validateNotes(errors);
        validateWarnings(errors);
        validateMute(errors);
    }

    private void validateMute(List<String> errors) {
        if (!configuration.isSet(PlayerProfileSchema.MUTE)) {
            return;
        }

        ConfigurationSection muteSection =
                configuration.getConfigurationSection(
                        PlayerProfileSchema.MUTE
                );

        if (muteSection == null) {
            errors.add(
                    PlayerProfileSchema.MUTE
                            + " is not a configuration section in "
                            + path.getFileName()
            );
            return;
        }

        ValidatorHelper.validateUuidValue(
                muteSection.get(
                        PlayerProfileSchema.MUTE_STAFF_ID
                ),
                PlayerProfileSchema.MUTE_STAFF_ID,
                -1,
                "mute",
                errors,
                path.getFileName().toString()
        );

        ValidatorHelper.validateMinecraftName(
                muteSection.get(
                        PlayerProfileSchema.MUTE_STAFF_NAME
                ),
                PlayerProfileSchema.MUTE_STAFF_NAME,
                -1,
                "mute",
                errors,
                path.getFileName().toString()
        );

        ValidatorHelper.validateTimestamp(
                muteSection.get(
                        PlayerProfileSchema.MUTE_TIMESTAMP
                ),
                PlayerProfileSchema.MUTE_TIMESTAMP,
                -1,
                "mute",
                ConfigConstants.MAX_FUTURE_SKEW_MILLIS,
                errors,
                path.getFileName().toString()
        );

        ValidatorHelper.validateNonBlankString(
                muteSection.get(
                        PlayerProfileSchema.MUTE_REASON
                ),
                PlayerProfileSchema.MUTE_REASON,
                -1,
                "mute",
                errors,
                path.getFileName().toString()
        );

        ValidatorHelper.validateUuidValue(
                muteSection.get(
                        PlayerProfileSchema.MUTE_TARGET_ID
                ),
                PlayerProfileSchema.MUTE_TARGET_ID,
                -1,
                "mute",
                errors,
                path.getFileName().toString()
        );

        Object profileId =
                configuration.get(
                        PlayerProfileSchema.PLAYER_ID
                );

        Object muteTargetId =
                muteSection.get(
                        PlayerProfileSchema.MUTE_TARGET_ID
                );

        if (profileId instanceof String profileIdString
                && muteTargetId instanceof String targetIdString
                && !profileIdString.equals(targetIdString)) {

            errors.add(
                    "Mute target ID does not match profile player ID in "
                            + path.getFileName()
            );
        }
    }

    private void validatePlayerId(List<String> errors) {
        ValidatorHelper.validateUuidValue(
                configuration.get(PlayerProfileSchema.PLAYER_ID),
                PlayerProfileSchema.PLAYER_ID,
                -1,
                "player profile",
                errors,
                path.getFileName().toString()
        );
    }

    private void validatePlayerName(List<String> errors) {
        ValidatorHelper.validateMinecraftName(
                configuration.get(PlayerProfileSchema.PLAYER_NAME),
                PlayerProfileSchema.PLAYER_NAME,
                -1,
                "player profile",
                errors,
                path.getFileName().toString()
        );
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
                    ValidatorHelper.validateUuidValue(value, field, index, "note", errors, path.getFileName().toString());
            case PlayerProfileSchema.NOTE_AUTHOR_NAME ->
                    ValidatorHelper.validateMinecraftName(value, field, index, "note", errors, path.getFileName().toString());
            case PlayerProfileSchema.NOTE_CONTENT ->
                    ValidatorHelper.validateNonBlankString(value, field, index, "note", errors, path.getFileName().toString());
            case PlayerProfileSchema.NOTE_TIMESTAMP ->
                    ValidatorHelper.validateTimestamp(
                            value,
                            field,
                            index,
                            "note",
                            ConfigConstants.MAX_FUTURE_SKEW_MILLIS,
                            errors,
                            path.getFileName().toString()
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

            ValidatorHelper.validateUuidValue(
                    warningMap.get(PlayerProfileSchema.WARNING_TARGET_ID),
                    PlayerProfileSchema.WARNING_TARGET_ID,
                    index,
                    "warning",
                    errors,
                    path.getFileName().toString()
            );
            ValidatorHelper.validateUuidValue(
                    warningMap.get(PlayerProfileSchema.WARNING_STAFF_ID),
                    PlayerProfileSchema.WARNING_STAFF_ID,
                    index,
                    "warning",
                    errors,
                    path.getFileName().toString()
            );
            ValidatorHelper.validateMinecraftName(
                    warningMap.get(PlayerProfileSchema.WARNING_STAFF_NAME),
                    PlayerProfileSchema.WARNING_STAFF_NAME,
                    index,
                    "warning",
                    errors,
                    path.getFileName().toString()
            );
            ValidatorHelper.validateNonBlankString(
                    warningMap.get(PlayerProfileSchema.WARNING_CONTENT),
                    PlayerProfileSchema.WARNING_CONTENT,
                    index,
                    "warning",
                    errors,
                    path.getFileName().toString()
            );
            ValidatorHelper.validateTimestamp(
                    warningMap.get(PlayerProfileSchema.WARNING_TIMESTAMP),
                    PlayerProfileSchema.WARNING_TIMESTAMP,
                    index,
                    "warning",
                    ConfigConstants.MAX_FUTURE_SKEW_MILLIS,
                    errors,
                    path.getFileName().toString()
            );
        }
    }


}
