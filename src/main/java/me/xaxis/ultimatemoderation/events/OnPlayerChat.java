package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.spy.PlayerSpy;
import me.xaxis.ultimatemoderation.spy.SpyManager;
import me.xaxis.ultimatemoderation.type.Mute;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.UUID;

public class OnPlayerChat extends Utils implements Listener {

    private final UMP plugin;

    public OnPlayerChat(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {

        if (hasPermission(event.getPlayer(), Permissions.STAFF_CHAT)) {

            Player player = event.getPlayer();

            if (event.getMessage().startsWith("*")) {

                event.setCancelled(true);

                String msg = Utils.chat(plugin.getLangYML().getString(Lang.STAFF_CHAT_PREFIX) + "&7" + player.getDisplayName() + ":&b" + event.getMessage()).replace("*", "");
                if (!plugin.getStaffChat().contains(player))
                    plugin.getStaffChat().add(player);

                for (UUID playerUUID : plugin.getStaffChat().getPlayers()) {

                    Player target = Bukkit.getPlayer(playerUUID);

                    if (target == null || !target.isOnline()) continue;

                    target.sendMessage(msg);

                }

                message(player, Lang.STAFF_CHAT_TOGGLED);

            } else if(!event.getMessage().startsWith("*") && plugin.getStaffChat().contains(player)) {

                plugin.getStaffChat().remove(player);

                message(player, Lang.STAFF_CHAT_UNTOGGLED);

            }

        }else
        if (plugin.getMuteManager().getMutedPlayers().contains(event.getPlayer().getUniqueId())) {

            Player player = event.getPlayer();

            Mute mute = new Mute(plugin, plugin.getMuteManager().getSection().getConfigurationSection(player.getUniqueId().toString()));

            if (System.currentTimeMillis() >= mute.getTimestamp()) {
                plugin.getMuteManager().removeMutedPlayer(player.getUniqueId());
                return;
            }

            event.setCancelled(true);

            player.sendMessage(Utils.chat("&4You are currently muted! Time left: " + Utils.formatDate(mute.getTimestamp())));

        }else
        if (PlayerBanGUI.getGUI(event.getPlayer().getUniqueId()) != null) {
            PlayerBanGUI gui = PlayerBanGUI.getGUI(event.getPlayer().getUniqueId());
            if (gui.inSetReason) {

                String renameText = event.getMessage();

                gui.setReason(renameText);

                event.getPlayer().openInventory(gui.getInventory());

                gui.inSetReason = false;

                event.setCancelled(true);

            } else if (gui.inTimeSet) {

                String renameText = event.getMessage();

                if(!renameText.equalsIgnoreCase("null")) {

                    long[] values;

                    try {
                        values = Arrays.stream(renameText.split("/")).mapToLong(Long::parseLong).toArray();
                    }catch (Exception e){
                        event.getPlayer().sendMessage("Incorrect formatted! y/m/w/d/h/m/s | Or enter \"null\" to continue!");
                        return;
                    }
                    //0 y 1 m 2 w 3 d 4 h 5 m 6 s
                    if (values.length != 7) {
                        event.getPlayer().sendMessage("Incorrect amount of arguments! y/m/w/d/h/m/s | Or enter \"null\" to continue!");
                        return;
                    }

                    gui.setTime(Utils.fromYears(values[0]) + Utils.fromMonths(values[1]) + Utils.fromWeeks(values[2]) + Utils.fromDays(values[3]) + Utils.fromHours(values[4]) + Utils.fromMinutes(values[5]) + Utils.fromSeconds(values[6]));

                }

                event.getPlayer().openInventory(gui.getInventory());

                gui.inTimeSet = false;

                event.setCancelled(true);

            } else if (gui.inExtraNotes) {
                String renameText = event.getMessage();

                gui.setExtraNotes(renameText);

                event.getPlayer().openInventory(gui.getInventory());

                gui.inExtraNotes = false;

                event.setCancelled(true);
            }
        }
    }
}
