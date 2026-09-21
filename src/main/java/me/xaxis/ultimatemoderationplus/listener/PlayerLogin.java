package me.xaxis.ultimatemoderationplus.listener;

import me.xaxis.ultimatemoderationplus.infractions.Ban;
import me.xaxis.ultimatemoderationplus.lang.LangKey;
import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.lang.Placeholders;
import me.xaxis.ultimatemoderationplus.player.PlayerProfile;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.Map;
import java.util.UUID;

public class PlayerLogin implements Listener {

    private final PlayerProfileManager playerProfileManager;
    private final LangManager langManager;

    public PlayerLogin(PlayerProfileManager playerProfileManager, LangManager langManager) {
        this.playerProfileManager = playerProfileManager;
        this.langManager = langManager;
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        UUID playerId = event.getUniqueId();
        PlayerProfile playerProfile = playerProfileManager.getPlayerProfile(playerId);
        if (playerProfile == null) return;
        Ban ban = playerProfileManager.getPlayerBan(playerProfile);
        if (ban == null) return;
        String message = langManager.replacePlaceholders(
                langManager.getMessage(LangKey.LOGIN_BAN_MESSAGE),
                Map.of(
                        Placeholders.STAFF, ban.staffName(),
                        Placeholders.REASON, ban.reason()
                )
        );

        event.disallow(
                AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                message
        );
    }
}
