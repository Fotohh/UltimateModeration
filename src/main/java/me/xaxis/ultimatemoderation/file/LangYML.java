package me.xaxis.ultimatemoderation.file;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public class LangYML extends File {

    private final FileConfiguration configuration;

    private final UMP plugin;

    public LangYML(@NotNull File path, UMP plugin) {
        super(path, "Lang.yml");

        this.plugin = plugin;

        if(!exists()){
            try {
                createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Unable to create Lang.yml in path ".concat(path.getPath()), e);
            }
        }

        configuration = YamlConfiguration.loadConfiguration(this);

        checkDefaultValues();

    }

    public void checkDefaultValues(){
        boolean changed = false;

        for(Lang lang : Lang.values()){
            if(configuration.getString(lang.getPath()) == null){
                configuration.set(lang.getPath(), lang.getDefaultValue());
                changed = true;
            }
        }

        if(changed) save();
    }

    public void save(){
        try {
            configuration.save(this);
        } catch (IOException e) {
            throw new RuntimeException("Unable to save Lang.yml!",e);
        }
    }


    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public String getString(Lang lang){
        return Utils.chat(getConfiguration().getString(lang.getPath()));
    }

}
