package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public class Warning extends Infraction {

    public Warning(String reason, UUID punisher, UUID punished, long date) {
        super(reason, punisher, punished, date);
    }

    @Override
    InfractionType getInfractionType() {
        return InfractionType.WARNING;
    }
}
