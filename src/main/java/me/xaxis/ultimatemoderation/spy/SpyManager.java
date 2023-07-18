package me.xaxis.ultimatemoderation.spy;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class SpyManager extends Utils implements Listener{

    private final UMP plugin;
    private final HashMap<UUID, PlayerSpy> spyHashMap = new HashMap<>();

    public HashMap<UUID, PlayerSpy> getSpyHashMap() {
        return spyHashMap;
    }

    public SpyManager(UMP plugin) {

        super(plugin);

        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }

    public void addPlayer(Player player, Player target, PlayerRollbackManager manager){
        spyHashMap.put(player.getUniqueId(), new PlayerSpy(player, target, manager));
    }

    public boolean containsPlayer(Player player){
        return spyHashMap.containsKey(player.getUniqueId());
    }

    public void removePlayer(Player player){
        unvanishPlayer(player, false);
        spyHashMap.get(player.getUniqueId()).restore();
        spyHashMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        if(containsPlayer(player)){
            removePlayer(player);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(!spyHashMap.containsKey(player.getUniqueId()) && !containsPlayer(player)) return;

        if(event.getItem() == null || event.getItem().getType() == Material.AIR) return;

        PlayerSpy playerSpy = spyHashMap.get(player.getUniqueId());

        switch (event.getItem().getType()){
            case BARRIER -> removePlayer(player);
            case ANVIL -> {
                playerSpy.getTarget().ban("L bozo", new Date(System.currentTimeMillis() + 1000000), "nobody", true);
            }
        }

        event.setCancelled(true);

    }

    public ItemStack[] getDefaultContents() {

        return null;
    }

    //TODO add on interact to quit spying when finished n stuff using items in hot bar

}
