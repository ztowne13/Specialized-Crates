package me.ztowne13.customcrates.utils.nbt_utils;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by ztowne13 on 3/7/16.
 */
public class NBTTagManager
{
    public static ItemStack applyTo(ItemStack item, String tag)
    {
        return NBTTagReflection.applyTo(item, tag);
    }

    public static List<String> getFrom(ItemStack item)
    {
        return NBTTagReflection.getFrom(item);
    }
}
