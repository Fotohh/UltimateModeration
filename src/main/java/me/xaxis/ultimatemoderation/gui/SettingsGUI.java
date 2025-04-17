package me.xaxis.ultimatemoderation.gui;

import com.github.fotohh.itemutil.ItemBuilder;
import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class SettingsGUI implements InventoryHolder {

    private final PlayerProfile profile;
    private final Inventory inventory;
    private final Player player;

    private ItemBuilder toggleStaffChat;
    private boolean staffChatToggled;

    private ItemBuilder joinVanishedItem;
    private boolean joinVanished;

    public SettingsGUI(Player player, UMP plugin) {
        profile = PlayerProfile.getPlayerProfile(player.getUniqueId());
        this.player = player;
        this.inventory = Bukkit.createInventory(this, 9*6, "Settings");
        this.staffChatToggled = plugin.getStaffChat().chatIsVisible(player);
        this.joinVanished = profile.isJoinVanishEnabled();
    }

    public void toggleStaffChat() {
        staffChatToggled = !staffChatToggled;
        UMP.instance.getStaffChat().setVisibility(player, staffChatToggled);
        updateGUI();
    }

    public void toggleJoinVanish() {
        joinVanished = !joinVanished;
        profile.setJoinVanish(joinVanished);
        updateGUI();
    }

    private void updateGUI() {
        inventory.clear();
        toggleStaffChat = new ItemBuilder(Material.CLOCK)
                .withTitle("&7Toggle Staff Chat")
                .withLore("&7Click to toggle staff chat visibility", "&7Currently: " + (staffChatToggled ? "&aEnabled" : "&cDisabled"));
        joinVanishedItem = new ItemBuilder(Material.ENDER_PEARL)
                .withTitle("&7Join Vanished")
                .withLore("&7Click to toggle join vanished", "&7Currently: " + (joinVanished ? "&aEnabled" : "&cDisabled"));
        inventory.setItem(0, toggleStaffChat.build());
        inventory.setItem(1, joinVanishedItem.build());
        inventory.setItem(2, new ItemBuilder(Material.BARRIER).withTitle("&cClose").build());
    }

    public void openGUI(){
        updateGUI();
        player.openInventory(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        if (event.getCurrentItem().getType() == Material.AIR) return;
        switch (event.getRawSlot()) {
            case 0 -> toggleStaffChat();
            case 1 -> toggleJoinVanish();
            case 2 -> player.closeInventory();
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
