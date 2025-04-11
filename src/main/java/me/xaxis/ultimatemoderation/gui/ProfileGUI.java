package me.xaxis.ultimatemoderation.gui;

import com.github.fotohh.itemutil.ItemBuilder;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.InfractionType;
import me.xaxis.ultimatemoderation.utils.Tuple;
import me.xaxis.ultimatemoderation.utils.Utils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.WrittenBookItem;
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

import java.util.Date;
import java.util.UUID;

public class ProfileGUI implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;
    private final OfflinePlayer target;

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

    private void setItems() {

        PlayerProfile profile = PlayerProfile.getPlayerProfile(target.getUniqueId());

        inventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .withTitle("&a" + target.getName())
                .withLore("Status: " + (target.isOnline() ? "&aOnline" : "&cOffline"),
                        "UUID: " + target.getUniqueId(),
                        "Banned: " + (profile.isBanned() ? "&aYes" : "&cNo"),
                        "Muted: " + (profile.isMuted() ? "&aYes" : "&cNo"),
                        "Last Seen: " + (profile.getLastJoinDate() == -1 ? "&cNever" : "&a" + Utils.formatDate(profile.getLastJoinDate())),
                        "Number of Warns: " + profile.getInfractionAmount(InfractionType.WARNING),
                        "Number of Kicks: " + profile.getInfractionAmount(InfractionType.KICK),
                        "Number of Mutes: " + profile.getInfractionAmount(InfractionType.MUTE),
                        "Number of Bans: " + profile.getInfractionAmount(InfractionType.BAN),
                        "Click to view additional notes")
                .build());
    }

    public void handleClick(InventoryClickEvent event) {

        ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) writtenBook.getItemMeta();
        bookMeta.setTitle("Notes for " + target.getName());
        bookMeta.setAuthor(player.getName());
        for(Tuple<Long, String> entry : PlayerProfile.getPlayerProfile(target.getUniqueId()).getNotes()) {
            bookMeta.addPage(Utils.formatDate2(entry.getLeft()) + ": " + entry.getRight());
        }
        writtenBook.setItemMeta(bookMeta);
        player.openBook(writtenBook); // TODO  finish this

    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
