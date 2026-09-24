package me.xaxis.ultimatemoderationplus.utils;

import org.bukkit.ChatColor;

import java.util.Locale;

public class Utils {
    private Utils() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public static long parseDuration(String unparsedDuration) {
        if (unparsedDuration == null || unparsedDuration.isBlank()) {
            return -1;
        }

        String duration = unparsedDuration.toLowerCase(Locale.ROOT);

        if (duration.length() < 2) {
            return -1;
        }

        char unit = duration.charAt(duration.length() - 1);
        String numberPart = duration.substring(0, duration.length() - 1);

        long amount;

        try {
            amount = Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            return -1;
        }

        if (amount <= 0) {
            return -1;
        }

        long multiplier = switch (unit) {
            case 's' -> 1_000L;
            case 'm' -> 60_000L;
            case 'h' -> 3_600_000L;
            case 'd' -> 86_400_000L;
            case 'w' -> 604_800_000L;
            default -> -1L;
        };

        if (multiplier == -1) {
            return -1;
        }

        try {
            return Math.multiplyExact(amount, multiplier);
        } catch (ArithmeticException e) {
            return -1;
        }
    }

    public static String formatDuration(long durationMillis) {
        if(durationMillis <= 0) {
            return "now";
        }
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
