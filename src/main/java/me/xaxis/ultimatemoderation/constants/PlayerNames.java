package me.xaxis.ultimatemoderation.constants;

import java.util.regex.Pattern;

public final class PlayerNames {

    private static final Pattern PATTERN =
            Pattern.compile("^[A-Za-z0-9_]{3,16}$");

    private PlayerNames() {}

    public static boolean isValid(String name) {
        return name != null
                && PATTERN.matcher(name).matches();
    }
}
