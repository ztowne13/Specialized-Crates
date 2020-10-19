package me.ztowne13.customcrates.crates.types.display;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import org.bukkit.Material;

/**
 * Created by ztowne13 on 2/24/16.
 */
public class MaterialPlaceholder extends DynamicCratePlaceholder {
    public MaterialPlaceholder(SpecializedCrates cc) {
        super(cc);
    }

    public void place(PlacedCrate placedCrate) {
        Material m = placedCrate.getCrate().getSettings().getCrateItemHandler().getItem(1).getType();
        if (placedCrate.getCrate().isEnabled()
                && !placedCrate.getCrate().getSettings().getCrateItemHandler().crateMatchesBlock(placedCrate.getLocation().getBlock().getType())) {
            try {
                if (SettingsValue.KEEP_CRATE_BLOCK_CONSISTENT.getValue(instance).equals(Boolean.TRUE) ||
                        placedCrate.getLocation().getBlock().getType().equals(Material.AIR)) {
                    placedCrate.getLocation().getBlock().setType(m);
                }
            } catch (Exception exc) {
                StatusLoggerEvent.SETTINGS_CRATE_FAILURE_DISABLE.log(placedCrate.getCrate().getSettings().getStatusLogger(),
                        m.toString() + " is not a block and therefore cannot be used as a crate type!");
                placedCrate.getCrate().setDisabledByError(false);
                placedCrate.setCratesEnabled(false);
            }
        }
    }

    public void remove(PlacedCrate placedCrate) {
        placedCrate.getLocation().getBlock().setType(Material.AIR);
    }

    public String getType() {
        return "";
    }

    public void setType(Object obj) {
        // EMPTY
    }

    public void fixHologram(PlacedCrate placedCrate) {
        // EMPTY
    }

    public String toString() {
        return "Block";
    }

}
