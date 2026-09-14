package me.xaxis.ultimatemoderation.lang;

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
    UNABLE_TO_EXECUTE_COMMAND("messages.unable-to-execute-command"),
    NOTE_TOO_LONG("messages.note-too-long"),
    NO_NOTES("messages.no-notes"),
    NOTE_LIST_HEADER("messages.note-list-header"),
    NOTE_LIST_ENTRY("messages.note-list-entry"),
    ;
    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
