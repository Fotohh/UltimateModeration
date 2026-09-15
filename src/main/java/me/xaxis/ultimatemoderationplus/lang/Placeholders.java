package me.xaxis.ultimatemoderationplus.lang;

public enum Placeholders {

    PLAYER("player"),
    NOTE_CONTENT("note-content"),
    NOTE_INDEX("note-index"),
    NOTE_AUTHOR("note-author"),
    NOTE_MAX_LENGTH("note-max-length")
    ;

    private final String placeholder;

    Placeholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
