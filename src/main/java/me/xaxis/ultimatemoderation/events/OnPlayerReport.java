package me.xaxis.ultimatemoderation.events;

import io.github.xaxisplayz.reportplus.api.events.ReportPlayerEvent;
import me.xaxis.reportplus.reports.Report;
import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class OnPlayerReport implements UMPListener {

    private final UMP plugin;

    public OnPlayerReport(UMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void playerReport(ReportPlayerEvent event){
        Player player = event.getPlayer();
        Report report = event.getReport();

        player.performCommand("spy "+player.getName());
    }

    @Override
    public boolean isDependent() {
        return true;
    }
}
