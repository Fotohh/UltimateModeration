package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public abstract class Infraction {

    private final String reason;
    private final UUID punisher;
    private final UUID punished;
    private final long date;

    public Infraction(String reason, UUID punisher, UUID punished, long date) {
        this.reason = reason;
        this.punisher = punisher;
        this.punished = punished;
        this.date = date;
    }

    public long getDateOfInfraction(){
        return date;
    }

    public String getReason() {
        return reason;
    }

    public long getDate() {
        return date;
    }

    public UUID getPunished() {
        return punished;
    }

    public UUID getPunisher() {
        return punisher;
    }

    abstract InfractionType getInfractionType();

}
