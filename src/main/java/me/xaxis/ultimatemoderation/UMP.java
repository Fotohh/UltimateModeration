package me.xaxis.ultimatemoderation;

import me.xaxis.ultimatemoderation.chat.StaffChat;
import me.xaxis.ultimatemoderation.commands.SpyCommand;
import me.xaxis.ultimatemoderation.commands.StaffChatCommand;
import me.xaxis.ultimatemoderation.events.InventoryClick;
import me.xaxis.ultimatemoderation.events.OnPlayerChat;
import me.xaxis.ultimatemoderation.events.OnQuit;
import me.xaxis.ultimatemoderation.events.PlayerJoin;
import me.xaxis.ultimatemoderation.file.LangYML;
import me.xaxis.ultimatemoderation.mute.MuteManager;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.spy.SpyManager;
import me.xaxis.ultimatemoderation.updatechecker.UpdateChecker;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UMP extends JavaPlugin {

    private LangYML langYML;

    private SpyManager spyManager;

    private StaffChat staffChat;

    private HashMap<CommandExecutor, String>[] commands;

    private PlayerRollbackManager rollbackManager;

    private MuteManager muteManager;

    public MuteManager getMuteManager() {
        return muteManager;
    }


    private Metrics metrics;

    private Listener[] listeners;

    @Override
    public void onEnable() {

        rollbackManager = new PlayerRollbackManager();

        langYML = new LangYML(getDataFolder(), this);

        staffChat = new StaffChat(this);

        commands = new HashMap[]{
                load(new StaffChatCommand(this), "staffchat"),
                load(new SpyCommand(this), "spy")
        };

        listeners = new Listener[]{
                new OnPlayerChat(this),
                new PlayerJoin(this),
                new OnQuit(this),
                new InventoryClick(this)
        };

        registerCommands();

        registerListeners();

        this.spyManager = new SpyManager(this);

        try {
            this.muteManager = new MuteManager(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        metrics = new Metrics(this, 19198);

        new UpdateChecker(this);

        loadPlayerData();

    }

    private HashMap<CommandExecutor, String> load(CommandExecutor c, String s){
        return new HashMap<>(Map.of(c, s));
    }

    public void loadPlayerData(){
        for(Player player : getServer().getOnlinePlayers()){
            new PlayerProfile(player, this);
        }
    }

    public SpyManager getSpyManager() {
        return spyManager;
    }

    private void registerCommands(){
        for(HashMap<CommandExecutor, String> map : commands){
            for(CommandExecutor command : map.keySet()) {
                getCommand(map.get(command)).setExecutor(command);
            }
        }
    }

    private void registerListeners(){
        for(Listener listener : listeners){
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
        metrics.shutdown();
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
