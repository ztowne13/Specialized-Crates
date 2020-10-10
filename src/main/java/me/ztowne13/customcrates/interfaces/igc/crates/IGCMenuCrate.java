package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.buttons.IGCButtonType;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/31/16.
 */
public abstract class IGCMenuCrate extends IGCMenu {
    Crate crates;
    CrateSettings cs;

    public IGCMenuCrate(SpecializedCrates cc, Player p, IGCMenu lastMenu, String invName, Crate crates) {
        this(cc, p, lastMenu, invName, crates, new IGCButtonType[]{}, new int[]{});
    }

    public IGCMenuCrate(SpecializedCrates cc, Player p, IGCMenu lastMenu, String invName, Crate crates,
                        IGCButtonType[] buttonTypes, int[] buttonSpots) {
        super(cc, p, lastMenu, invName, buttonTypes, buttonSpots);
        this.crates = crates;
        this.cs = crates.getSettings();
    }

    public Crate getCrates() {
        return crates;
    }

    public CrateSettings getCs() {
        return cs;
    }
}
