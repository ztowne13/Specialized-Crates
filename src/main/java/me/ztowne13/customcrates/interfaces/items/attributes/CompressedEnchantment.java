package me.ztowne13.customcrates.interfaces.items.attributes;

import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class CompressedEnchantment {
    private final Enchantment enchantment;
    private final int level;

    public CompressedEnchantment(Enchantment enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public static CompressedEnchantment fromString(String value) throws Exception {
        String[] split = value.split(";");

        Enchantment enchantment = Enchantment.getByName(split[0].toUpperCase());
        if (enchantment == null)
            throw new Exception();

        int level = Integer.parseInt(split[1]);

        return new CompressedEnchantment(enchantment, level);
    }

    public void applyTo(ItemBuilder itemBuilder) {
        if (itemBuilder.getItemMeta() instanceof EnchantmentStorageMeta) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) itemBuilder.getItemMeta();
            meta.addStoredEnchant(enchantment, level, true);

            itemBuilder.setItemMeta(meta);
            return;
        }

        itemBuilder.getStack().addUnsafeEnchantment(enchantment, level);
    }

    @Override
    public String toString() {
        return enchantment.getName() + ";" + level;
    }

    public Enchantment getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }
}
