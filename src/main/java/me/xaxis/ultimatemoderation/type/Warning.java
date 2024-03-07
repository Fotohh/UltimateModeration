package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public class Warning extends Infraction {

    public Warning(String reason, UUID punisher, UUID punished) {
        super(reason, punisher, punished, 0);
    }

    @Override
    InfractionType getInfractionType() {
        return InfractionType.WARNING;
    }
}
