package me.xaxis.ultimatemoderation.spy;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.gui.PlayerBanGUI;
import me.xaxis.ultimatemoderation.utils.ItemUtil;
import me.xaxis.ultimatemoderation.utils.PlayerRollbackManager;
import me.xaxis.ultimatemoderation.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_20_R3.block.CraftChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class SpyManager extends Utils implements Listener{

    private final UMP plugin;
    private final HashMap<UUID, PlayerSpy> spyHashMap = new HashMap<>();

    public HashMap<UUID, PlayerSpy> getSpyHashMap() {
        return spyHashMap;
    }

    public SpyManager(UMP plugin) {
        super(plugin);
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void addPlayer(Player player, Player target, PlayerRollbackManager manager){
        spyHashMap.put(player.getUniqueId(), new PlayerSpy(player, target, manager));
    }

    public boolean containsPlayer(Player player){
        return spyHashMap.containsKey(player.getUniqueId());
    }

    public void removePlayer(Player player){
        unvanishPlayer(player, false);
        spyHashMap.get(player.getUniqueId()).restore();
        spyHashMap.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event){
        if(containsPlayer(event.getPlayer())){
            removePlayer(event.getPlayer());
        }
    }

    private final Map<UUID, Block> chestMap = new HashMap<>();

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (chestMap.containsKey(uuid)) {
            Block block = chestMap.remove(uuid);
            if (block.getState() instanceof InventoryHolder holder) {
                holder.getInventory().setContents(event.getInventory().getContents());
            }else if(block.getType() == Material.ENDER_CHEST){
                Player target = spyHashMap.get(uuid).getTarget();
                target.getEnderChest().setContents(event.getInventory().getContents());
            }
        }
    }

    private void handleInventory(InventoryInteractEvent event){
        Player player = (Player) event.getWhoClicked();
        UUID uuid = player.getUniqueId();
        if (chestMap.containsKey(uuid)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                Block block = chestMap.get(uuid);
                if (block.getState() instanceof InventoryHolder holder) {
                    holder.getInventory().setContents(event.getInventory().getContents());
                }else if(block.getType() == Material.ENDER_CHEST){
                    Player target = spyHashMap.get(uuid).getTarget();
                    target.getEnderChest().setContents(event.getInventory().getContents());
                }
            }, 1L);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        handleInventory(event);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        handleInventory(event);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerInteractOnBlock(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!containsPlayer(player)) return;
        if (!event.hasBlock() && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!(event.getClickedBlock().getState() instanceof InventoryHolder holder)) {
            if (block.getType() == Material.ENDER_CHEST) {
                event.setCancelled(true);
                Player target = spyHashMap.get(player.getUniqueId()).getTarget();
                Inventory fakeInv = Bukkit.createInventory(null, target.getEnderChest().getSize(), "Chest");
                fakeInv.setContents(target.getEnderChest().getContents());
                chestMap.put(event.getPlayer().getUniqueId(), block);
                player.openInventory(fakeInv);
            }
            return;
        }
        event.setCancelled(true);
        Inventory fakeInv = Bukkit.createInventory(null, Math.max(holder.getInventory().getSize(), 9), "Chest");
        fakeInv.setContents(holder.getInventory().getContents());
        chestMap.put(event.getPlayer().getUniqueId(), block);
        player.openInventory(fakeInv);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!containsPlayer(player)) return;
        if(!event.hasItem() || event.getItem() == null || event.getItem().getType() == Material.AIR) return;
        PlayerSpy playerSpy = spyHashMap.get(player.getUniqueId());
        switch (event.getPlayer().getInventory().getItemInMainHand().getType()){
            case BARRIER -> removePlayer(player);
            case ANVIL -> new PlayerBanGUI(player, playerSpy.getTarget());
            case CHEST -> player.openInventory(playerSpy.getTarget().getInventory());
        }
        event.setCancelled(true);
    }

    public ItemStack[] getDefaultContents() {
        ItemUtil barrier = new ItemUtil(Material.BARRIER);
        barrier.withTitle("&cExit")
                .withLore("&7Left or right","&7click to exit!")
                .build();
        ItemUtil anvil = new ItemUtil(Material.ANVIL);
        anvil.withTitle("&4Ban Player")
                .withLore("&7Left or right","&7click to ban","the player!")
                .build();
        ItemUtil chest = new ItemUtil(Material.CHEST);
        chest.withTitle("&4Spy Chest")
                .withLore("&7Left or right","&7click to open","the player's inventory!")
                .build();
        return new ItemStack[]{barrier, anvil, chest};
    }

}
