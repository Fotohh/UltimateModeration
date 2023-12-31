package me.xaxis.ultimatemoderation.type;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import org.bukkit.configuration.ConfigurationSection;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Mute{

    private final UMP plugin;

    private final ConfigurationSection section;

    public Mute(String reason, long timestamp, UUID muted, UUID muter) {
        this.plugin = UMP.instance;
        section = PlayerProfile.getPlayerProfile(muted).getConfiguration().createSection(UUID.randomUUID().toString());
        section.set("reason", reason);
        section.set("timestamp", timestamp);
        section.set("muted", muted.toString());
        section.set("muter", muter.toString());

        plugin.getMuteManager().addMutedPlayer(muted);
    }

    public Mute(UMP plugin, ConfigurationSection section){
        this.plugin = plugin;
        this.section = section;
    }

    public void setReason(String reason) {
        section.set("reason", reason);
    }

    public String getReason() {
        return section.getString("reason");
    }

    public void setDateOfInfraction(){
        section.set("date_of_infraction", LocalDateTime.now().toLocalDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    public String getDateOfInfraction(){
        return section.getString("date_of_infraction");
    }

    public void setTimestamp(long timestamp) {
        section.set("timestamp", timestamp);
    }

    public long getTimestamp() {
        return section.getLong("timestamp");
    }

    public UUID getMuted() {
        return UUID.fromString(section.getString("muted"));
    }

    public void setMuted(UUID muted) {
        section.set("muted", muted.toString());
    }

    public UUID getMuter() {
        return UUID.fromString(section.getString("muter"));
    }

    public void setMuter(UUID muter) {
        section.set("muter", muter.toString());
    }
}
