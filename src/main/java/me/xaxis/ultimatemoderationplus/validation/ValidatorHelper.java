package me.xaxis.ultimatemoderationplus.validation;

import me.xaxis.ultimatemoderationplus.constants.PlayerNames;

import java.util.List;
import java.util.UUID;

public final class ValidatorHelper {

    private ValidatorHelper(){
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static void validateUuidValue(
            Object value,
            String field,
            int index,
            String entryType,
            List<String> errors,
            String fileName
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
                            + fileName
                            + " at index "
                            + index
            );
            return;
        }

        parseCanonicalUuid(rawId, field, index, entryType, errors, fileName);
    }

    public static UUID parseCanonicalUuid(
            String rawId,
            String field,
            int index,
            String entryType,
            List<String> errors,
            String fileName
    ) {
        if (rawId == null || rawId.isBlank()) {
            errors.add(
                    "Empty "
                            + entryType
                            + " UUID for "
                            + field
                            + locationSuffix(index, fileName)
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
                            + locationSuffix(index, fileName)
            );
            return null;
        }

        if (!parsed.toString().equals(rawId)) {
            errors.add(
                    "Non-canonical UUID found in "
                            + field
                            + locationSuffix(index, fileName)
            );
        }

        return parsed;
    }

    public static void validateMinecraftName(
            Object value,
            String field,
            int index,
            String entryType,
            List<String> errors,
            String fileName
    ) {
        if (!(value instanceof String name)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + locationSuffix(index, fileName)
            );
            return;
        }

        if (!PlayerNames.isValid(name)) {
            errors.add(
                    "Invalid Minecraft username entry for "
                            + field
                            + ": "
                            + name
                            + locationSuffix(index, fileName)
            );
        }
    }

    public static void validateNonBlankString(
            Object value,
            String field,
            int index,
            String entryType,
            List<String> errors,
            String fileName
    ) {
        if (!(value instanceof String content)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + locationSuffix(index, fileName)
            );
            return;
        }

        if (content.isBlank()) {
            errors.add(
                    "Blank "
                            + entryType
                            + " content for "
                            + field
                            + locationSuffix(index, fileName)
            );
        }
    }

    public static void validateTimestamp(
            Object value,
            String field,
            int index,
            String entryType,
            long maxFutureSkewMillis,
            List<String> errors,
            String fileName
    ) {
        if (!(value instanceof Long timestamp)) {
            errors.add(
                    "Invalid "
                            + entryType
                            + " entry value type for "
                            + field
                            + ": "
                            + value
                            + locationSuffix(index, fileName)
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
                            + locationSuffix(index, fileName)
            );
        }

        if (timestamp > System.currentTimeMillis() + maxFutureSkewMillis) {
            errors.add(
                    entryType
                            + " timestamp is too far in the future for "
                            + field
                            + ": "
                            + timestamp
                            + locationSuffix(index, fileName)
            );
        }
    }

    private static String locationSuffix(int index, String fileName) {
        if (index < 0) {
            return " in " + fileName;
        }

        return " in " + fileName + " at index " + index;
    }

}
