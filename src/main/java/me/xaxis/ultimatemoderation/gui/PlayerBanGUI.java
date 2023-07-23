package me.xaxis.ultimatemoderation.gui;

import me.xaxis.ultimatemoderation.UMP;
import me.xaxis.ultimatemoderation.utils.ItemUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.UUID;

public class PlayerBanGUI extends GUI {

    private final static HashMap<UUID, PlayerBanGUI> map = new HashMap<>();

    public static PlayerBanGUI getGUI(UUID uuid){
        return map.get(uuid);
    }

    public void removeGUI(){
        map.remove(getHolder().getUniqueId());
    }

    private final UMP plugin;

    private final Player target;

    public Player getTarget() {
        return target;
    }

    public void setReasonGUI(GUI reasonGUI) {
        this.reasonGUI = reasonGUI;
    }

    private GUI reasonGUI;

    public GUI getReasonGUI() {
        return reasonGUI;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getExtraNotes() {
        return extraNotes;
    }

    public boolean inSetReason = true;
    public boolean inExtraNotes = true;
    public boolean inTimeSet = true;

    public void setExtraNotes(String extraNotes) {
        this.extraNotes = extraNotes;
    }

    public PlayerBanGUI(Player player, Player target, UMP plugin) {
        super("Ban Menu", 9, player);
        this.plugin = plugin;
        this.target = target;
        map.put(player.getUniqueId(), this);
        addItems();
        player.openInventory(getInventory());
    }


    private void addItems(){
        ItemUtil barrier = new ItemUtil(Material.BARRIER);
        barrier.withTitle("&4Cancel").build();
        ItemUtil rc = new ItemUtil(Material.RED_CONCRETE);
        rc.withTitle("&cBan Player").build();
        ItemUtil b = new ItemUtil(Material.BOOK);
        b.withTitle("&fAdd Extra notes").build();
        ItemUtil c = new ItemUtil(Material.COMPASS);
        c.withTitle("&fSet ban time").withLore("&7Automatically set","&7to &6&lpermanent&7!").build();
        ItemUtil p = new ItemUtil(Material.PAPER);
        p.withTitle("&fSet ban reason").build();
        ItemUtil h = new ItemUtil(Material.PLAYER_HEAD);
        h.withTitle(target.getDisplayName()).withLore(target.getUniqueId().toString()).build();
        SkullMeta m = (SkullMeta) h.getItemMeta();
        if(m == null) return;
        m.setOwningPlayer(target);
        h.setItemMeta(m);

        getInventory().setItem(0, barrier);
        getInventory().setItem(1, b);
        getInventory().setItem(3, c);
        getInventory().setItem(5, h);
        getInventory().setItem(7, p);
        getInventory().setItem(8, rc);

        for(int i = 0; i < getInventory().getSize() - 1; i++){
            ItemStack a = getInventory().getItem(i);
            if(a == null || a.getType() != Material.AIR) continue;
            ItemUtil z = new ItemUtil(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            z.withTitle("").withLore("").build();
            getInventory().setItem(i,z);
        }

    }

    private Long time = null;

    private String reason = "No reason";

    private String extraNotes = "No extra notes";

}
