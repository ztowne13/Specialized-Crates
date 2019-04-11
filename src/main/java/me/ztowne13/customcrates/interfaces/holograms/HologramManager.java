package me.ztowne13.customcrates.interfaces.holograms;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import java.util.ArrayList;
import java.util.List;

public class HologramManager
{
    CustomCrates cc;

    List<HologramAS> holograms;

    public HologramManager(CustomCrates cc)
    {
        this.cc = cc;
        holograms = new ArrayList<>();
    }

    public boolean isHologramEntity(ArmorStand stand)
    {
        for(HologramAS hologram : getHolograms())
        {
            if(hologram.getStands().contains(stand))
                return true;
        }

        return false;
    }

    public HologramAS createHologram(String name, Location location)
    {
        HologramAS newHolo = new HologramAS(getCc(), name, location);
        getHolograms().add(newHolo);

        return newHolo;
    }

    public void deleteHologram(HologramAS hologram)
    {
        hologram.deleteStands();
        getHolograms().remove(hologram);
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public List<HologramAS> getHolograms()
    {
        return holograms;
    }
}
