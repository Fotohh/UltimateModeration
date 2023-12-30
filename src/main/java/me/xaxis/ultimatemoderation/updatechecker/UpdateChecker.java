package me.xaxis.ultimatemoderation.updatechecker;

import me.xaxis.ultimatemoderation.UMP;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

public class UpdateChecker {

    private final UMP ump;

    private double webVersion;

    private static final String SPIGET_API_URL = "https://api.spiget.org/v2/resources/%d/versions?size=1&sort=-releaseDate";

    //from spigot page
    private static final String RESOURCE_LINK = "";

    public UpdateChecker(UMP ump) {

        this.ump = ump;

        double version = Double.parseDouble(ump.getDescription().getVersion());

        new BukkitRunnable() {
            @Override
            public void run() {
                fetchResourceVersion();
            }
        }.runTaskAsynchronously(ump);

        if(version < webVersion){
            ump.getLogger().warning("You are not running the latest version!\n Please download the latest version from here: " + RESOURCE_LINK);
        }

    }

    private void fetchResourceVersion() {
        try {
            // Create the API URL with the resource ID
            int resourceId = 12345;
            String apiUrl = String.format(SPIGET_API_URL, resourceId);

            // Create a connection to the Spiget API
            HttpsURLConnection connection = createConnection(apiUrl);

            // Read the response from the API
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JSONArray versions = new JSONArray(response.toString());
            if (!versions.isEmpty()) {
                JSONObject latestVersion = versions.getJSONObject(0);
                webVersion = Double.parseDouble(latestVersion.getString("name"));
            } else {
                ump.getLogger().info("No versions found for the resource.");
            }
        } catch (Exception e) {
            ump.getLogger().severe("Failed to fetch resource version: " + e.getMessage());
        }
    }

    private HttpsURLConnection createConnection(String apiURL){

        URL url;

        HttpsURLConnection connection;

        try {

            url = URI.create(apiURL).toURL();

            connection = (HttpsURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

        } catch (IOException e) {

            throw new RuntimeException(e);

        }

        connection.setDoOutput(true);

        return connection;

    }


}
