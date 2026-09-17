package me.xaxis.ultimatemoderationplus.constants;

import java.util.Set;

public final class PlayerProfileSchema {

    public static final String PLAYER_ID = "player-id";
    public static final String PLAYER_NAME = "player-name";
    public static final String NOTES = "notes";
    public static final String WARNINGS = "warnings";
    public static final String MUTE = "mute";

    public static final String NOTE_AUTHOR_ID = "author-id";
    public static final String NOTE_AUTHOR_NAME = "author-name";
    public static final String NOTE_CONTENT = "content";
    public static final String NOTE_TIMESTAMP = "timestamp";

    public static final String WARNING_STAFF_ID = "author-id";
    public static final String WARNING_STAFF_NAME = "author-name";
    public static final String WARNING_CONTENT = "content";
    public static final String WARNING_TIMESTAMP = "timestamp";
    public static final String WARNING_TARGET_ID = "player-id";

    public static final String MUTE_STAFF_ID = "staff-id";
    public static final String MUTE_STAFF_NAME = "staff-name";
    public static final String MUTE_REASON = "reason";
    public static final String MUTE_TIMESTAMP = "timestamp";
    public static final String MUTE_TARGET_ID = "player-id";

    public static final Set<String> NOTE_FIELDS = Set.of(
            NOTE_AUTHOR_ID,
            NOTE_AUTHOR_NAME,
            NOTE_CONTENT,
            NOTE_TIMESTAMP
    );

    public static final Set<String> WARNING_FIELDS = Set.of(
            WARNING_STAFF_ID,
            WARNING_STAFF_NAME,
            WARNING_CONTENT,
            WARNING_TIMESTAMP,
            WARNING_TARGET_ID
    );

    private PlayerProfileSchema() {}
}
