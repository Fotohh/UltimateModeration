package me.xaxis.ultimatemoderation.utils;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class Utils {

    private final UMP main;

    public Utils(UMP main){
        this.main = main;
    }

    public static String chat(String s){

        return ChatColor.translateAlternateColorCodes('&', s);

    }

    public void message(Player player, Lang lang){
        player.sendMessage(main.getLangYML().getString(lang));
    }

    public void message(Player player, String msg){
        player.sendMessage(Utils.chat(msg));
    }

    public void message(Player player, String msg, Object... args){
        player.sendMessage(Utils.chat(String.format(msg, args)));
    }

    public void message(Player player, Lang lang, Object... args){
        player.sendMessage(String.format(main.getLangYML().getString(lang), args));
    }

    public boolean hasPermission(Player player, Permissions permission){
        return player.hasPermission(permission.s());
    }

    public void vanishPlayer(Player player){
        main.getRollbackManager().save(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.setCanPickupItems(false);
        player.setFlying(true);
        player.setAllowFlight(true);
        for(Player target : Bukkit.getServer().getOnlinePlayers()){
            if(target.hasPermission(Permissions.VANISH_BYPASS.s()) || !target.canSee(player)) continue;
            target.hidePlayer(main, player);
            message(target, Lang.VANISH_LEAVE_MESSAGE, player.getDisplayName());
        }
    }

    public void unvanishPlayer(Player player){
        main.getRollbackManager().restore(player);
        for(Player target : Bukkit.getServer().getOnlinePlayers()){
            if(target.canSee(player)) continue;
            target.showPlayer(main, player);
            message(target, Lang.VANISH_JOIN_MESSAGE, player.getDisplayName());
        }
    }

}
