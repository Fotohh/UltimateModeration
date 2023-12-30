package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public class Kick extends Infraction {
    public Kick(String reason, UUID punisher, UUID punished, long date) {
        super(reason, punisher, punished, date);
    }

    @Override
    InfractionType getInfractionType() {
        return null;
    }
}
