package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.interfaces.items.attributes.CompressedEnchantment;
import me.ztowne13.customcrates.interfaces.items.attributes.RGBColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EditableItem {
    ItemStack getStack();

    EditableItem setStack(ItemStack stack);

    EditableItem setDisplayName(String displayName);

    String getDisplayName(boolean useMaterialWhenNull);

    String getPlayerHeadName();

    void setPlayerHeadName(String name);

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

    void setColor(RGBColor color);

    boolean isColorable();

    void addItemFlag(ItemFlag flag);

    void removeItemFlag(ItemFlag flag);

    int getDamage();

    void setDamage(int damage);

    /**
     * Clears all the currently set values and updates them based on the current itemstack.
     */
    void updateFromItem();
}
