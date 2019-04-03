package me.ztowne13.customcrates.interfaces.items;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EditableItem
{
    ItemStack getStack();

    void setStack(ItemStack stack);

    void setDisplayName(String displayName);

    String getDisplayName();

    void setPlayerHeadName(String name);

    String getPlayerHeadName();


    // Handled outside of the ItemStack

    boolean isGlowing();

    void setGlowing(boolean glow);

    List<String> getNBTTags();

    void reapplyNBTTags();

    List<CompressedEnchantment> getEnchantments();

    void reapplyEnchantments();

    List<CompressedPotionEffect> getPotionEffects();

    void reapplyPotionEffects();

    List<String> getLore();

    void reapplyLore();
}
