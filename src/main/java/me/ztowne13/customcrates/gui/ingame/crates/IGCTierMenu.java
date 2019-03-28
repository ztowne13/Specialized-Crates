package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/2/16.
 */
public abstract class IGCTierMenu extends IGCMenuCrate
{
    String tier;

    public IGCTierMenu(CustomCrates cc, Player p, IGCMenu lastMenu, String name, Crate crates, String tier)
    {
        super(cc, p, lastMenu, name, crates);
        this.tier = tier;
    }

    public String getTier()
    {
        return tier;
    }

    public void setTier(String tier)
    {
        this.tier = tier;
    }
}
