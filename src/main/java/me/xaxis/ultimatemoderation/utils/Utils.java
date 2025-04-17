package me.xaxis.ultimatemoderation.utils;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
//import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.time.*;
import java.time.temporal.ChronoUnit;

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

    public static long fromSeconds(long seconds){
        return seconds * 1000;
    }
    public static long fromMinutes(long minutes){
        return minutes * 1000 * 60;
    }
    public static long fromHours(long hours){
        return hours * 1000 * 60 * 60;
    }
    public static long fromDays(long days){
        return days * 1000 * 60 * 60 * 24;
    }
    public static long fromWeeks(long weeks){
        return weeks * 1000 * 60 * 60 * 24 * 7;
    }
    public static long fromMonths(long months){
        return months * 1000 * 60 * 60 * 24 * 30;
    }
    public static long fromYears(long years){
        return years * 1000 * 60 * 60 * 24 * 365;
    }

    public static String formatDate2(long l) {
        LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());

        int hour = dt.getHour();
        String amPm = hour >= 12 ? "PM" : "AM";
        hour = hour % 12 == 0 ? 12 : hour % 12;

        return String.format("%02d/%02d/%d %02d:%02d %s",
                dt.getMonthValue(),
                dt.getDayOfMonth(),
                dt.getYear(),
                hour,
                dt.getMinute(),
                amPm);
    }

    public static String formatDate(long l){

        LocalDateTime targetDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());

        Period period = Period.between(LocalDate.now(), targetDateTime.toLocalDate());

        int years = Math.abs(period.getYears());
        int months = Math.abs(period.getMonths());
        long weeks = Math.abs(ChronoUnit.WEEKS.between(LocalDate.now(), targetDateTime.toLocalDate()));
        long days = Math.abs(ChronoUnit.DAYS.between(LocalDate.now(), targetDateTime.toLocalDate()) % 7);
        long hours = Math.abs(ChronoUnit.HOURS.between(LocalDateTime.now(), targetDateTime) % 24);
        long minutes = Math.abs(ChronoUnit.MINUTES.between(LocalDateTime.now(), targetDateTime) % 60);
        long seconds = Math.abs(ChronoUnit.SECONDS.between(LocalDateTime.now(), targetDateTime) % 60);

        StringBuilder resultBuilder = new StringBuilder();
        if(years > 0) resultBuilder.append(years).append(" years, ");
        if(months > 0) resultBuilder.append(months).append(" months, ");
        if (weeks > 0) resultBuilder.append(weeks).append(" weeks, ");
        if (days > 0) resultBuilder.append(days).append(" days, ");
        if (hours > 0) resultBuilder.append(hours).append(" hours, ");
        if (minutes > 0) resultBuilder.append(minutes).append(" minutes, ");
        if (seconds > 0 || resultBuilder.isEmpty()) resultBuilder.append(seconds).append(" seconds");

        return resultBuilder.toString();
    }

    public void vanishPlayer(Player player, boolean rollbackManager){
        if(rollbackManager) {
            main.getRollbackManager().save(player);
            player.getInventory().clear();
        }
        player.setInvulnerable(true);
        player.setCanPickupItems(false);
        player.setAllowFlight(true);
        if(!player.isFlying())
            player.setFlying(true);
        for(Player target : Bukkit.getServer().getOnlinePlayers()){
            if(target.hasPermission(Permissions.VANISH_BYPASS.s()) || !target.canSee(player)) continue;
            target.hidePlayer(main, player);
            message(target, Lang.VANISH_LEAVE_MESSAGE, player.getDisplayName());
        }
    }

    public void unvanishPlayer(Player player, boolean rollbackManager){
        player.setCanPickupItems(true);
        player.setInvulnerable(false);
        if(rollbackManager) {
            player.getInventory().clear();
            main.getRollbackManager().restore(player);
        }
        for(Player target : Bukkit.getServer().getOnlinePlayers()){
            if(target.canSee(player)) continue;
            target.showPlayer(main, player);
            message(target, Lang.VANISH_JOIN_MESSAGE, player.getDisplayName());
        }
    }

}
