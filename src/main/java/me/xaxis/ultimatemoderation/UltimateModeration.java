package me.xaxis.ultimatemoderation;

import io.github.xaxisplayz.reportplus.api.ReportPlus;
import me.xaxis.ultimatemoderation.chat.StaffChat;
import org.bukkit.plugin.java.JavaPlugin;

public class UltimateModeration extends JavaPlugin {

    private StaffChat staffChat;

    @Override
    public void onEnable() {

        if(getServer().getPluginManager().getPlugin("ReportPlus") == null){
            getLogger().warning("Didn't find ReportPlus. Add this plugin so that UltimateModeration+ can hook into it.");
        }

        ReportPlus reportPlus = new ReportPlus(this);
        staffChat = new StaffChat(this);

    }

    @Override
    public void onDisable() {



    }

    public StaffChat getStaffChat() {
        return staffChat;
    }
}
