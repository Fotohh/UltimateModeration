package me.xaxis.ultimatemoderationplus.commands;

import me.xaxis.ultimatemoderationplus.lang.LangManager;
import me.xaxis.ultimatemoderationplus.player.PlayerProfileManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jspecify.annotations.NonNull;

public class MuteCommand implements CommandExecutor {

    private final LangManager langManager;
    private final PlayerProfileManager playerProfileManager;

    public MuteCommand(LangManager langManager, PlayerProfileManager playerProfileManager) {
        this.langManager = langManager;
        this.playerProfileManager = playerProfileManager;
    }

    @Override
    public boolean onCommand(@NonNull CommandSender sender, @NonNull Command command, @NonNull String label, @NonNull String[] args) {




        return true;
    }
}
