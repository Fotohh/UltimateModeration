package me.xaxis.ultimatemoderation.gui;

import com.github.fotohh.itemutil.ItemBuilder;
import me.xaxis.ultimatemoderation.UMP;
//import org.apache.commons.lang3.tuple.Triple;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.*;

public class SearchBarGUI implements Listener {

    private final UMP plugin;
    private final static Map<UUID, Triple<StringBuilder, Inventory, ArrayList<String>>> searchBarMap = new HashMap<>();
    private final static String[] ALPHABET = {
            "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R",
            "S", "T", "U", "V", "W", "X", "Y", "Z"
    };
    private final static String[] NUMBERS = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
    };

    public SearchBarGUI(UMP plugin) {
        this.plugin = plugin;
    }

    private Inventory createSearchHistory(ArrayList<String> history) {
        Inventory inventory = plugin.getServer().createInventory(null, 9 * 6, "Search History");

        for(String s : history) {
            inventory.addItem(new ItemBuilder(Material.BOOK).withTitle(s).withLore("Left click to select", "Right click to delete").build());
        }
        return inventory;
    }

    private Inventory createContents(Inventory inventory) {
        inventory.clear();
        for(String s : ALPHABET) {
            inventory.addItem(new ItemBuilder(Material.PAPER).withTitle(s).withLore(" ").build());
        }
        for(String s : NUMBERS) {
            inventory.addItem(new ItemBuilder(Material.PAPER).withTitle(s).withLore(" ").build());
        }
        inventory.addItem(new ItemBuilder(Material.BARRIER).withTitle("Clear").withLore(" ").build());
        inventory.addItem(new ItemBuilder(Material.STRUCTURE_VOID).withTitle("Search").withLore(" ").build());
        inventory.addItem(new ItemBuilder(Material.BOOK).withTitle("Search History").withLore(" ").build());

        for(int i = 0; i < inventory.getSize(); i++) {
            if(inventory.getItem(i) == null || inventory.getItem(i).getType() == Material.AIR) {
                inventory.setItem(i, new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE).withLore(" ").withTitle(" ").build());
            }
        }
        return inventory;
    }

    private void handleHistory(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if(event.getCurrentItem().getType() == Material.BOOK) {
            Triple<StringBuilder, Inventory, ArrayList<String>> triple = searchBarMap.get(event.getWhoClicked().getUniqueId());
            ArrayList<String> history = triple.getRight();
            StringBuilder search = triple.getLeft();
            if(event.isLeftClick()) {
                search.delete(0, search.length() - 1);
                search.append(event.getCurrentItem().getItemMeta().getDisplayName());
                event.getView().setTitle("CLOSED");
                event.getWhoClicked().openInventory(createContents(plugin.getServer().createInventory(null, 9 * 6, "Search Bar")));
            } else if(event.isRightClick()) {
                history.remove(event.getCurrentItem().getItemMeta().getDisplayName());
                event.getWhoClicked().openInventory(createSearchHistory(history));
            }
        }
    }

    private void onInvClose(InventoryCloseEvent event) {
        if(!searchBarMap.containsKey(event.getPlayer().getUniqueId())) return;
        if(event.getView().getTitle().equals("Search History")) searchBarMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {

        if(event.getClickedInventory() == null) return;
        if(!searchBarMap.containsKey(event.getWhoClicked().getUniqueId())) return;

        Triple<StringBuilder, Inventory, ArrayList<String>> triple = searchBarMap.get(event.getWhoClicked().getUniqueId());
        Inventory inventory = triple.getMiddle();
        if(!triple.getMiddle().equals(event.getClickedInventory())) return;
        event.setCancelled(true);
        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        if(event.getView().getTitle().equals("Search History")) {
            handleHistory(event);
            return;
        }

        StringBuilder search = triple.getLeft();

        switch (event.getCurrentItem().getType()) {
            case PAPER -> {
                search.append(event.getCurrentItem().getItemMeta().getDisplayName());
                event.getView().setTitle("Search: " + search);
            }
            case BARRIER -> {
                search.delete(0, search.length() - 1);
                event.getView().setTitle("Search: " + search);
            }
            case STRUCTURE_VOID -> {
                new PlayerListGUI((Player)event.getWhoClicked());
                searchBarMap.remove(event.getWhoClicked().getUniqueId());
            }
            case BOOK -> event.getWhoClicked().openInventory(createSearchHistory(triple.getRight()));
        }

    }

    public void open(Player player) {
        searchBarMap.put(player.getUniqueId(),
                Triple.of(new StringBuilder(), createContents(plugin.getServer().createInventory(null, 9 * 6, "Search Bar")), new ArrayList<>()));
    }

    public Inventory getGUI(Player player) {
        return searchBarMap.get(player.getUniqueId()).getMiddle();
    }

}
