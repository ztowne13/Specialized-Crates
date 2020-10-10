package me.ztowne13.customcrates.interfaces.items.attributes;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * Created by ztown on 2/17/2017.
 */
public class BukkitGlowEffect {
    ItemStack stack;

    public BukkitGlowEffect(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack apply() {
        stack.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
        ItemMeta im = stack.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(im);
        return stack;
    }

    public ItemStack remove() {
        ItemMeta im = stack.getItemMeta();
        im.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        im.removeEnchant(Enchantment.DURABILITY);
        stack.setItemMeta(im);

        return stack;
    }
}
