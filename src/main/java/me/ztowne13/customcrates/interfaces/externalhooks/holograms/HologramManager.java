package me.ztowne13.customcrates.interfaces.externalhooks.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class HologramManager {
    private final SpecializedCrates customCrates;

    protected List<Hologram> holograms;

    public HologramManager(SpecializedCrates customCrates) {
        this.customCrates = customCrates;
        holograms = new ArrayList<>();
    }

    public Hologram createHologram(Location location) {
        Hologram hologram = newHologram(location);
        holograms.add(hologram);
        return hologram;
    }

    public abstract Hologram newHologram(Location location);

    public void deleteHologram(Hologram hologram) {
        hologram.delete();
        holograms.remove(hologram);
    }

    protected SpecializedCrates getCustomCrates() {
        return customCrates;
    }

    public List<Hologram> getHolograms() {
        return holograms;
    }
}
