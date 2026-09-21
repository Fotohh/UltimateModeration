package me.xaxis.ultimatemoderationplus.lang;

public enum LangKey {

    NO_PERMISSION("messages.no-permission"),
    NOTE_COMMAND_USAGE("messages.note-command-usage"),
    PLAYER_NOT_FOUND("messages.player-not-found"),
    NOTE_ADD_COMMAND_USAGE("messages.note-add-command-usage"),
    NO_DELETE_MESSAGE_INDEX("messages.no-delete-message-index"),
    NOTE_DELETE_COMMAND_USAGE("messages.note-delete-command-usage"),
    NOTE_DELETED("messages.note-deleted"),
    NOTE_ADDED("messages.note-added"),
    INVALID_NOTE_INDEX("messages.invalid-note-index"),
    NOTE_MUST_HAVE_CONTENT("messages.note-must-have-content"),
    SENDER_NOT_VALID("messages.sender-not-valid"),
    CONTENT_TOO_LONG("messages.note-too-long"),
    NO_NOTES("messages.no-notes"),
    NOTE_LIST_HEADER("messages.note-list-header"),
    NOTE_LIST_ENTRY("messages.note-list-entry"),
    SENDER_NOT_PLAYER("messages.sender-not-player"),
    WARN_COMMAND_USAGE("messages.warn-command-usage"),
    WARN_ADD_COMMAND_USAGE("messages.warn-add-command-usage"),
    WARN_DELETE_COMMAND_USAGE("mesages.warn-delete-command-usage"),
    WARN_MUST_HAVE_REASON("messages.warn-must-have-reason"),
    INVALID_NUMBER("messages.invalid-number"),
    INVALID_WARN_INDEX("messages.invalid-warn-index"),
    WARN_DELETED("messages.warn-deleted"),
    WARN_LIST_COMMAND_USAGE("messages.warn-list-command-usage"),
    NO_WARNS("messages.no-warns"),
    WARN_LIST_HEADER("messages.warn-list-header"),
    WARN_LIST_ENTRY("messages.warn-list-entry"),
    WARN_ADDED("messages.warn-added"),
    KICK_COMMAND_USAGE("messages.kick-command-usage"),
    KICK_MUST_HAVE_REASON("messages.kick-must-have-reason"),
    PLAYER_NOT_ONLINE("messages.player-not-online"),
    KICKED_PLAYER("messages.kicked-player"),
    MUTE_USAGE("messages.mute-usage"),
    MUTE_MUST_HAVE_REASON("messages.mute-must-have-reason"),
    MUTED_PLAYER("messages.muted-player"),
    PLAYER_MUTED("messages.player-muted"),
    UNMUTE_USAGE("messages.unmute-usage"),
    PLAYER_NOT_MUTED("messages.player-not-muted"),
    PLAYER_ALREADY_MUTED("messages.player-already-muted"),
    PLAYER_UNMUTED("messages.player-unmuted"),
    BAN_USAGE("messages.ban-usage"),
    BAN_MUST_HAVE_REASON("messages.ban-must-have-reason"),
    BANNED_PLAYER("messages.banned-player"),
    PLAYER_ALREADY_BANNED("messages.player-already-banned"),
    UNBAN_USAGE("messages.unban-usage"),
    UNBANNED_PLAYER("messages.unbanned-player"),
    PLAYER_NOT_BANNED("messages.player-not-banned"),
    LOGIN_BAN_MESSAGE("messages.login-ban-message")
    ;
    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
