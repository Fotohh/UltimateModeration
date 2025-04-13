package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.gui.*;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.Ban;
import me.xaxis.ultimatemoderation.type.InfractionType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {
    private final UMP plugin;

    public InventoryClick(UMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if(event.getInventory().getHolder() instanceof PlayerListGUI gui) {
            event.setCancelled(true);
            gui.handleClick(event);
            return;
        }

        if(event.getInventory().getHolder() instanceof SearchBarGUI searchBar) {
            event.setCancelled(true);
            searchBar.handleClick(event);
            return;
        }

        if(event.getInventory().getHolder() instanceof ProfileGUI gui) {
            event.setCancelled(true);
            gui.handleClick(event);
            return;
        }

        if(event.getInventory().getHolder() instanceof SettingsGUI settings) {
            event.setCancelled(true);
            settings.handleClick(event);
            return;
        }
        if(!(event.getWhoClicked() instanceof Player player)) return;
        Inventory i = event.getClickedInventory();
        if (i == null || i.isEmpty()) return;
        PlayerBanGUI gui = PlayerBanGUI.getGUI(player.getUniqueId());
        if(gui == null) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if (ChatColor.stripColor(event.getView().getTitle()).equalsIgnoreCase(gui.getTitle())) {
            event.setCancelled(true);
            ItemStack itemStack = event.getCurrentItem();
            switch (itemStack.getType()) {
                case RED_CONCRETE -> {
                    gui.removeGUI();
                    PlayerProfile prof = PlayerProfile.getPlayerProfile(gui.getTarget().getUniqueId());
                    new Ban(gui.getReason(), player.getUniqueId(), gui.getTarget().getUniqueId(),
                            gui.getTime(), gui.isIpBan());
                    InfractionType type;
                    if(gui.getTime() == -1) type = InfractionType.PERM_BAN; else type = InfractionType.TEMP_BAN;
                    if(gui.isIpBan()) type = InfractionType.IP_BAN;
                    prof.addInfraction(type); //TODO change how infractions are handled, include notes, time of ban, ect.
                }
                case BARRIER -> player.closeInventory();
                case BOOK -> {
                    player.closeInventory();
                    gui.inExtraNotes = true;
                    player.sendMessage("Enter extra notes here or enter in \"null\" to continue!");
                }
                case COMPASS -> {
                    player.closeInventory();
                    gui.inTimeSet = true;
                    player.sendMessage("Enter time here or enter in \"null\" to continue! \nFormat y/m/w/d/h/m/s");
                }
                case PAPER -> {
                    player.closeInventory();
                    gui.inSetReason = true;
                    player.sendMessage("Enter reason here or enter in \"null\" to continue!");
                }
                case BLACK_CONCRETE -> {
                    gui.setIpBan(!gui.isIpBan());
                    gui.getInventory().setItem(event.getSlot(), gui.getIpBanDisabled());
                }
                case GREEN_CONCRETE -> {
                    gui.setIpBan(!gui.isIpBan());
                    gui.getInventory().setItem(event.getSlot(), gui.getIpBanEnabled());
                }
            }
        }
    }
}
