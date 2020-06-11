package me.ztowne13.customcrates.interfaces.igc.buttons;

import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;

public interface IGCButton
{
     ItemBuilder getButtonItem();

    /**
     * Handle the click of a button
     * @return If true, reopen the menu, and false if not to reopen
     */
    boolean handleClick(IGCMenu menu);

    Object getValue();
}
