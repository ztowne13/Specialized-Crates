package me.ztowne13.customcrates.interfaces.igc;

import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;

/**
 * Created by ztowne13 on 3/11/16.
 */
public enum IGCDefaultItems
{
    EXIT_BUTTON(
            new ItemBuilder(DynamicMaterial.GRAY_DYE, 1).setName("&cExit").setLore("&4&oNOTE: THIS DOES NOT SAVE CHANGES")
                    .addLore("").addLore("&7Exit or return to the previous menu.")),

    SAVE_BUTTON(new ItemBuilder(DynamicMaterial.LIME_DYE, 1).setName("&aSave and reload").addLore("")
            .addLore("&7Save your current changes and reload the plugin.")),

    SAVE_ONLY_BUTTON(new ItemBuilder(DynamicMaterial.LIME_DYE, 1).setName("&aSave").setLore("&7Note: click reload to")
            .addLore("&7see the changes.")),

    RELOAD_BUTTON(new ItemBuilder(DynamicMaterial.PINK_DYE, 1).setName("&aReload").setLore("&7This reloads the plugin")
            .addLore("&4&lMAKE SURE YOU SAVE FIRST!"));

    ItemBuilder ib;

    IGCDefaultItems(ItemBuilder ib)
    {
        this.ib = ib;
    }

    public ItemBuilder getIb()
    {
        return ib;
    }

    public void setIb(ItemBuilder ib)
    {
        this.ib = ib;
    }
}
