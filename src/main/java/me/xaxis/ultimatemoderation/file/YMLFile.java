package me.xaxis.ultimatemoderation.file;

import com.google.common.base.Objects;
import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class YMLFile extends File {

    private final YamlConfiguration configuration;

    /**
     * Constructs a new YMLFile object representing a YAML file.
     *
     * @param main     The main plugin class.
     * @param fileName The name of the YAML file (without the extension).
     * @throws IOException If an error occurs during file operations.
     */
    public YMLFile(UMP main, String fileName) throws IOException {
        super(main.getDataFolder(), fileName + ".yml");

        if(!main.getDataFolder().exists()) main.getDataFolder().mkdirs();

        if (!exists()) createNewFile();

        this.configuration = YamlConfiguration.loadConfiguration(this);
    }

    /**
     * Constructs a new YMLFile object representing a YAML file within a specific folder directory.
     *
     * @param main       The main plugin class.
     * @param fileName   The name of the YAML file (without the extension).
     * @param folderDir  The directory path relative to the plugin's data folder.
     * @throws IOException If an error occurs during file operations.
     */
    public YMLFile(UMP main, String fileName, String folderDir) throws IOException {
        super(main.getDataFolder() + "/" + folderDir, fileName + ".yml");
        File folder = new File(main.getDataFolder() + "/" + folderDir);

        if (!folder.exists()) folder.mkdirs();

        if (!exists()) createNewFile();

        this.configuration = YamlConfiguration.loadConfiguration(this);
    }

    /**
     * Saves the configuration to the file.
     *
     * @throws IOException If an error occurs during file operations.
     */
    public void save() throws IOException {
        configuration.save(this);
    }

    /**
     * Retrieves the configuration object.
     *
     * @return The FileConfiguration object.
     */
    public FileConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Checks if this YMLFile object is equal to another object.
     *
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof YMLFile)) return false;
        YMLFile that = (YMLFile) o;
        return Objects.equal(getConfiguration(), that.getConfiguration()) && Objects.equal(this, that);
    }

    /**
     * Returns a string representation of the YMLFile object.
     *
     * @return The string representation.
     */
    @Override
    public String toString() {
        return "YMLFile{" +
                "configuration=" + configuration +
                ", file=" + this +
                '}';
    }

    public Location getLocation(ConfigurationSection section){
        return new Location(Bukkit.getWorld(UUID.fromString(section.getString("world_uuid"))),
                section.getDoubleList("coordinates").get(0), //x
                section.getDoubleList("coordinates").get(1), //y
                section.getDoubleList("coordinates").get(2), //z
                section.getFloatList("yaw_pitch").get(0), // yaw
                section.getFloatList("yaw_pitch").get(1));// pitch
    }

    public void setLocation(Location location, ConfigurationSection s){
        s.set("world_uuid", location.getWorld().getUID().toString());
        List<Double> list = new ArrayList<>();
        list.add(location.getX());
        list.add(location.getY());
        list.add(location.getZ());
        s.set("coordinates", list);
        List<Float> floats = new ArrayList<>();
        floats.add(location.getYaw());
        floats.add(location.getPitch());
        s.set("yaw_pitch", floats);
    }
}
