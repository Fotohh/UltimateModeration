package me.xaxis.ultimatemoderationplus.listener;

import me.xaxis.ultimatemoderationplus.infractions.Mute;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class PlayerChatEvent implements Listener {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;
    private final JavaPlugin plugin;

    public PlayerChatEvent(LangManager langManager, PlayerProfileManager playerProfileManager, JavaPlugin plugin) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(playerId);
        if(playerProfile == null) {
            Logger.getLogger("AsyncPlayerChatEvent").severe(player.getName() + " has no player profile." +
                    "This should not happen.");
            return;
        }

        Mute mute = playerProfileManager.getMute(playerProfile);

        if(mute == null) {
            return;
        }

        String message =
                langManager.replacePlaceholders(
                        langManager.getMessage(
                                LangKey.PLAYER_MUTED
                        ),
                        Map.of(
                                Placeholders.REASON,
                                mute.reason()
                        )
                );

        event.setCancelled(true);

        Bukkit.getScheduler().runTask(
                plugin,
                () -> player.sendMessage(message)
        );
    }
}
