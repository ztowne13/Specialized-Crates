package me.ztowne13.customcrates.interfaces.externalhooks.holograms.holographicdisplays;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class HDHologramManager extends HologramManager {
    public HDHologramManager(SpecializedCrates customCrates) {
        super(customCrates);
    }

    @Override
    public Hologram createHologram(Location location) {
        HDHologram hdHologram = new HDHologram(getCustomCrates(), location);
        holograms.add(hdHologram);
        return hdHologram;
    }
}
