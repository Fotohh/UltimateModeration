package me.xaxis.ultimatemoderation.gui;

import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PlayerBanGUI implements Listener {

    private final Player player;

    private final Inventory inventory;

    private final UMP plugin;

    public PlayerBanGUI(Player player, UMP plugin) {
        this.player = player;
        this.inventory = Bukkit.createInventory(player, 6*9, "nice");
        this.plugin = plugin;
    }

    @EventHandler
    public void invClick(InventoryClickEvent event){

    }

    public void unregister(){
        HandlerList.unregisterAll(this);
    }
}
