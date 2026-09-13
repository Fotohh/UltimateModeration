package me.xaxis.ultimatemoderation.constants;

import java.util.Set;

public final class PlayerProfileSchema {

    public static final String PLAYER_ID = "player-id";
    public static final String PLAYER_NAME = "player-name";
    public static final String NOTES = "notes";

    public static final String NOTE_AUTHOR_ID = "author-id";
    public static final String NOTE_AUTHOR_NAME = "author-name";
    public static final String NOTE_CONTENT = "content";
    public static final String NOTE_TIMESTAMP = "timestamp";

    public static final Set<String> NOTE_FIELDS = Set.of(
            NOTE_AUTHOR_ID,
            NOTE_AUTHOR_NAME,
            NOTE_CONTENT,
            NOTE_TIMESTAMP
    );

    private PlayerProfileSchema() {}
}
