package me.ztowne13.customcrates.interfaces.holograms;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public abstract class HologramManager
{
    CustomCrates cc;

    List<Hologram> holograms;

    public HologramManager(CustomCrates cc)
    {
        this.cc = cc;
        holograms = new ArrayList<>();
    }

    public abstract boolean isHologramEntity(ArmorStand stand);

    public abstract Hologram createHologram(String name, Location location);

    public abstract void deleteHologram(Hologram hologram);

    abstract CustomCrates getCc();

    public List<Hologram> getHolograms()
    {
        return holograms;
    }
}
