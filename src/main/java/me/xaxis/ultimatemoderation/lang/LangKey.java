package me.xaxis.ultimatemoderation.lang;

public enum LangKey {

    PREFIX("messages.prefix"),
    NO_PERMISSION("messages.no-permission"),
    NOTE_COMMAND_USAGE("messages.note-command-usage"),
    PLAYER_NOT_FOUND("messages.player-not-found"),
    NOTE_ADD_COMMAND_USAGE("messages.note-add-command-usage"),


    ;

    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
