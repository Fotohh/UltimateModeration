package me.xaxis.ultimatemoderation.mute;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.file.YMLFile;
import org.bukkit.configuration.ConfigurationSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MuteManager extends YMLFile {

    private final UMP plugin;

    private final ConfigurationSection section;

    public MuteManager(UMP plugin) throws IOException {

        super(plugin, "Mutes");

        this.plugin = plugin;

        boolean created = false;

        if(getConfiguration().getConfigurationSection("muted_players") == null) {
            getConfiguration().createSection("muted_players");
            created = true;
        }

        section = getConfiguration().getConfigurationSection("muted_players");

        if(!created) {
            for (String s : section.getKeys(false)) {
                if (!section.isConfigurationSection(s)) {
                    section.set(s, null);
                    continue;
                }
                ConfigurationSection ps = section.getConfigurationSection(s);
                if (ps == null) continue;
                if (System.currentTimeMillis() >= ps.getLong("timestamp")) {
                    section.set(s, null);
                    continue;
                }
                mutedPlayers.add(UUID.fromString(s));
            }
        }
    }

    public void removeMutedPlayer(UUID muted){
        mutedPlayers.remove(muted);
        section.set(muted.toString(), null);
    }

    public ConfigurationSection getSection() {
        return section;
    }

    public void addMutedPlayer(UUID muted){
        if(mutedPlayers.contains(muted)) return;
        mutedPlayers.add(muted);
    }

    public List<UUID> getMutedPlayers() {
        return mutedPlayers;
    }

    private final List<UUID> mutedPlayers = new ArrayList<>();

}
