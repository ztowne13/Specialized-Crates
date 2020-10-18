package me.ztowne13.customcrates.interfaces.externalhooks.holograms.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class SainttXHologramManager extends HologramManager {
    public SainttXHologramManager(SpecializedCrates instance) {
        super(instance);
    }

    @Override
    public Hologram newHologram(Location location) {
        return new SainttXHologram(instance, location);
    }
}
