package me.xaxis.ultimatemoderation.events;

import io.github.xaxisplayz.reportplus.api.events.ReportPlayerEvent;
import me.xaxis.reportplus.reports.Report;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnPlayerReport implements Listener {

    @EventHandler
    public void playerReport(ReportPlayerEvent event){
        Player player = event.getPlayer();
        Report report = event.getReport();

        player.performCommand("spy "+player.getName());
    }
}
