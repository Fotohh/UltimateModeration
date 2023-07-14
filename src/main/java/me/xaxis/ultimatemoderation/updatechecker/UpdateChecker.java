package me.xaxis.ultimatemoderation.updatechecker;

import me.xaxis.ultimatemoderation.UMP;

public class UpdateChecker {

    public final UMP ump;

    private final double version;

    private static final String resourceId = "0";

    public UpdateChecker(UMP ump) {
        this.ump = ump;
        this.version = Double.parseDouble(ump.getDescription().getVersion());
    }


}
