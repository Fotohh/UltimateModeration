package me.xaxis.ultimatemoderation.lang;

public enum Placeholders {

    PLAYER_NAME("player"),
    REASON("reason"),
    DURATION("duration"),
    STAFF_NAME("staff_name"),
    ;

    private final String placeholder;

    Placeholders(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
