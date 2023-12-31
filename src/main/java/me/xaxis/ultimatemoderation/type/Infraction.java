package me.xaxis.ultimatemoderation.type;

import me.xaxis.ultimatemoderation.player.PlayerProfile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public abstract class Infraction {

    private final String reason;
    private final UUID punisher;
    private final UUID punished;
    private final long date;
    private final ConfigurationSection section;
    private final UUID infractionUUID;

    public UUID getInfractionUUID() {
        return infractionUUID;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public Infraction(String reason, UUID punisher, UUID punished, long date) {
        this.infractionUUID = UUID.randomUUID();
        this.section = PlayerProfile.getPlayerProfile(punished).getConfiguration().createSection(this.infractionUUID.toString());
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
