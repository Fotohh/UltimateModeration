package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public class Warning implements Infraction{
    @Override
    public String getDateOfInfraction() {
        return null;
    }

    @Override
    public void setDateOfInfraction() {

    }

    @Override
    public String getReason() {
        return null;
    }

    @Override
    public UUID getPunisher() {
        return null;
    }

    @Override
    public UUID getPunished() {
        return null;
    }

    @Override
    public InfractionType getInfractionType() {
        return null;
    }
}
