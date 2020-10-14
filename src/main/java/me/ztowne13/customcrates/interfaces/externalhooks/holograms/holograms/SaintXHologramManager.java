package me.ztowne13.customcrates.interfaces.externalhooks.holograms.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class SaintXHologramManager extends HologramManager {
    public SaintXHologramManager(SpecializedCrates customCrates) {
        super(customCrates);
    }

    @Override
    public Hologram newHologram(Location location) {
        return new SainttXHologram(getCustomCrates(), location);
    }
}
