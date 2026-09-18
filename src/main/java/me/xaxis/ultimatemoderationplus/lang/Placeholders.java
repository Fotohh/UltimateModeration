package me.xaxis.ultimatemoderationplus.lang;

public enum Placeholders {

    PLAYER("player"),
    NOTE_CONTENT("note-content"),
    NOTE_INDEX("note-index"),
    NOTE_AUTHOR("note-author"),
    CONTENT_MAX_LENGTH("content-max-long"),
    VALUE("value"),
    WARN_CONTENT("warn-content"),
    WARN_AUTHOR("warn-author"),
    REASON("reason");

    private final String placeholder;

    Placeholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
