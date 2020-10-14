package me.ztowne13.customcrates.interfaces.externalhooks.holograms.nms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class NMSHologramManager extends HologramManager {
    public NMSHologramManager(SpecializedCrates cc) {
        super(cc);
    }

    @Override
    public Hologram newHologram(Location location) {
        return new NMSHologram(getCustomCrates(), location);
    }
}
