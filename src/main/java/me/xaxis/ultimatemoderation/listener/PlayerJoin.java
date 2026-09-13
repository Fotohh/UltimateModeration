package me.xaxis.ultimatemoderation.listener;

import me.xaxis.ultimatemoderation.manager.PlayerProfileManager;
import me.xaxis.ultimatemoderation.player.PlayerProfile;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.Objects;

public class PlayerJoin implements Listener {

    private final PlayerProfileManager playerProfileManager;

    public PlayerJoin(
            PlayerProfileManager playerProfileManager
    ) {
        this.playerProfileManager =
                Objects.requireNonNull(
                        playerProfileManager,
                        "Player profile manager cannot be null"
                );
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        PlayerProfile profile =
                playerProfileManager.getPlayerProfile(
                        player.getUniqueId()
                );

        if(profile != null) {
            if(!profile.playerName()
                    .equals(player.getName())) {

                profile.updatePlayerName(
                        player.getName()
                );
            }

            return;
        }

        playerProfileManager.addPlayerProfile(
                new PlayerProfile(
                        player.getUniqueId(),
                        player.getName(),
                        new ArrayList<>()
                )
        );
    }

}
