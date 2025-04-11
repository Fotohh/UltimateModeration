package me.xaxis.ultimatemoderation.events;

import com.github.fotohh.itemutil.ItemBuilder;
import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.gui.PlayerListGUI;
import me.xaxis.ultimatemoderation.gui.ProfileGUI;
import me.xaxis.ultimatemoderation.gui.SearchBarGUI;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.Ban;
import me.xaxis.ultimatemoderation.type.InfractionType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InventoryClick implements Listener {
    private final UMP plugin;

    public InventoryClick(UMP plugin) {
        this.plugin = plugin;
    }

    private void handlePlayerListGUI(InventoryClickEvent event, PlayerListGUI gui) {
        Player player = (Player) event.getWhoClicked();
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        switch (event.getCurrentItem().getType()) {
                case PLAYER_HEAD -> {
                    //lore 0 - Click to view profile lore 1 - uuid
                    UUID uuid;
                    try {
                        uuid = UUID.fromString(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(1)));
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("Invalid player entry found! Removing entry...");
                        gui.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
                        return;
                    }
                    OfflinePlayer target = Bukkit.getOfflinePlayer(uuid);

                    PlayerProfile profile = PlayerProfile.getPlayerProfile(target.getUniqueId());
                    if(profile == null) {
                        player.sendMessage("Invalid player entry found! Removing entry...");
                        gui.getInventory().setItem(event.getSlot(), new ItemStack(Material.AIR));
                        return;
                    }

                    //TODO open profile gui

                }

                case ARROW -> {
                    if(event.getRawSlot() == gui.prevPageSlot()) {
                        if(gui.getPage() == 0) break;
                        gui.setPage(gui.getPage() - 1);
                        gui.update();
                        gui.getInventory().setItem(50, new ItemBuilder(Material.PAPER).withTitle("Page: " + gui.getPage()).withLore(" ").build());
                    }else if(event.getRawSlot() == gui.nextPageSlot()) {
                        if(!gui.canGoToNextPage()) break;
                        gui.setPage(gui.getPage() + 1);
                        gui.update();
                        gui.getInventory().setItem(50, new ItemBuilder(Material.PAPER).withTitle("Page: " + gui.getPage()).withLore(" ").build());
                    }
                }
                case SPYGLASS -> new SearchBarGUI(player).open();
                case BARRIER -> new PlayerListGUI(player).openGUI();
        }
    }

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        if(event.getInventory().getHolder() instanceof PlayerListGUI gui) {
            event.setCancelled(true);
            handlePlayerListGUI(event, gui);
            return;
        }

        if(event.getInventory().getHolder() instanceof SearchBarGUI searchBar) {
            event.setCancelled(true);
            handleSearchClick(event, searchBar);
            return;
        }

        if(event.getInventory().getHolder() instanceof ProfileGUI gui) {
            event.setCancelled(true);
            gui.handleClick(event);
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

    private void handleSearchClick(InventoryClickEvent event, SearchBarGUI searchBar) {
        searchBar.handleClick(event);
    }
}
