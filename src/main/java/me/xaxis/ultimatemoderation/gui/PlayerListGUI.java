package me.xaxis.ultimatemoderation.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import com.github.fotohh.itemutil.ItemBuilder;

public class PlayerListGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;
    private final int maxSize = 9*6;
    private final int pageSize = 9*5;
    private int page = 0;

    public void update() {
        int lowerBound = pageSize * page;
        int higherBound = pageSize * (page + 1);
        if(items.size() <= lowerBound) return;
        clearPage();
        int counter = 0;
        for(int i = lowerBound; i < higherBound - 1; i++) {
            if(items.size() < i) break;
            inventory.setItem(counter, items.get(i));
            counter++;
        }
    }

    public boolean canGoToNextPage() {
        int higherBound = pageSize * (page + 1);
        return items.size() > higherBound;
    }

    private void clearPage() {
        for(int i = 0; i < pageSize - 1; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int prevPageSlot() {
        return 45;
    }

    public int nextPageSlot() {
        return 53;
    }

    public int getPage() {
        return page;
    }

    public ArrayList<ItemBuilder> getItems() {
        return items;
    }

    public PlayerListGUI(Player player) {
        this.player = player;
        this.items = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(p ->
                new ItemBuilder(Material.PLAYER_HEAD)
                .withTitle(p.getName())
                .withLore("Click to view details", p.getUniqueId().toString()).build()).toList());
        for(int i = 0; i < 100; i++) {
            items.add(new ItemBuilder(Material.PLAYER_HEAD).withTitle("Player " + i).withLore("Click to view details", "fake-uuid").build());
        }
        inventory = Bukkit.createInventory(this, maxSize, "Player List");
        setItems();
    }

    private final ArrayList<ItemBuilder> items;

    public boolean partiallyMatches(String searchQuery, String other) {

        searchQuery = searchQuery.replaceAll(" ", "");
        other = other.replaceAll(" ", "");

        if(searchQuery.length() > other.length()) {
            searchQuery = searchQuery.substring(0, other.length());
        }else if(other.length() > searchQuery.length()) {
            other = other.substring(0, searchQuery.length());
        }

        for(int i = 0; i < other.length(); i++) {
            if(other.toLowerCase().charAt(i) != searchQuery.toLowerCase().charAt(i)) return false;
        }
        return true;

    }

    public PlayerListGUI(Player player, String searchQuery) {
        this.player = player;
        this.items = new ArrayList<>(Bukkit.getOnlinePlayers().stream()
                .filter(p -> partiallyMatches(p.getName(), searchQuery))
                .map(p -> new ItemBuilder(Material.PLAYER_HEAD)
                        .withTitle(p.getName())
                        .withLore("Click to view details", p.getUniqueId().toString())
                        .build())
                .toList());
        for(int i = 0; i < 100; i++) {
            items.add(new ItemBuilder(Material.PLAYER_HEAD).withTitle("Player " + i).withLore("Click to view details", "fake-uuid").build());
        }
        inventory = Bukkit.createInventory(this, maxSize, "Player List | Search: " + searchQuery);
        setItems();
    }

    private void setItems() {
        inventory.setItem(45, new ItemBuilder(Material.ARROW).withTitle("Previous Page").withLore(" ").build());
        inventory.setItem(53, new ItemBuilder(Material.ARROW).withTitle("Next Page").withLore(" ").build());
        inventory.setItem(49, new ItemBuilder(Material.SPYGLASS).withTitle("Search").withLore(" ").build());
        inventory.setItem(48, new ItemBuilder(Material.BARRIER).withTitle("Clear Search").withLore(" ").build());
        inventory.setItem(50, new ItemBuilder(Material.PAPER).withTitle("Page: " + (page + 1)).withLore(" ").build());
    }


    public void openGUI() {
        update();
        player.openInventory(inventory);
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

}
