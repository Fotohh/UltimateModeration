package me.xaxis.ultimatemoderation.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import com.github.fotohh.itemutil.ItemBuilder;
import com.google.common.base.Strings;

import net.md_5.bungee.api.ChatColor;

public class PlayerListGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;
    private int maxSize = 9*6 - 1;
    private int pageSize = 9*5 - 1;
    private int page = 0;

    public boolean containsSameCharacters(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return false;
        }

        char[] arr1 = str1.toLowerCase().toCharArray();
        char[] arr2 = str2.toLowerCase().toCharArray();

        Arrays.sort(arr1);
        Arrays.sort(arr2);

        int i = 0, j = 0;
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] == arr2[j]) {
                i++;
                j++;
            } else if (arr1[i] < arr2[j]) {
                i++;
            } else {
                return false; 
            }
        }

        return j == arr2.length;
    }

    public void update() {
        int lowerBound = pageSize * page;
        int higherBound = pageSize * (page + 1);
        List<ItemBuilder> list = filteredList.orElse(unfilteredList);
        if(list.size() <= lowerBound) return;
        for(int i = lowerBound; i < higherBound; i++) {
            if(list.size() < i) break;
            clearPage();
            inventory.setItem(i, list.get(i));
        }
    }

    public boolean canGoToNextPage() {
        int higherBound = pageSize * (page + 1);
        if(filteredList.orElse(unfilteredList).size() <= higherBound) return false; else return true;
    }

    private void clearPage() {
        for(int i = 0; i < pageSize; i++) {
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int prevPageSlot() {
        return maxSize - 8;
    }

    public int nextPageSlot() {
        return maxSize;
    }

    public int getPage() {
        return page;
    }

    private final Optional<List<ItemBuilder>> filteredList;

    public Optional<List<ItemBuilder>> getFilteredList() {
        return filteredList;
    }

    public List<ItemBuilder> getUnfilteredList() {
        return unfilteredList;
    }

    public PlayerListGUI(Player player) {
        this.player = player;
        this.searchQuery = Optional.empty();
        this.unfilteredList = Bukkit.getOnlinePlayers().stream().map(p -> {
            return new ItemBuilder(Material.PLAYER_HEAD)
            .withTitle(p.getName())
            .withLore("Click to view details", p.getUniqueId().toString()).build();
        }).toList();
        this.filteredList = Optional.empty();
        inventory = Bukkit.createInventory(this, 9, "Player List");
        openGUI();
    }

    private final Optional<String> searchQuery;

    private final List<ItemBuilder> unfilteredList;

    public PlayerListGUI(Player player, String searchQuery) {
        this.player = player;
        this.searchQuery = Optional.of(searchQuery);
        this.unfilteredList = Bukkit.getOnlinePlayers().stream().map(p -> {
            return new ItemBuilder(Material.PLAYER_HEAD)
            .withTitle(p.getName())
            .withLore("Click to view details", p.getUniqueId().toString()).build();
        }).toList();
        this.filteredList = Optional.of(unfilteredList.stream().filter(item -> {
            return containsSameCharacters(ChatColor.stripColor(item.getItemMeta().getDisplayName()), searchQuery);
        }).toList());
        inventory = Bukkit.createInventory(this, maxSize, "Player List | Search: " + searchQuery);
        openGUI();
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
