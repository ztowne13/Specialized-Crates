package me.ztowne13.customcrates.crates.options.rewards;

import org.bukkit.inventory.ItemStack;

/**
 * Created by ztown on 2/17/2017.
 */
public abstract class GlowEffect
{
    ItemStack stack;

    public GlowEffect(ItemStack stack)
    {
        this.stack = stack;
    }

    public abstract ItemStack apply();

    public abstract ItemStack remove();
}
