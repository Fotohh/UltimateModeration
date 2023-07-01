package me.xaxis.ultimatemoderation;

import me.xaxis.ultimatemoderation.chat.StaffChat;
import me.xaxis.ultimatemoderation.commands.StaffChatCommand;
import me.xaxis.ultimatemoderation.events.OnPlayerChat;
import me.xaxis.ultimatemoderation.events.OnPlayerReport;
import me.xaxis.ultimatemoderation.events.UMPListener;
import me.xaxis.ultimatemoderation.file.LangYML;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class UMP extends JavaPlugin {

    /*
    ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
    PacketContainer packetContainer = protocolManager.createPacket(PacketType.Play.Server.KICK_DISCONNECT);
    protocolManager.sendServerPacket(player, packetContainer);
    */

    public static boolean reportplusEnabled = true;

    private LangYML langYML;

    private StaffChat staffChat;

    private final HashMap<CommandExecutor, String> commands = new HashMap<>();

    private PlayerRollbackManager rollbackManager;

    private final ArrayList<UMPListener> listeners = new ArrayList<>();

    @Override
    public void onEnable() {

        if(getServer().getPluginManager().getPlugin("ReportPlus") == null){
            getLogger().warning("Didn't find ReportPlus. Add ReportPlus so that UltimateModeration+ can hook into it.");
            UMP.reportplusEnabled = false;
        }

        rollbackManager = new PlayerRollbackManager();

        langYML = new LangYML(getDataFolder(), this);

        staffChat = new StaffChat(this);

        commands.put(new StaffChatCommand(this), "staffchat");
        listeners.add(new OnPlayerChat(this));
        listeners.add(new OnPlayerReport(this));

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
            if(listener.isDependent() && !reportplusEnabled) continue;
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
