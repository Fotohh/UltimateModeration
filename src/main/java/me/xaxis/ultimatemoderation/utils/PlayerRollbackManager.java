package me.xaxis.ultimatemoderation.utils;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerRollbackManager {

    private final Map<UUID, Location> previousLocationMap = new HashMap<>();
    private final Map<UUID, GameMode> previousGameModeMap = new HashMap<>();
    private final Map<UUID, Boolean> previousCanFlyMap = new HashMap<>();
    private final Map<UUID, ItemStack[]> previousInventoryContents = new HashMap<>();
    private final Map<UUID, ItemStack[]> previousArmorContents = new HashMap<>();
    private final Map<UUID, Integer> previousHungerValue = new HashMap<>();
    private final Map<UUID, Integer> previousLevelMap = new HashMap<>();

    public void save(Player player) {

        previousLocationMap.put(player.getUniqueId(), player.getLocation());
        previousGameModeMap.put(player.getUniqueId(), player.getGameMode());
        previousInventoryContents.put(player.getUniqueId(), player.getInventory().getContents());
        previousArmorContents.put(player.getUniqueId(), player.getInventory().getArmorContents());
        previousHungerValue.put(player.getUniqueId(), player.getFoodLevel());
        previousLevelMap.put(player.getUniqueId(), player.getLevel());
        previousCanFlyMap.put(player.getUniqueId(), player.getAllowFlight());
        player.getInventory().clear();

    }

    public void restore(Player player) {

        player.getInventory().clear();

        ItemStack[] inventoryContent = previousInventoryContents.get(player.getUniqueId());
        if(inventoryContent != null) {

            player.getInventory().setContents(inventoryContent);

        }

        ItemStack[] armorContents = previousArmorContents.get(player.getUniqueId());
        if(armorContents != null) {

            player.getInventory().setArmorContents(armorContents);

        }

        GameMode previousGameMode = previousGameModeMap.get(player.getUniqueId());
        if(previousGameMode != null) {

            player.setGameMode(previousGameMode);

        }

        Location previousLocation = previousLocationMap.get(player.getUniqueId());
        if(previousLocation != null) {

            player.teleport(previousLocation);

        }

        player.setFoodLevel(previousHungerValue.getOrDefault(player.getUniqueId(), 20));

        player.setLevel(previousLevelMap.getOrDefault(player.getUniqueId(), 0));

        player.setAllowFlight(previousCanFlyMap.get(player.getUniqueId()));

        previousHungerValue.remove(player.getUniqueId());
        previousLocationMap.remove(player.getUniqueId());
        previousInventoryContents.remove(player.getUniqueId());
        previousArmorContents.remove(player.getUniqueId());
        previousGameModeMap.remove(player.getUniqueId());
        previousLevelMap.remove(player.getUniqueId());

    }

}
