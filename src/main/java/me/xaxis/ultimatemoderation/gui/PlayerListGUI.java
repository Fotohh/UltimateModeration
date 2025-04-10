package me.xaxis.ultimatemoderation.gui;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class PlayerListGUI implements InventoryHolder {

    private final Inventory inventory;

    public PlayerListGUI() {
        inventory = Bukkit.createInventory(this, 9, "Player List");
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
