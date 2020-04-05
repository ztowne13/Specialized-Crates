package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CrateItemHandler extends CSetting
{

    SaveableItemBuilder crateItem;

    public CrateItemHandler(Crate crates, SpecializedCrates cc)
    {
        super(crates, cc);

        crateItem = new SaveableItemBuilder(DynamicMaterial.RED_WOOL, 1);
        crateItem.setDisplayName("&4Please set me!");
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        boolean result = crateItem
                .loadItem(getCrate().getSettings().getFileHandler(), "crate", csb.getStatusLogger(),
                        StatusLoggerEvent.SETTINGS_CRATE_FAILURE,
                        StatusLoggerEvent.SETTINGS_CRATE_ENCHANTMENT_ADD_FAILURE,
                        StatusLoggerEvent.SETTINGS_CRATE_POTION_ADD_FAILURE, StatusLoggerEvent.SETTINGS_CRATE_GLOW_FAILURE,
                        StatusLoggerEvent.SETTINGS_CRATE_AMOUNT_FAILURE, StatusLoggerEvent.SETTINGS_CRATE_FLAG_FAILURE);
        if (!result)
        {
            StatusLoggerEvent.SETTINGS_CRATE_FAILURE_DISABLE.log(csb.getStatusLogger());
        }
    }

    @Override
    public void saveToFile()
    {
        crateItem.saveItem(getCrate().getSettings().getFileHandler(), "crate", false);
    }

    public boolean crateMatchesToStack(ItemStack stack)
    {
        ItemStack crate = getItem(1);
        if (Utils.itemHasName(stack))
        {
            return crate.getType().equals(stack.getType()) &&
                    crate.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName());
        }
        return false;
    }

    public boolean crateMatchesBlock(Material blockType)
    {
        if (blockType.equals(getItem(1).getType()))
        {
            return true;
        }

        if (blockType.name().equalsIgnoreCase("SKULL") && getItem(1).getType().name().equalsIgnoreCase("SKULL_ITEM"))
        {
            return true;
        }

        return false;
    }

    public ItemStack getItem(int amount)
    {
        ItemStack stack = crateItem.get().clone();
        stack.setAmount(amount);
        return stack;
    }

    public SaveableItemBuilder getItem()
    {
        return crateItem;
    }

    public void setItem(SaveableItemBuilder crateItem)
    {
        this.crateItem = crateItem;
    }
}
