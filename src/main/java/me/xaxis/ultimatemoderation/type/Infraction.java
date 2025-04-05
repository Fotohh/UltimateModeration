package me.xaxis.ultimatemoderation.type;

import me.xaxis.ultimatemoderation.player.PlayerProfile;
import org.bukkit.configuration.ConfigurationSection;

import java.util.UUID;

public abstract class Infraction {

    private final ConfigurationSection section;
    private final UUID infractionUUID;

    public UUID getInfractionUUID() {
        return infractionUUID;
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public Infraction(String reason, UUID punisher, UUID punished, long time, InfractionType type) {
        this.infractionUUID = UUID.randomUUID();
        this.section = PlayerProfile.getPlayerProfile(punished).getConfiguration().createSection(this.infractionUUID.toString());
        section.set("reason", reason);
        section.set("punisher", punisher.toString());
        section.set("punished", punished.toString());
        section.set("date", System.currentTimeMillis());
        section.set("time", time);
        section.set("infraction_type", type.toString());
        PlayerProfile.getPlayerProfile(punished).save();
    }

    public long getTime(){
        return section.getLong("time");
    }

    public String getReason() {
        return section.getString("reason");
    }

    public long getDate() {
        return section.getLong("date");
    }

    public UUID getPunished() {
        return UUID.fromString(section.getString("punished"));
    }

    public UUID getPunisher() {
        return UUID.fromString(section.getString("punisher"));
    }

    public InfractionType getType() {
        return InfractionType.valueOf(section.getString("infraction_type"));
    }

    abstract InfractionType getInfractionType();

}
