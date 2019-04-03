package me.ztowne13.customcrates.interfaces.items;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class CompressedEnchantment
{
    Enchantment enchantment;
    int level;

    public CompressedEnchantment(Enchantment enchantment, int level)
    {
        this.enchantment = enchantment;
        this.level = level;
    }

    public void applyTo(ItemStack stack)
    {
        stack.addUnsafeEnchantment(enchantment, level);
    }

    @Override
    public String toString()
    {
        return enchantment.getName() + ";" + level;
    }

    public Enchantment getEnchantment()
    {
        return enchantment;
    }

    public int getLevel()
    {
        return level;
    }

    public static CompressedEnchantment fromString(String value) throws Exception
    {
        String[] split = value.split(";");

        Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());
        if(enchantment == null)
            throw new Exception();

        int level = Integer.parseInt(split[1]);

        return new CompressedEnchantment(enchantment, level);
    }
}
