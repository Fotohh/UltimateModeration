package me.xaxis.ultimatemoderationplus.lang;

public enum Lang {

    // General
    NO_PERMISSION("messages.general.no-permission"),
    PLAYER_NOT_FOUND("messages.general.player-not-found"),
    SENDER_NOT_VALID("messages.general.sender-not-valid"),
    SENDER_NOT_PLAYER("messages.general.sender-not-player"),
    CONTENT_TOO_LONG("messages.general.content-too-long"),
    INVALID_NUMBER("messages.general.invalid-number"),
    PLAYER_NOT_ONLINE("messages.general.player-not-online"),
    INVALID_DURATION("messages.general.invalid-duration"),

    // Notes
    NOTE_COMMAND_USAGE("messages.notes.command-usage"),
    NOTE_ADD_COMMAND_USAGE("messages.notes.add-command-usage"),
    NOTE_DELETE_COMMAND_USAGE("messages.notes.delete-command-usage"),
    NO_DELETE_MESSAGE_INDEX("messages.notes.no-delete-message-index"),
    NOTE_DELETED("messages.notes.note-deleted"),
    NOTE_ADDED("messages.notes.note-added"),
    INVALID_NOTE_INDEX("messages.notes.invalid-note-index"),
    NOTE_MUST_HAVE_CONTENT("messages.notes.note-must-have-content"),
    NO_NOTES("messages.notes.no-notes"),
    NOTE_LIST_HEADER("messages.notes.list-header"),
    NOTE_LIST_ENTRY("messages.notes.list-entry"),

    // Warnings
    WARN_COMMAND_USAGE("messages.warnings.command-usage"),
    WARN_ADD_COMMAND_USAGE("messages.warnings.add-command-usage"),
    WARN_DELETE_COMMAND_USAGE("messages.warnings.delete-command-usage"),
    WARN_LIST_COMMAND_USAGE("messages.warnings.list-command-usage"),
    WARN_MUST_HAVE_REASON("messages.warnings.must-have-reason"),
    INVALID_WARN_INDEX("messages.warnings.invalid-index"),
    WARN_DELETED("messages.warnings.deleted"),
    NO_WARNS("messages.warnings.no-warns"),
    WARN_LIST_HEADER("messages.warnings.list-header"),
    WARN_LIST_ENTRY("messages.warnings.list-entry"),
    WARN_ADDED("messages.warnings.added"),

    // Kick
    KICK_COMMAND_USAGE("messages.kick.command-usage"),
    KICK_MUST_HAVE_REASON("messages.kick.must-have-reason"),
    KICKED_PLAYER("messages.kick.kicked-player"),

    // Mute
    MUTE_USAGE("messages.mute.usage"),
    MUTE_MUST_HAVE_REASON("messages.mute.must-have-reason"),
    MUTED_PLAYER("messages.mute.muted-player"),
    PLAYER_MUTED("messages.mute.player-muted"),
    UNMUTE_USAGE("messages.mute.unmute-usage"),
    PLAYER_NOT_MUTED("messages.mute.player-not-muted"),
    PLAYER_ALREADY_MUTED("messages.mute.player-already-muted"),
    PLAYER_UNMUTED("messages.mute.player-unmuted"),

    // Ban
    BAN_USAGE("messages.ban.usage"),
    BAN_MUST_HAVE_REASON("messages.ban.must-have-reason"),
    BANNED_PLAYER("messages.ban.banned-player"),
    PLAYER_ALREADY_BANNED("messages.ban.player-already-banned"),
    UNBAN_USAGE("messages.ban.unban-usage"),
    UNBANNED_PLAYER("messages.ban.unbanned-player"),
    PLAYER_NOT_BANNED("messages.ban.player-not-banned"),
    LOGIN_BAN_MESSAGE("messages.ban.login-message"),

    // Temporary mute
    TEMPMUTE_USAGE("messages.tempmute.usage"),
    TEMPMUTED_PLAYER("messages.tempmute.muted-player"),

    // Temporary ban
    TEMPBAN_USAGE("messages.tempban.usage"),
    TEMPBANNED_PLAYER("messages.tempban.banned-player");

    private final String path;

    Lang(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}