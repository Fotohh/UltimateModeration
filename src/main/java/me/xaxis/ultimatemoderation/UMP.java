package me.xaxis.ultimatemoderation;

import me.xaxis.ultimatemoderation.chat.StaffChat;
import me.xaxis.ultimatemoderation.commands.StaffChatCommand;
import me.xaxis.ultimatemoderation.events.OnPlayerChat;
import me.xaxis.ultimatemoderation.events.UMPListener;
import me.xaxis.ultimatemoderation.file.LangYML;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class UMP extends JavaPlugin {

    private LangYML langYML;

    private StaffChat staffChat;

    private final HashMap<CommandExecutor, String> commands = new HashMap<>();

    private PlayerRollbackManager rollbackManager;

    private final ArrayList<UMPListener> listeners = new ArrayList<>();

    @Override
    public void onEnable() {

        rollbackManager = new PlayerRollbackManager();

        langYML = new LangYML(getDataFolder(), this);

        staffChat = new StaffChat(this);

        commands.put(new StaffChatCommand(this), "staffchat");

        listeners.add(new OnPlayerChat(this));

        registerCommands();

        registerListeners();

    }

    private void registerCommands(){
        for(CommandExecutor command : commands.keySet()){
            getCommand(commands.get(command)).setExecutor(command);
        }
    }

    private void registerListeners(){
        for(UMPListener listener : listeners){
            if(listener.isDependent()){
                String dependency = listener.getDependentPlugin();
                if(getServer().getPluginManager().getPlugin(dependency) == null){
                    getLogger().severe("Unable to find required dependency: " + dependency);
                    continue;
                }
            }
            getServer().getPluginManager().registerEvents(listener, this);
        }
    }

    @Override
    public void onDisable() {
    }

    public PlayerRollbackManager getRollbackManager() {
        return rollbackManager;
    }

    public LangYML getLangYML() {
        return langYML;
    }

    public StaffChat getStaffChat() {
        return staffChat;
    }

}
