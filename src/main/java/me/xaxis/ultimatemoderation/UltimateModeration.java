package me.xaxis.ultimatemoderation;

import org.bukkit.plugin.java.JavaPlugin;

public class UltimateModeration extends JavaPlugin {

    private static UltimateModeration instance;

    @Override
    public void onEnable() {
        instance = this;


    }

    @Override
    public void onDisable() {



    }

    public static UltimateModeration getInstance(){
        return instance;
    }

}
