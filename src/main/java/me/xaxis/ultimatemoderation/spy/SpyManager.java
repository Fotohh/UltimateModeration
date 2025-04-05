package me.xaxis.ultimatemoderation.spy;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.utils.ItemUtil;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

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
        if(containsPlayer(event.getPlayer())){
            removePlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();

        if(!spyHashMap.containsKey(player.getUniqueId())) return;

        if(event.getItem() == null || event.getItem().getType() == Material.AIR) return;

        PlayerSpy playerSpy = spyHashMap.get(player.getUniqueId());

        switch (event.getItem().getType()){
            case BARRIER -> removePlayer(player);
            case ANVIL -> new PlayerBanGUI(player, playerSpy.getTarget());
        }

        event.setCancelled(true);

    }

    public ItemStack[] getDefaultContents() {

        ItemUtil barrier = new ItemUtil(Material.BARRIER);
        barrier.withTitle("&cExit")
                .withLore("&7Left or right","&7click to exit!")
                .build();
        ItemUtil anvil = new ItemUtil(Material.ANVIL);
        anvil.withTitle("&4Ban Player")
                .withLore("&7Left or right","&7click to ban","the player!")
                .build();

        return new ItemStack[]{barrier, anvil};
    }

}
