package me.xaxis.ultimatemoderationplus.constants;

import java.util.UUID;

public final class ModerationConstants {

    private ModerationConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final UUID CONSOLE_UUID =
            UUID.fromString("00000000-0000-0000-0000-000000000000");

    public static final String CONSOLE_NAME = "CONSOLE";

    public static final String UNKNOWN_PLAYER_NAME = "<unknown>";
}
