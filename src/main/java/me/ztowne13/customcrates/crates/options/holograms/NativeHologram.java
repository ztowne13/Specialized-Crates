package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.holograms.HologramAS;
import me.ztowne13.customcrates.interfaces.holograms.HologramManager;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

public class NativeHologram extends DynamicHologram
{
    HologramManager hologramManager;
    HologramAS hologram;

    public NativeHologram(CustomCrates cc, PlacedCrate placedCrate)
    {
        super(cc, placedCrate);
        this.hologramManager = getCc().getHologramManager();
    }

    @Override
    public void create(Location l)
    {
        hologram = hologramManager.createHologram("test", l);
        teleport(l);
    }

    @Override
    public void addLine(String line)
    {
        hologram.addLine(line);
    }

    @Override
    public void setLine(int lineNum, String line)
    {
        hologram.setLine(lineNum, line);
    }

    @Override
    public void delete()
    {
        hologramManager.deleteHologram(hologram);
    }

    @Override
    public void teleport(Location l)
    {
        hologram.setLocation(LocationUtils.getLocationCentered(l.clone()));
    }
}
