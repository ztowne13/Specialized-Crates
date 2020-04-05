package me.ztowne13.customcrates.crates.types.display;

import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.Material;

/**
 * Created by ztowne13 on 2/24/16.
 */
public class MaterialPlaceholder extends DynamicCratePlaceholder
{
    public MaterialPlaceholder(SpecializedCrates cc)
    {
        super(cc);
    }

    public void place(PlacedCrate cm)
    {
        Material m = cm.getCrates().getSettings().getCrateItemHandler().getItem(1).getType();
        if (cm.getCrates().isEnabled())
        {
            DynamicMaterial crateMat = DynamicMaterial.fromItemStack(cm.getCrates().getSettings().getCrateItemHandler().getItem(1));
            DynamicMaterial blockMat = DynamicMaterial.fromString(cm.getL().getBlock().getType().toString());

            if(!cm.getCrates().getSettings().getCrateItemHandler().crateMatchesBlock(cm.getL().getBlock().getType()))
            {
                try
                {
                    if (((boolean) SettingsValues.KEEP_CRATE_BLOCK_CONSISTENT.getValue(cc)) == true ||
                            cm.getL().getBlock().getType().equals(Material.AIR))
                    {
                        cm.getL().getBlock().setType(m);
                    }
                }
                catch (Exception exc)
                {
                    StatusLoggerEvent.SETTINGS_CRATE_FAILURE_DISABLE.log(cm.getCrates().getSettings().getStatusLogger(),
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
