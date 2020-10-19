package me.ztowne13.customcrates.interfaces.externalhooks.holograms.cmi;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class ZripsHologramManager extends HologramManager {
    public ZripsHologramManager(SpecializedCrates instance) {
        super(instance);
    }

    @Override
    public Hologram newHologram(Location location) {
        return new ZripsHologram(instance, location);
    }
}
