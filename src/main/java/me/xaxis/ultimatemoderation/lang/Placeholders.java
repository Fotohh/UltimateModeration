package me.xaxis.ultimatemoderation.lang;

public enum Placeholders {

    PLAYER("player"),
    REASON("reason"),
    DURATION("duration"),
    STAFF("staff"),
    ;

    private final String placeholder;

    Placeholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
