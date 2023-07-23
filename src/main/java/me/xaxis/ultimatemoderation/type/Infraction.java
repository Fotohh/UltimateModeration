package me.xaxis.ultimatemoderation.type;

import java.util.UUID;

public interface Infraction {
    String getDateOfInfraction();
    void setDateOfInfraction();
    String getReason();
    UUID getPunisher();
    UUID getPunished();
    InfractionType getInfractionType();

}
