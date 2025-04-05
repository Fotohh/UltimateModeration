package me.xaxis.ultimatemoderation.type;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class Ban extends Infraction {

    private final boolean ip;
    private final long time;
    public Ban(String reason, UUID punisher, UUID punished, long time, boolean ip) {
        super(reason, punisher, punished, time, InfractionType.BAN);
        this.ip = ip;
        this.time = time;
        Player player = Bukkit.getPlayer(punished);
        if(player.isOnline()) {
            if (ip) {
                if(time == -1)player.banIp(reason,(Date) null, Bukkit.getPlayer(punisher).getDisplayName(), true);
                else player.banIp(reason, Instant.now().plusMillis(time), Bukkit.getPlayer(punisher).getDisplayName(), true);
            } else {
                if(time == -1) player.ban(reason, (Date) null, Bukkit.getPlayer(punisher).getDisplayName(), true);
                else player.ban(reason, Instant.now().plusMillis(time), Bukkit.getPlayer(punisher).getDisplayName(), true);
            }
        }else{
            OfflinePlayer o = Bukkit.getOfflinePlayer(punished);
            if (ip) {
                Bukkit.getServer().banIP(o.getPlayer().getAddress().getAddress());
            } else {
                if(time == -1) o.ban(reason, (Date) null, Bukkit.getPlayer(punisher).getDisplayName());
                else o.ban(reason, Instant.now().plusMillis(time), Bukkit.getPlayer(punisher).getDisplayName());
            }
        }
    }

    @Override
    InfractionType getInfractionType() {
        return ip ? InfractionType.IP_BAN : (time == 0 ? InfractionType.PERM_BAN : InfractionType.TEMP_BAN);
    }
}
