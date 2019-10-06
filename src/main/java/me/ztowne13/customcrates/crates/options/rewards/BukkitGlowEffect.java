package me.ztowne13.customcrates.crates.options.rewards;

import org.bukkit.inventory.ItemStack;


/**
 * Created by ztown on 2/17/2017.
 */
public class BukkitGlowEffect extends GlowEffect
{
    public BukkitGlowEffect(ItemStack stack)
    {
        super(stack);
    }

    @Override
    public ItemStack apply()
    {
//        if (NMSUtils.Version.v1_8.isServerVersionOrLater())
//        {
//            stack.addUnsafeEnchantment(Enchantment.DURABILITY, 0);
//            ItemMeta im = stack.getItemMeta();
//            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//            stack.setItemMeta(im);
//            return stack;
//        }
        return stack;
    }

    @Override
    public ItemStack remove()
    {
//        if (NMSUtils.Version.v1_8.isServerVersionOrLater())
//        {
//            ItemMeta im = stack.getItemMeta();
//            im.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
//            im.removeEnchant(Enchantment.DURABILITY);
//            stack.setItemMeta(im);
//
//            return stack;
//        }
        return stack;
    }
}
