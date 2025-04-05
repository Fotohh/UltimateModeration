package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public class Kick extends Infraction {
    public Kick(String reason, UUID punisher, UUID punished) {
        super(reason, punisher, punished, System.currentTimeMillis(), InfractionType.KICK);
    }

    @Override
    InfractionType getInfractionType() {
        return null;
    }
}
