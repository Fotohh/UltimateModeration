package me.xaxis.ultimatemoderation.gui;

import com.github.fotohh.itemutil.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SearchBarGUI implements InventoryHolder {

    private final Player player;
    private final static Map<UUID, ArrayList<String>> searchHistory = new HashMap<>();
    private final StringBuilder search = new StringBuilder();
    private boolean inHistory = false;
    private final Inventory inventory;

    private final static String[] ALPHABET = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z", " "
    };
    private final static String[] NUMBERS = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    public SearchBarGUI(Player player) {
        this.player = player;
        inventory = Bukkit.getServer().createInventory(this, 9 * 6, "Search Bar");
        setSearchContents();
    }

    private void setSearchHistoryContents() {
        inventory.clear();
        inventory.setItem(9*6-1, new ItemBuilder(Material.ARROW).withTitle("Back").withLore(" ").build());
        List<String> sh = searchHistory.get(player.getUniqueId());
        if(sh.isEmpty()) {
            inventory.setItem(0, new ItemBuilder(Material.PAPER).withTitle("No search history").withLore(" ").build());
            return;
        }
        if(sh.size() - 1 > 9*6-2) {
            sh = sh.subList(0, 9*6-1);
        }
        for(String s : sh) {
            inventory.addItem(new ItemBuilder(Material.BOOK).withTitle(s).withLore("Left click to select", "Right click to delete").build());
        }
    }

    private void setSearchContents() {
        inventory.clear();
        for(String s : ALPHABET) {
            inventory.addItem(new ItemBuilder(Material.PAPER).withTitle(s).withLore(" ").build());
        }
        for(String s : NUMBERS) {
            inventory.addItem(new ItemBuilder(Material.PAPER).withTitle(s).withLore(" ").build());
        }
        inventory.addItem(new ItemBuilder(Material.ANVIL).withTitle("Backspace").withLore(" ").build());
        inventory.addItem(new ItemBuilder(Material.BARRIER).withTitle("Clear").withLore(" ").build());
        inventory.addItem(new ItemBuilder(Material.STRUCTURE_VOID).withTitle("Search").withLore(" ").build());
        inventory.addItem(new ItemBuilder(Material.BOOK).withTitle("Search History").withLore(" ").build());
        inventory.setItem(53, new ItemBuilder(Material.ARROW).withTitle("Back").withLore(" ").build());

        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).withLore(" ").withTitle(" ").build());
            }
        }
    }

    public void handleClick(InventoryClickEvent event) {
        if(inHistory) handleHistory(event);
        else handleSearch(event);
    }

    //TODO search isnt filtering numbers???...

    private void handleHistory(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if(event.getCurrentItem().getType() == Material.BOOK) {
            ArrayList<String> history = searchHistory.get(event.getWhoClicked().getUniqueId());
            if(event.isLeftClick()) {
                search.delete(0, search.length());
                search.append(ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()));
                event.getView().setTitle("Search: " + search);
                setSearchContents();
                inHistory = false;
            } else if(event.isRightClick()) {
                history.remove(event.getCurrentItem().getItemMeta().getDisplayName());
                inventory.setItem(event.getSlot(), new ItemStack(Material.AIR));
            }
        }else if(event.getCurrentItem().getType() == Material.ARROW) {
            inHistory = false;
            event.getView().setTitle("Search Bar");
            setSearchContents();
        }
    }

    private void handleSearch(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        switch (event.getCurrentItem().getType()) {
            case PAPER -> {
                search.append(event.getCurrentItem().getItemMeta().getDisplayName());
                event.getView().setTitle("Search: " + search);
            }
            case ANVIL -> {
                if(search.isEmpty()) return;
                search.deleteCharAt(search.length() - 1);
                event.getView().setTitle("Search: " + search);
            }
            case ARROW -> new PlayerListGUI(player).openGUI();
            case BARRIER -> {
                search.delete(0, search.length());
                event.getView().setTitle("Search: " + search);
            }
            case STRUCTURE_VOID -> {
                String searchString = search.toString();
                if(searchString.isBlank()) {
                    new PlayerListGUI((Player)event.getWhoClicked()).openGUI();
                    return;
                }
                if(!searchHistory.get(player.getUniqueId()).contains(searchString)) {
                    searchHistory.get(player.getUniqueId()).add(searchString);
                }
                new PlayerListGUI((Player)event.getWhoClicked(), searchString).openGUI();
            }
            case BOOK -> {
                inHistory = true;
                setSearchHistoryContents();
            }
        }
    }

    public void open() {
        searchHistory.putIfAbsent(player.getUniqueId(), new ArrayList<>());
        player.openInventory(inventory);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
