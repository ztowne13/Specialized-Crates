package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.inventory.ItemStack;

public class CrateUtils {
    private CrateUtils() {
        // EMPTY
    }

    public static Crate searchByKey(ItemStack stack) {
        for (Crate crate : Crate.getLoadedCrates().values()) {
            if (isCrateUsable(crate) && crate.getSettings().getKeyItemHandler().keyMatchesToStack(stack, false)) {
                return crate;
            }
        }
        return null;
    }

    public static Crate searchByCrate(ItemStack stack) {
        return searchByCrate(stack, false);
    }

    public static Crate searchByCrate(ItemStack stack, boolean includeDisabled) {
        for (Crate crate : Crate.getLoadedCrates().values()) {
            if ((isCrateUsable(crate) || includeDisabled) && crate.getSettings().getCrateItemHandler().crateMatchesToStack(stack)) {
                return crate;
            }
        }
        return null;
    }

    public static boolean isCrateUsable(Crate crate) {
        return crate != null && crate.isEnabled() && !crate.isDisabledByError();
    }

    public static boolean isCrateUsable(PlacedCrate placedCrate) {
        return placedCrate.isCratesEnabled() && placedCrate.getCrate() != null && placedCrate.getCrate().isEnabled() && !placedCrate.getCrate().isDisabledByError();
    }
}