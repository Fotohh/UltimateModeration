package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public class Ban extends Infraction {
    public Ban(String reason, UUID punisher, UUID punished) {
        super(reason, punisher, punished);
    }

    @Override
    InfractionType getInfractionType() {
        return null;
    }
}
