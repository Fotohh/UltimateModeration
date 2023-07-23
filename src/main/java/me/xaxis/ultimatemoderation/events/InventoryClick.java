package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.gui.GUI;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.utils.ItemUtil;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

public class InventoryClick implements Listener {
    private final UMP plugin;

    public InventoryClick(UMP plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {

        Inventory i = event.getClickedInventory();

        if (i == null || i.isEmpty()) return;

        PlayerBanGUI gui = PlayerBanGUI.getGUI(UUID.fromString(i.getItem(5).getItemMeta().getLore().get(0)));

        if(!event.getWhoClicked().getUniqueId().equals(gui.getHolder().getUniqueId())) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if (event.getView().getTitle().equalsIgnoreCase(gui.getTitle())) {
            event.setCancelled(true);
            //5 player head

            ItemStack itemStack = event.getCurrentItem();


            switch (itemStack.getType()) {
                case RED_CONCRETE -> {
                    gui.getTarget().ban(gui.getReason(), new Date(gui.getTime()), "null");
                }
                case BARRIER -> {

                    gui.getHolder().closeInventory();
                }
                case BOOK -> {
                    gui.setBookGUI(new GUI("Extra Notes", InventoryType.ANVIL, gui.getHolder()));
                    gui.getBookGUI().addItem(new ItemUtil(Material.PAPER).withTitle("Extra Notes").build());
                    gui.getHolder().openInventory(gui.getBookGUI().getInventory());
                }
                case COMPASS -> {
                    gui.setTimeGUI( new GUI("Time", InventoryType.ANVIL, gui.getHolder()));
                    gui.getTimeGUI().addItem(new ItemUtil(Material.PAPER).withTitle("Format: y/m/w/h/m/s").withLore("years,months,weeks", "hours,minutes,seconds").build(), 0);
                    gui.getHolder().openInventory(gui.getTimeGUI().getInventory());
                }
                case PAPER -> {

                    gui.setReasonGUI( new GUI("Reason", InventoryType.ANVIL, gui.getHolder()));
                    gui.getReasonGUI().addItem(new ItemUtil(Material.PAPER).withTitle("No reason").build(), 0);
                    gui.getHolder().openInventory(gui.getReasonGUI().getInventory());
                }
            }

        } else if (gui.getReasonGUI() != null && event.getView().getTitle().equalsIgnoreCase(gui.getReasonGUI().getTitle())) {

            event.setCancelled(true);


            AnvilInventory inv = (AnvilInventory) gui.getReasonGUI().getInventory();

            ItemStack itemStack = event.getCurrentItem();
            if (itemStack == null || itemStack.getType() == Material.AIR) return;

            //2 is result


            if (event.getSlot() == 2 && itemStack.getType() == Material.PAPER || inv.getRenameText() == null) {
                gui.setReason( inv.getRenameText());
                gui.getHolder().openInventory(gui.getInventory());
            }
        } else if (gui.getTimeGUI() != null && event.getView().getTitle().equalsIgnoreCase(gui.getTimeGUI().getTitle())) {
            event.setCancelled(true);

            AnvilInventory inv = (AnvilInventory) gui.getTimeGUI().getInventory();


            ItemStack itemStack = event.getCurrentItem();

            if (itemStack == null || itemStack.getType() == Material.AIR || inv.getRenameText() == null) return;

            //2 is result

            if (event.getSlot() == 2 && itemStack.getType() == Material.PAPER) {

                long[] values = Arrays.stream(inv.getRenameText().split("/")).mapToLong(Long::parseLong).toArray();
                //0 y 1 m 2 w 3 d 4 h 5 m 6 s
                if (values.length != 7) return;


                gui.setTime( Utils.fromYears(values[0]) + Utils.fromMonths(values[1]) + Utils.fromWeeks(values[2]) + Utils.fromDays(values[3]) + Utils.fromHours(values[4]) + Utils.fromMinutes(values[5]) + Utils.fromSeconds(values[6]));

                gui.getHolder().openInventory(gui.getInventory());
            }

        } else if (gui.getBookGUI() != null && event.getView().getTitle().equalsIgnoreCase(gui.getBookGUI().getTitle())) {
            event.setCancelled(true);


            AnvilInventory inv = (AnvilInventory) gui.getBookGUI().getInventory();
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR || inv.getRenameText() == null) return;
            if (event.getSlot() != 2 || item.getType() != Material.PAPER) return;
            gui.setExtraNotes( inv.getRenameText());
            gui.getHolder().openInventory(gui.getInventory());
        }
    }
}
