package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.interfaces.items.attributes.CompressedEnchantment;
import me.ztowne13.customcrates.interfaces.items.attributes.RGBColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EditableItem
{
    ItemStack getStack();

    void setStack(ItemStack stack);

    void setDisplayName(String displayName);

    String getDisplayName(boolean useMaterialWhenNull);

    void setPlayerHeadName(String name);

    String getPlayerHeadName();

    /**
     * These functions are handled outside of the ItemStack
     **/

    void reapplyColor();

    void reapplyNBTTags();

    void reapplyPotionEffects();

    void reapplyEnchantments();

    void reapplyItemFlags();

    void reapplyLore();

    boolean isGlowing();

    void setGlowing(boolean glow);

    List<String> getNBTTags();

    List<CompressedEnchantment> getEnchantments();

    List<CompressedPotionEffect> getPotionEffects();

    List<String> getLore();

    List<ItemFlag> getItemFlags();

    RGBColor getColor();

    boolean isColorable();

    void setColor(RGBColor color);

    void addItemFlag(ItemFlag flag);

    void removeItemFlag(ItemFlag flag);

    void setDamage(int damage);

    int getDamage();

    /**
     * Clears all the currently set values and updates them based on the current itemstack.
     */
    void updateFromItem();
}
