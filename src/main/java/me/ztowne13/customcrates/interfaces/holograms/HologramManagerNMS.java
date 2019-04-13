package me.ztowne13.customcrates.interfaces.holograms;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

public class HologramManagerNMS extends HologramManager
{
    public HologramManagerNMS(CustomCrates cc)
    {
        super(cc);
    }

    @Override
    public boolean isHologramEntity(ArmorStand stand)
    {
        return false;
    }

    @Override
    public Hologram createHologram(String name, Location location)
    {
        HologramNMS hologram = new HologramNMS(cc, name, location);

        getHolograms().add(hologram);

        return hologram;
    }

    @Override
    public void deleteHologram(Hologram hologram)
    {
        getHolograms().remove(hologram);
        ((HologramNMS)hologram).remove();
    }

    @Override
    CustomCrates getCc()
    {
        return null;
    }
}
