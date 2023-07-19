package me.xaxis.ultimatemoderation.utils;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ItemUtil extends ItemStack{

    private final ItemStack i;
    private final ItemMeta im;

    public ItemUtil(Material m){
        super(m);
        this.i = new ItemStack(m);
        this.im = i.getItemMeta();
    }

    public ItemUtil withLore(String... l){
        im.setLore(Arrays.stream(l).map(Utils::chat).collect(Collectors.toList()));
        return this;
    }

    public ItemUtil withTitle(String t){
        im.setDisplayName(Utils.chat(t));
        return this;
    }

    public ItemUtil withEnchantments(EnchantmentBuilder... e){
        Arrays.stream(e).forEach(enchantment -> im.addEnchant(enchantment.e(), enchantment.i(), enchantment.v()));
        return this;
    }

    public ItemUtil build(){
        i.setItemMeta(im);
        return this;
    }

}

