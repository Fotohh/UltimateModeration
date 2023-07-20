package me.xaxis.ultimatemoderation.utils;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.configmanagement.Lang;
import me.xaxis.ultimatemoderation.configmanagement.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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
        return minutes * 60000L;
    }
    public static long fromHours(long hours){
        return hours * 3600000L;
    }
    public static long fromDays(long days){
        return days * 86400000L;
    }
    public static long fromWeeks(long weeks){
        return weeks * 604800000L;
    }
    public static long fromMonths(long months){
        return months * 2629746000L;
    }
    public static long fromYears(long years){
        return years * 31556926000L;
    }

    /**
     %% – Inserts a “%” sign
     %x/%X – Integer hexadecimal
     %t/%T – Time and Date
     %s/%S – String
     %n – Inserts a newline character
     %o – Octal integer
     %f – Decimal floating-point
     %e/%E – Scientific notation
     %g – Causes Formatter to use either %f or %e, whichever is shorter
     %h/%H – Hash code of the argument
     %d – Decimal integer
     %c – Character
     %b/%B – Boolean
     %a/%A – Floating-point hexadecimal
     */
    public static String formatDate(long l){

        LocalDateTime targetDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneId.systemDefault());

        Period period = Period.between(LocalDate.now(), targetDateTime.toLocalDate());

        int years = period.getYears();
        int months = period.getMonths();
        long weeks = ChronoUnit.WEEKS.between(LocalDate.now(), targetDateTime.toLocalDate());
        long days = ChronoUnit.DAYS.between(LocalDate.now(), targetDateTime.toLocalDate()) % 7;
        long hours = ChronoUnit.HOURS.between(LocalDateTime.now(), targetDateTime) % 24;
        long minutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), targetDateTime) % 60;
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), targetDateTime) % 60;

        StringBuilder resultBuilder = new StringBuilder();
        if(years > 0) resultBuilder.append(years).append(" years, ");
        if(months > 0) resultBuilder.append(months).append(" months, ");
        if (weeks > 0) resultBuilder.append(weeks).append(" weeks, ");
        if (days > 0) resultBuilder.append(days).append(" days, ");
        if (hours > 0) resultBuilder.append(hours).append(" hours, ");
        if (minutes > 0) resultBuilder.append(minutes).append(" minutes, ");
        if (seconds > 0 || resultBuilder.length() == 0) resultBuilder.append(seconds).append(" seconds");

        return resultBuilder.toString();
    }

    public void vanishPlayer(Player player, boolean rollbackManager){
        if(rollbackManager)
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

    public void unvanishPlayer(Player player, boolean rollbackManager){
        if(rollbackManager)
            main.getRollbackManager().restore(player);
        for(Player target : Bukkit.getServer().getOnlinePlayers()){
            if(target.canSee(player)) continue;
            target.showPlayer(main, player);
            message(target, Lang.VANISH_JOIN_MESSAGE, player.getDisplayName());
        }
    }

}
