package me.xaxis.ultimatemoderation.lang;

public enum LangKey {

    PREFIX("messages.prefix"),
    NO_PERMISSION("messages.no-permission"),
    ;

    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
