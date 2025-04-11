package me.xaxis.ultimatemoderation.gui;

import com.github.fotohh.itemutil.ItemBuilder;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.InfractionType;
import me.xaxis.ultimatemoderation.utils.Tuple;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ProfileGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;
    private final OfflinePlayer target;
    private static final HashMap<UUID, UUID> notePrompts = new HashMap<>();

    public static HashMap<UUID, UUID> getNotePrompts() {
        return notePrompts;
    }

    public OfflinePlayer getTarget() {
        return target;
    }

    public Player getPlayer() {
        return player;
    }

    public ProfileGUI(Player player, UUID target) {
        this.player = player;
        this.target = Bukkit.getOfflinePlayer(target);
        this.inventory = Bukkit.createInventory(this, 9*6, this.target.getName() + "'s Profile");

        setItems();
    }

    public void open() {
        player.openInventory(inventory);
    }

    private void setItems() {

        PlayerProfile profile = PlayerProfile.getPlayerProfile(target.getUniqueId());

        inventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .withTitle("&a" + target.getName())
                .withLore("&7Status: " + (target.isOnline() ? "&aOnline" : "&cOffline"),
                        "&7Banned: " + (profile.isBanned() ? "&aYes" : "&cNo"),
                        "&7Muted: " + (profile.isMuted() ? "&aYes" : "&cNo"),
                        "&7Last Seen: " + (profile.getLastJoinDate() == -1 ? "&cNever" : "&a" + Utils.formatDate(Math.abs(profile.getLastJoinDate()))) + " ago",
                        "&7Number of Warns: " + profile.getInfractionAmount(InfractionType.WARNING),
                        "&7Number of Kicks: " + profile.getInfractionAmount(InfractionType.KICK),
                        "&7Number of Mutes: " + profile.getInfractionAmount(InfractionType.MUTE),
                        "&7Number of Bans: " + profile.getInfractionAmount(InfractionType.BAN),
                        "&6Left click to view additional notes", "&6Right click to add additional notes")
                .build());
    }

    public void handleClick(InventoryClickEvent event) {

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;
        if(event.getCurrentItem().getType() == Material.PLAYER_HEAD) {
            if(event.isLeftClick()) {
                openNotes();
            }else if(event.isRightClick()) {
                notePrompts.put(player.getUniqueId(), target.getUniqueId());
                player.sendMessage(Utils.chat("&aType your note in chat:"));
                player.closeInventory();
            }
        }

    }

    private void openNotes() {
        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("Blank");
        bookMeta.setAuthor("Blank");
        bookMeta.addPage(Utils.chat("&cNotes for " + target.getName()));
        ArrayList<String> pages = new ArrayList<>();
        ArrayList<Tuple<Long, String>> notes = new ArrayList<>(PlayerProfile.getPlayerProfile(target.getUniqueId()).getNotes());

        StringBuilder pageEntry = new StringBuilder();
        for (Tuple<Long, String> entry : notes) {
            String preparedString = Utils.formatDate2(entry.getLeft()) + ": " + entry.getRight();
            if (preparedString.length() + pageEntry.length() >= 245 || pageEntry.length() >= 245) {
                pages.add(pageEntry.toString());
                pageEntry = new StringBuilder();
            }
            pageEntry.append(preparedString).append("\n");
        }
        if (!pageEntry.isEmpty()) pages.add(pageEntry.toString());

        for(String page : pages) {
            if(page.isBlank()) continue;
            bookMeta.addPage(page);
        }

        writtenBook.setItemMeta(bookMeta);
        player.openBook(writtenBook);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
