package me.xaxis.ultimatemoderation.utils;

import org.bukkit.ChatColor;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static String formatDuration(long durationMillis) {
        long seconds = durationMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        } else if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        } else if (minutes > 0) {
            return minutes + "m " + (seconds % 60) + "s";
        } else {
            return seconds + "s";
        }
    }

    public static String chat(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
