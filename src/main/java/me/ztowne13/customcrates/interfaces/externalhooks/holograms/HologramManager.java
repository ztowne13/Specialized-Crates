package me.ztowne13.customcrates.interfaces.externalhooks.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class HologramManager<T extends Hologram> {
    private final SpecializedCrates customCrates;

    protected List<T> holograms;

    public HologramManager(SpecializedCrates customCrates) {
        this.customCrates = customCrates;
        holograms = new ArrayList<>();
    }

    public abstract T createHologram(Location location);

    public void deleteHologram(T hologram) {
        holograms.remove(hologram);
        hologram.delete();
    }

    protected SpecializedCrates getCustomCrates() {
        return customCrates;
    }

    public List<T> getHolograms() {
        return holograms;
    }
}
