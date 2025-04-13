package me.xaxis.ultimatemoderation.updatechecker;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class UpdateChecker {

    private final int resourceID;
    private final String pluginVersion;
    private final JavaPlugin plugin;
    private final Logger logger;
    private static final String apiUrl = "https://api.spiget.org/v2/resources/%d/versions/latest";
    private final String upToDatePluginMessage;
    private final String outdatedPluginMessage;

    public UpdateChecker(int resourceID, String resourceURL, JavaPlugin plugin, String loggerName) {
        this.resourceID = resourceID;
        this.pluginVersion = plugin.getDescription().getVersion();
        this.plugin = plugin;
        upToDatePluginMessage = plugin.getName() + " is up to date";
        outdatedPluginMessage = plugin.getName() + " is outdated! Download the new update from " + resourceURL;
        logger = Logger.getLogger(Objects.requireNonNullElseGet(loggerName, () -> plugin.getName() + " UpdateChecker"));
        runAsync();
    }

    public void runAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String webVersion;
            try {
                webVersion = requestServerVersion(fetchURL());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (isUpToDate(parseVersion(pluginVersion), parseVersion(webVersion))) {
                logger.info(upToDatePluginMessage);
            } else {
                logger.warning(outdatedPluginMessage);
            }
        });
    }

    public List<Integer> parseVersion(String version) {
        List<Integer> list = new ArrayList<>();

        String[] values = null;
        if(version.contains(".")) values = version.split("\\.");
        if(values == null) {
            int num = Integer.parseInt(version);
            list.add(num);
            return list;
        }
        for (String val : values) {
            int num = Integer.parseInt(val);
            list.add(num);
        }
        return list;
    }

    public boolean isUpToDate(List<Integer> parsedPluginVersion, List<Integer> parsedWebVersion) {
        if(parsedWebVersion.size() > parsedPluginVersion.size()) {
            int diff = parsedWebVersion.size() - parsedPluginVersion.size();
            for (int i = 0; i < diff; i++) {
                parsedPluginVersion.add(0);
            }
        }else if(parsedWebVersion.size() < parsedPluginVersion.size()) {
            int diff = parsedPluginVersion.size() - parsedWebVersion.size();
            for (int i = 0; i < diff; i++) {
                parsedWebVersion.add(0);
            }
        }
        for (int i = 0; i < parsedWebVersion.size(); i++) {
            if (parsedWebVersion.get(i) > parsedPluginVersion.get(i)) {
                return false;
            }
        }
        return true;
    }

    public HttpsURLConnection fetchURL() throws IOException {
        URL url = URI.create(String.format(apiUrl, resourceID)).toURL();
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    public String requestServerVersion(HttpsURLConnection connection) throws IOException {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            JsonObject response = JsonParser.parseReader(in).getAsJsonObject();
            return response.get("name").getAsString();
        }
    }

}
