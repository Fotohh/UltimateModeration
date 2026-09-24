package me.xaxis.ultimatemoderationplus.listener;

import me.xaxis.ultimatemoderationplus.constants.ConfigConstants;
import me.xaxis.ultimatemoderationplus.infractions.Ban;
import me.xaxis.ultimatemoderationplus.lang.Lang;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import me.xaxis.ultimatemoderationplus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;

public class PlayerLogin implements Listener {

    private final PlayerProfileManager playerProfileManager;
    private final LangManager langManager;
    private final JavaPlugin plugin;

    public PlayerLogin(PlayerProfileManager playerProfileManager, LangManager langManager, JavaPlugin plugin) {
        this.playerProfileManager = playerProfileManager;
        this.langManager = langManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerId = event.getUniqueId();
        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(playerId);
        if (playerProfile == null) return;
        Ban ban = playerProfileManager.getPlayerBan(playerProfile);
        if (ban == null) return;

        long now = System.currentTimeMillis();
        long expiresAt = ban.timeUntil();

        if (expiresAt != -1 && expiresAt <= now) {
            Bukkit.getScheduler().runTask(
                    plugin,
                    () -> {
                        if (playerProfileManager.getPlayerBan(playerProfile) == ban) {
                            playerProfileManager.unbanPlayer(playerProfile);
                        }
                    }
            );
            return;
        }

        String duration = expiresAt == -1
                ? "never"
                : Utils.formatDuration(expiresAt - now);

        String message = langManager.replacePlaceholders(
                langManager.getMessage(Lang.LOGIN_BAN_MESSAGE),
                Map.of(
                        Placeholders.STAFF, ban.staffName(),
                        Placeholders.REASON, ban.reason(),
                        Placeholders.DURATION, duration
                )
        );

        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                message
        );
    }
}
