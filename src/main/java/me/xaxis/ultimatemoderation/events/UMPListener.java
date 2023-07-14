package me.xaxis.ultimatemoderation.events;

import org.bukkit.event.Listener;

public interface UMPListener extends Listener {
    boolean isDependent();
    String getDependentPlugin();
}
