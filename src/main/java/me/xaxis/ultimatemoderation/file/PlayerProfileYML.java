package me.xaxis.ultimatemoderation.file;

import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerProfileYML extends File {

    protected enum Defaults{

        PLAYER_UUID("player_uuid", "null"),

        PLAYER_NAME("player_name", "null"),

        PLAYER_DISPLAY_NAME("player_display_name", "null"),

        LAST_JOIN_DATE("last_join_date", "null"),

        IS_MUTED("mute_settings.is_muted", false),

        IS_BANNED("ban_settings.is_banned", false),

        NUM_OF_MUTES("mute_settings.times_muted", 0),

        NUM_OF_WARNINGS("warn_settings.times_warned", 0),

        NUM_OF_BANS("ban_settings.times_banned", 0),

        NUM_OF_KICKS("kick_settings.times_kicked", 0),

        ADDITIONAL_NOTES("additional_notes", "null"),

        ;

        private final String path;
        private final Object defaultValue;

        Defaults(String path, Object defaultValue){
            this.path  = path;
            this.defaultValue = defaultValue;
        }

        public String getPath() {
            return path;
        }

        public Object getDefaultValue(){
            return defaultValue;
        }

    }

    private final FileConfiguration configuration;

    private final UMP plugin;

    public PlayerProfileYML(String path, @NotNull String child, UMP plugin, Player player) {
        super(path, child + ".yml");

        File folder = new File(path);

        if(!folder.exists()) folder.mkdirs();

        this.plugin = plugin;

        if(!exists()){
            try {
                createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create "+ child + " in path ".concat(path), e);
            }
        }

        configuration = YamlConfiguration.loadConfiguration(this);

        checkDefaults();

        getConfiguration().set(Defaults.PLAYER_UUID.getPath(), player.getUniqueId());
        getConfiguration().set(Defaults.PLAYER_NAME.getPath(), player.getName());
        getConfiguration().set(Defaults.PLAYER_DISPLAY_NAME.getPath(), player.getDisplayName());

        save();

    }

    public int getInt(Defaults path){
        return getConfiguration().getInt(path.getPath());
    }

    public boolean getBoolean(Defaults path){
        return getConfiguration().getBoolean(path.getPath());
    }

    public String getString(Defaults path){
        return getConfiguration().getString(path.getPath());
    }

    public void set(Defaults path, Object value){
        getConfiguration().set(path.getPath(), value);
    }

    public void save(){
        try {
            configuration.save(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save PlayerProfile.yml!",e);
        }
    }

    private void checkDefaults(){
        boolean a = false;
        for(Defaults d : Defaults.values()){
            if(getConfiguration().get(d.path) == null){
                getConfiguration().set(d.path,d.defaultValue);
                a = true;
            }
        }
        if(a) save();
    }

    /**
     * Gets a player from a path. This path is expected to return a {@link String} which will
     * then be cast to a {@link UUID} and return a player using {@link Bukkit#getPlayer(UUID)}.
     * @param path The path
     * @return The player or else null if the {@link String path} or UUID is invalid.
     */
    public Player getPlayer(@NotNull String path){

        return Bukkit.getPlayer(UUID.fromString(getConfiguration().getString(path)));
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }
}
