package me.ztowne13.customcrates.interfaces.externalhooks.holograms.nms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class HologramManagerNMS extends HologramManager<HologramNMS> {
    public HologramManagerNMS(SpecializedCrates cc) {
        super(cc);
    }

    @Override
    public HologramNMS createHologram(Location location) {
        HologramNMS hologram = new HologramNMS(getCustomCrates(), location);
        getHolograms().add(hologram);
        return hologram;
    }
}
