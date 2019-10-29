package me.ztowne13.customcrates.interfaces.igc.crates.crateanimations;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.types.animations.CrateType;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 7/6/16.
 */
public abstract class IGCAnimation extends IGCMenu
{
    FileHandler fu;
    FileConfiguration fc;
    CrateType crateType;

    public IGCAnimation(SpecializedCrates cc, Player p, IGCMenu lastMenu, String inventoryName, CrateType crateType)
    {
        super(cc, p, lastMenu, inventoryName);
        this.crateType = crateType;
        this.fu = cc.getCrateconfigFile();
        this.fc = fu.get();
    }

    public String getString(String path)
    {
        return fc.getString(getPath(path));
    }

    public String getPath(String value)
    {
        return crateType.getPrefix() + "." + value;
    }
}
