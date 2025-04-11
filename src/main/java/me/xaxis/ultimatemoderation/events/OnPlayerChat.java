package me.xaxis.ultimatemoderation.events;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.gui.ProfileGUI;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import me.xaxis.ultimatemoderation.type.Mute;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class OnPlayerChat extends Utils implements Listener {

    private final UMP plugin;

    public OnPlayerChat(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @EventHandler
    public void notes(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        if(!ProfileGUI.getNotePrompts().containsKey(player.getUniqueId())) return;
        String message = event.getMessage();
        event.setCancelled(true);
        if(message.isEmpty()) return;
        if(message.length() > 245) {
            player.sendMessage("Message is too long! (max 245 characters)");
            return;
        }
        UUID target = ProfileGUI.getNotePrompts().get(player.getUniqueId());
        PlayerProfile profile = PlayerProfile.getPlayerProfile(target);
        profile.addNote(message);
        Bukkit.getScheduler().runTask(plugin, () -> {
            player.sendMessage("Note added: " + message +" \nTimestamp: " + Utils.formatDate2(System.currentTimeMillis()));
            new ProfileGUI(player, target).open();
        });
        ProfileGUI.getNotePrompts().remove(player.getUniqueId());
    }

    @EventHandler
    public void playerChat(AsyncPlayerChatEvent event) {
        if (hasPermission(event.getPlayer(), Permissions.STAFF_CHAT)) {
            Player player = event.getPlayer();
            if (event.getMessage().startsWith("*")) {
                event.setCancelled(true);
                String msg = Utils.chat(plugin.getLangYML().getString(Lang.STAFF_CHAT_PREFIX) + "&7" + player.getDisplayName() + ":&b" + event.getMessage()).replace("*", "");
                if (!plugin.getStaffChat().isInChat(player)) {
                    plugin.getStaffChat().setInChat(player, true);
                    message(player, Lang.STAFF_CHAT_TOGGLED);
                }

                for (UUID playerUUID : plugin.getStaffChat().getPlayers().keySet()) {
                    if (!plugin.getStaffChat().chatIsVisible(player)) continue;
                    Player target = Bukkit.getPlayer(playerUUID);
                    if (target == null || !target.isOnline()) continue;
                    target.sendMessage(msg);
                }
            } else if (!event.getMessage().startsWith("*") && plugin.getStaffChat().isInChat(player)) {
                plugin.getStaffChat().setInChat(player, false);
            }
        }
    }

    @EventHandler
    public void banGUI(AsyncPlayerChatEvent event) {
        if (PlayerBanGUI.getGUI(event.getPlayer().getUniqueId()) == null) return;
        PlayerBanGUI gui = PlayerBanGUI.getGUI(event.getPlayer().getUniqueId());
        String renameText = event.getMessage();
        if (gui.inSetReason) {

            gui.setReason(renameText);
            event.getPlayer().sendMessage("Set reason to: " + gui.getReason());
            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().openInventory(gui.getInventory()), 1L);
            gui.inSetReason = false;
            event.setCancelled(true);

        } else if (gui.inTimeSet) {
            char currentTimeChar = '`';
            long time = 0;

            for (int i = 0; i < renameText.length(); i++) {
                if (!Character.isDigit(renameText.charAt(i))) {
                    currentTimeChar = renameText.charAt(i);
                }
            }
            for (int i = 0; i < renameText.length(); i++) {
                if (Character.isDigit(renameText.charAt(i))) {
                    long digit = Long.parseLong(String.valueOf(renameText.charAt(i)));
                    time = time * 10 + digit;
                }
            }
            if (currentTimeChar == '`') {
                event.getPlayer().sendMessage(Utils.chat("&cPlease enter a valid time!"));
                return;
            }
            time = switch (currentTimeChar) {
                case 's' -> Utils.fromSeconds(time);
                case 'm' -> Utils.fromMinutes(time);
                case 'h' -> Utils.fromHours(time);
                case 'd' -> Utils.fromDays(time);
                case 'w' -> Utils.fromWeeks(time);
                case 'M' -> Utils.fromMonths(time);
                case 'y' -> Utils.fromYears(time);
                default -> {
                    event.getPlayer().sendMessage(Utils.chat("&cPlease enter a valid time!"));
                    yield -1;
                }
            };

            gui.setTime(time);

            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().openInventory(gui.getInventory()), 1L);

            event.getPlayer().sendMessage("Set time to: " + gui.getTime());

            gui.inTimeSet = false;
            event.setCancelled(true);
        } else if (gui.inExtraNotes) {
            gui.setExtraNotes(renameText);
            event.getPlayer().sendMessage("Set extra notes to: " + gui.getExtraNotes());
            Bukkit.getScheduler().runTaskLater(plugin, () -> event.getPlayer().openInventory(gui.getInventory()), 1L);
            gui.inExtraNotes = false;
            event.setCancelled(true);

        }
    }

    @EventHandler
    public void muteChat(AsyncPlayerChatEvent event) {
        if (plugin.getMuteManager().getMutedPlayers().contains(event.getPlayer().getUniqueId())) {

            Player player = event.getPlayer();
            Mute mute = new Mute(plugin, plugin.getMuteManager().getSection().getConfigurationSection(player.getUniqueId().toString()));

            if (System.currentTimeMillis() >= mute.getTimestamp()) {
                plugin.getMuteManager().removeMutedPlayer(player.getUniqueId());
                return;
            }

            event.setCancelled(true);
            player.sendMessage(Utils.chat("&4You are currently muted! Time left: " + Utils.formatDate(mute.getTimestamp())));
        }
    }
}
