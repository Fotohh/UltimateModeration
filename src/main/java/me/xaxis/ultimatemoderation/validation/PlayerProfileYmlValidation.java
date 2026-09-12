package me.xaxis.ultimatemoderation.validation;

import org.bukkit.configuration.file.FileConfiguration;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;

public final class PlayerProfileYmlValidation
        extends YamlValidator {

    private static final Pattern PLAYER_NAME_PATTERN =
            Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    private static final int CURRENT_CONFIG_VERSION = 1;

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

        if(!PLAYER_NAME_PATTERN.matcher(playerName).matches()) {
            errors.add(
                    "Malformed player name found in "
                            + PLAYER_NAME_PATH
                            + " in "
                            + path.getFileName()
            );
        }
    }

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

        List<?> notes = configuration.getList(NOTES_PATH);

        for(int i = 0; i < notes.size(); i++) {
            if(!(notes.get(i) instanceof String)) {
                errors.add(
                        NOTES_PATH
                                + "[" + i + "] is not of type String in "
                                + path.getFileName()
                );
            }
        }
    }
}