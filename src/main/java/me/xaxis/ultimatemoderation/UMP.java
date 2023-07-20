package me.xaxis.ultimatemoderation;

import me.xaxis.ultimatemoderation.chat.StaffChat;
import me.xaxis.ultimatemoderation.commands.StaffChatCommand;
import me.xaxis.ultimatemoderation.events.OnPlayerChat;
import me.xaxis.ultimatemoderation.events.UMPListener;
import me.xaxis.ultimatemoderation.file.LangYML;
import me.xaxis.ultimatemoderation.mute.MuteManager;
import me.xaxis.ultimatemoderation.spy.SpyManager;
import me.xaxis.ultimatemoderation.updatechecker.UpdateChecker;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class UMP extends JavaPlugin {

    private LangYML langYML;

    private SpyManager spyManager;

    private StaffChat staffChat;

    private final HashMap<CommandExecutor, String> commands = new HashMap<>();

    private PlayerRollbackManager rollbackManager;

    private MuteManager muteManager;

    public MuteManager getMuteManager() {
        return muteManager;
    }

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

        this.spyManager = new SpyManager(this);

        try {
            this.muteManager = new MuteManager(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        new UpdateChecker(this);

    }

    public SpyManager getSpyManager() {
        return spyManager;
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
        try {
            muteManager.save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
