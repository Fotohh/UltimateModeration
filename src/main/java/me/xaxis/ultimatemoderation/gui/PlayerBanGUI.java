package me.xaxis.ultimatemoderation.gui;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Date;

public class PlayerBanGUI extends GUI {

    private final UMP plugin;
    private final Player target;

    private GUI reasonGUI;

    private GUI timeGUI;

    public PlayerBanGUI(Player player, Player target, UMP plugin) {
        super("Ban Menu", 9, player);
        this.plugin = plugin;
        this.target = target;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
        addItems();
        player.openInventory(getInventory());
    }

    private void addItems(){

    }

    @EventHandler
    public void playerClick(InventoryClickEvent event) {

        Inventory i = event.getClickedInventory();

        if (i == null || i.isEmpty() || !event.getWhoClicked().getUniqueId().equals(getHolder().getUniqueId())) return;

        if (event.getView().getTitle().equalsIgnoreCase(getTitle())) {
            event.setCancelled(true);
            //4 player head

            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            switch (itemStack.getType()) {
                case RED_CONCRETE -> {
                    target.ban(reason, new Date(time), "null");
                    unregister();
                }
                case BARRIER -> {
                    getHolder().closeInventory();
                    unregister();
                }
                case BOOK -> {
                    //extra notes
                }
                case COMPASS -> {
                    timeGUI = new GUI("Time", InventoryType.ANVIL, getHolder());
                    timeGUI.addItem(new ItemUtil(Material.PAPER).withTitle("Format: y/m/w/h/m/s").withLore("years,months,weeks","hours,minutes,seconds").build(), 0);
                    getHolder().openInventory(timeGUI.getInventory());
                }
                case PAPER -> {
                    reasonGUI = new GUI("Reason", InventoryType.ANVIL, getHolder());
                    reasonGUI.addItem(new ItemUtil(Material.PAPER).withTitle("No reason").build(),0);
                    getHolder().openInventory(reasonGUI.getInventory());
                }
            }

        } else if (reasonGUI != null && event.getView().getTitle().equalsIgnoreCase(reasonGUI.getTitle())) {

            event.setCancelled(true);

            AnvilInventory inv = (AnvilInventory) reasonGUI.getInventory();

            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            //2 is result

            if (event.getSlot() == 2 && itemStack.getType() == Material.PAPER || inv.getRenameText() == null) {
                reason = inv.getRenameText();
                getHolder().openInventory(getInventory());
            }
        } else if(timeGUI != null && event.getView().getTitle().equalsIgnoreCase(timeGUI.getTitle())){
            event.setCancelled(true);

            AnvilInventory inv = (AnvilInventory) timeGUI.getInventory();

            ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null || itemStack.getType() == Material.AIR || inv.getRenameText() == null) return;

            //2 is result

            if (event.getSlot() == 2 && itemStack.getType() == Material.PAPER) {
                long[] values = Arrays.stream(inv.getRenameText().split("/")).mapToLong(Long::parseLong).toArray();
                //0 y 1 m 2 w 3 d 4 h 5 m 6 s
                if(values.length != 7) return;

                time = fromYears(values[0]) + fromMonths(values[1]) + fromWeeks(values[2]) + fromDays(values[3]) +fromHours(values[4]) +fromMinutes(values[5]) + fromSeconds(values[6]);

                getHolder().openInventory(getInventory());
            }
        }
    }

    private Long time = null;
    private String reason = "No reason";
    private String extraNotes = "No extra notes";

    private long fromSeconds(long seconds){
        return seconds * 1000;
    }
    private long fromMinutes(long minutes){
        return minutes * 60000L;
    }
    private long fromHours(long hours){
        return hours * 3600000L;
    }
    private long fromDays(long days){
        return days * 86400000L;
    }
    private long fromWeeks(long weeks){
        return weeks * 604800000L;
    }
    private long fromMonths(long months){
        return months * 2629746000L;
    }
    private long fromYears(long years){
        return years * 31556926000L;
    }

}
