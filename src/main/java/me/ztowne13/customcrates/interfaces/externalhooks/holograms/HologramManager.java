package me.ztowne13.customcrates.interfaces.externalhooks.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public abstract class HologramManager {
    SpecializedCrates cc;

    List<Hologram> holograms;

    public HologramManager(SpecializedCrates cc) {
        this.cc = cc;
        holograms = new ArrayList<>();
    }

    public abstract boolean isHologramEntity(ArmorStand stand);

    public abstract Hologram createHologram(String name, Location location);

    public abstract void deleteHologram(Hologram hologram);

    abstract SpecializedCrates getCc();

    public List<Hologram> getHolograms() {
        return holograms;
    }
}
