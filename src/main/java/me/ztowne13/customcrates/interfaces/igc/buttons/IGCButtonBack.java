package me.ztowne13.customcrates.interfaces.igc.buttons;

import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;

public class IGCButtonBack implements IGCButton
{
    @Override
    public ItemBuilder getButtonItem()
    {
        ItemBuilder button = new ItemBuilder(DynamicMaterial.GRAY_DYE, 1);
        button.setDisplayName("&cExit");
        button.addLore("&4&oNOTE: THIS DOES NOT SAVE CHANGES");
        button.addLore("");
        button.addLore("&7Exit or return to the previous menu.");
        return button;
    }

    @Override
    public boolean handleClick(IGCMenu menu)
    {
        if (menu.getLastMenu() == null)
        {
            menu.getP().closeInventory();
            return false;
        }
        menu.up();
        return false;
    }

    @Override
    public Object getValue()
    {
        return null;
    }
}
