package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/31/16.
 */
public abstract class IGCMenuCrate extends IGCMenu
{
    Crate crates;
    CrateSettings cs;

    public IGCMenuCrate(CustomCrates cc, Player p, IGCMenu lastMenu, String invName, Crate crates)
    {
        super(cc, p, lastMenu, invName);
        this.crates = crates;
        this.cs = crates.getCs();
    }
}
