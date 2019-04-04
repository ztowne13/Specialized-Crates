package me.ztowne13.customcrates.crates.options.holograms;

import com.Zrips.CMI.Modules.Holograms.HologramManager;
import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.Location;

import java.util.List;

public class CMIHologram extends DynamicHologram
{
    HologramManager hologramManager;
    com.Zrips.CMI.Modules.Holograms.CMIHologram cmiHologram;

    public CMIHologram(CustomCrates customCrates, PlacedCrate placedCrate)
    {
        super(customCrates, placedCrate);
        hologramManager = com.Zrips.CMI.CMI.getInstance().getHologramManager();
    }

    @Override
    public void create(Location l)
    {
        cmiHologram = new com.Zrips.CMI.Modules.Holograms.CMIHologram("test", l);
        hologramManager.addHologram(cmiHologram);
    }

    @Override
    public void addLine(String line)
    {
        List<String> newList = cmiHologram.getLinesAsList();
        newList.add(line);
        cmiHologram.setLines(newList);
        cmiHologram.update();
    }

    @Override
    public void setLine(int lineNum, String line)
    {
        List<String> newList = cmiHologram.getLinesAsList();
        newList.set(lineNum + 1, line);
        cmiHologram.setLines(newList);
        cmiHologram.update();
    }

    @Override
    public void delete()
    {
        hologramManager.removeHolo(cmiHologram);
    }

    @Override
    public void teleport(Location l)
    {
        cmiHologram.setLoc(l);
        cmiHologram.update();
    }
}
