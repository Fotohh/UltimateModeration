package me.xaxis.ultimatemoderation.player;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.file.PlayerProfileYML;
import me.xaxis.ultimatemoderation.type.InfractionType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class PlayerProfile extends PlayerProfileYML {

    private static final HashMap<UUID, PlayerProfile> map = new HashMap<>();

    public static boolean containsPlayer(UUID playerUUID){
        return map.containsKey(playerUUID);
    }

    public static void addPlayerProfile(UUID playerUUID, PlayerProfile profile){
        map.putIfAbsent(playerUUID, profile);
    }

    public static PlayerProfile getPlayerProfile(UUID playerUUID){
        return map.get(playerUUID);
    }

    public static void removePlayerProfile(UUID playerUUID){
        map.remove(playerUUID);
    }

    private final UMP plugin;

    private final Player player;

    public PlayerProfile(Player player, UMP plugin){
        super(plugin.getDataFolder() + "/player_data", player.getUniqueId().toString(), plugin, player);
        this.plugin = plugin;
        this.player = getPlayer(Defaults.PLAYER_UUID.getPath());
        PlayerProfile.addPlayerProfile(player.getUniqueId(), this);
    }

    public boolean isMuted(){
        return getBoolean(Defaults.IS_MUTED);
    }

    public boolean isBanned(){
        return getBoolean(Defaults.IS_BANNED);
    }

    public void addInfraction(InfractionType type){
        switch (type) {
            case MUTE, TEMP_MUTE -> set(Defaults.NUM_OF_MUTES, getInt(Defaults.NUM_OF_MUTES) + 1);
            case KICK -> set(Defaults.NUM_OF_KICKS, getInt(Defaults.NUM_OF_KICKS) + 1);
            case WARNING -> set(Defaults.NUM_OF_WARNINGS, getInt(Defaults.NUM_OF_WARNINGS) + 1);
            case IP_BAN,PERM_BAN,TEMP_BAN -> set(Defaults.NUM_OF_BANS, getInt(Defaults.NUM_OF_BANS) + 1);
        }
    }

    public void setInfractionAmount(InfractionType type, int amount){
        switch (type){
            case MUTE, TEMP_MUTE -> set(Defaults.NUM_OF_MUTES, amount);
            case KICK -> set(Defaults.NUM_OF_KICKS, amount);
            case WARNING -> set(Defaults.NUM_OF_WARNINGS, amount);
            case IP_BAN,PERM_BAN,TEMP_BAN -> set(Defaults.NUM_OF_BANS, amount);
        }
    }

    public int getInfractionAmount(InfractionType type){
        return switch (type){
            case MUTE, TEMP_MUTE -> getInt(Defaults.NUM_OF_MUTES);
            case KICK -> getInt(Defaults.NUM_OF_KICKS);
            case WARNING -> getInt(Defaults.NUM_OF_WARNINGS);
            case IP_BAN, PERM_BAN, TEMP_BAN -> getInt(Defaults.NUM_OF_BANS);
        };
    }

    public void deleteProfile(){
        if(delete()){
            PlayerProfile.removePlayerProfile(player.getUniqueId());
        }else{
            plugin.getLogger().severe("Unable to delete player profile! UUID: " + player.getUniqueId() + ", File Path: " + getPath());
        }
    }

}
