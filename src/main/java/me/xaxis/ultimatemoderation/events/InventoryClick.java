package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.gui.GUI;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.utils.ItemUtil;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Date;

public class InventoryClick implements Listener {
    private final UMP plugin;

    public InventoryClick(UMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {

        if(!(event.getWhoClicked() instanceof Player)) return;

        Inventory i = event.getClickedInventory();

        if (i == null || i.isEmpty()) return;

        PlayerBanGUI gui = PlayerBanGUI.getGUI(event.getWhoClicked().getUniqueId());

        if(gui == null) return;

        if(!event.getWhoClicked().getUniqueId().equals(gui.getHolder().getUniqueId())) return;

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if (ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(gui.getTitle())) {

            Player player = (Player) event.getWhoClicked();

            event.setCancelled(true);

            ItemStack itemStack = event.getCurrentItem();

            switch (itemStack.getType()) {
                case RED_CONCRETE -> {
                    gui.removeGUI();
                    if(gui.getTime() == null){
                        gui.getTarget().ban(gui.getReason(), (Date) null, player.getDisplayName());
                    }else {
                        gui.getTarget().ban(gui.getReason(), new Date(gui.getTime()), player.getDisplayName());
                    }
                }
                case BARRIER -> gui.getHolder().closeInventory();
                case BOOK -> {
                    gui.getHolder().closeInventory();
                    gui.inExtraNotes = true;
                    player.sendMessage("Enter extra notes here or enter in \"null\" to continue!");
                }
                case COMPASS -> {
                    gui.getHolder().closeInventory();
                    gui.inTimeSet = true;
                    player.sendMessage("Enter time here or enter in \"null\" to continue! \nFormat: years/months/weeks/days/hours/minutes/seconds");
                }
                case PAPER -> {
                    gui.getHolder().closeInventory();
                    gui.inSetReason = true;
                    player.sendMessage("Enter reason here or enter in \"null\" to continue!");
                }
            }

        }
    }
}
