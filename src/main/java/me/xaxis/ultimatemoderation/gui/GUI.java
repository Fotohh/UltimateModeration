package me.xaxis.ultimatemoderation.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUI implements Listener {


    private final Inventory inventory;
    private final String title;
    private int size;
    private InventoryType type;
    private final Player holder;

    public Inventory getInventory() {
        return inventory;
    }

    public void addItem(ItemStack item){
        inventory.addItem(item);
    }

    public void addItem(ItemStack item, int slot){
        inventory.setItem(slot,item);
    }

    public String getTitle() {
        return ChatColor.stripColor(title);
    }

    public int getSize() {
        return size;
    }

    public InventoryType getType() {
        return type;
    }

    public Player getHolder() {
        return holder;
    }

    public GUI(String s, int i, Player p){
        title = s;
        size = i;
        holder = p;
        inventory = Bukkit.createInventory(p,i,s);
    }
    public GUI(String s, InventoryType t, Player p){
        title = s;
        type = t;
        holder = p;
        inventory = Bukkit.createInventory(p,t,s);
    }

}
