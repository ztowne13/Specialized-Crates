package me.ztowne13.customcrates.visuals;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import org.bukkit.Material;

/**
 * Created by ztowne13 on 2/24/16.
 */
public class MaterialPlaceholder extends DynamicCratePlaceholder
{
    public MaterialPlaceholder(CustomCrates cc)
    {
        super(cc);
    }

    public void place(PlacedCrate cm)
    {
        Material m = cm.getCrates().getCs().getCrate(1).getType();
        if (cm.getCrates().isEnabled())
        {
            if (!cm.getL().getBlock().getType().equals(cm.getCrates().getCs().getCrate(1).getType()))
            {
                try
                {
                    cm.getL().getBlock().setType(m);
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.SETTINGS_CRATE_FAILURE_DISABLE.log(cm.getCrates().getCs().getSl(),
                            new String[]{m.toString() + " is not a block and therefore cannot be used as a crate type!"});
                    cm.getCrates().setEnabled(false);
                    cm.setCratesEnabled(false);
                }
            }
        }
    }

    public void remove(PlacedCrate cm)
    {
        cm.getL().getBlock().setType(Material.AIR);
    }

    public void setType(Object obj)
    {

    }

    public String getType()
    {
        return "";
    }


    public void fixHologram(PlacedCrate cm)
    {

    }

    public String toString()
    {
        return "Block";
    }

}
