package me.ztowne13.customcrates.crates.options.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class CMIHologram extends DynamicHologram
{
    HologramManager hologramManager;
    Location l;
    com.Zrips.CMI.Modules.Holograms.CMIHologram cmiHologram;

    public CMIHologram(CustomCrates customCrates, PlacedCrate placedCrate)
    {
        super(customCrates, placedCrate);
        hologramManager = CMI.getInstance().getHologramManager();
    }

    @Override
    public void create(Location l)
    {
        l.setY(l.getY() + getCm().getCholo().getHologramOffset() - 1);
        l = LocationUtils.getLocationCentered(l);
        this.l = l;
        cmiHologram =
                new com.Zrips.CMI.Modules.Holograms.CMIHologram(
                        ".SpecializedCrate::" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," +
                                l.getBlockZ(), l);

        cmiHologram.setUpdateIntervalSec(.1);
        hologramManager.addHologram(cmiHologram);
        update(true);
    }

    @Override
    public void addLine(String line)
    {
        System.out.println("adding: " + line);
        List<String> newList = cmiHologram.getLines();

        remove();

        cmiHologram = new com.Zrips.CMI.Modules.Holograms.CMIHologram(cmiHologram.getName(), l);
        newList.add(line);
        cmiHologram.setLines(newList);

        hologramManager.addHologram(cmiHologram);
        update(true);
        //cmiHologram.setUpdateIntervalSec(.001);
        //hologramManager.save();
    }

    @Override
    public void setLine(int lineNum, String line)
    {
        List<String> newList = cmiHologram.getLines();
        if (lineNum >= newList.size())
        {
            addLine(line);
        }
        else
        {
            remove();

            cmiHologram = new com.Zrips.CMI.Modules.Holograms.CMIHologram(cmiHologram.getName(), l);
            newList.set(lineNum, line);
            cmiHologram.setLines(newList);

            hologramManager.addHologram(cmiHologram);
            update(true);
            //hologramManager.save();
        }
    }

    public void remove()
    {
        hologramManager.removeHolo(cmiHologram);
        update(false);
    }

    @Override
    public void delete()
    {
        remove();
        hologramManager.save();
    }

    @Override
    public void teleport(Location l)
    {
//        delete();
//        this.l = l;
//        l.setY(l.getY() + getCm().getCholo().getHologramOffset());
//
//        cmiHologram = new com.Zrips.CMI.Modules.Holograms.CMIHologram(cmiHologram.getName(), LocationUtils.getLocationCentered(l));
//
//        hologramManager.addHologram(cmiHologram);
//        hologramManager.save();
    }

    public void update(boolean add)
    {

        for (Player p : Bukkit.getOnlinePlayers())
        {
            Location holoL = cmiHologram.getLoc();
            if(holoL.distance(p.getLocation()) <= 30)
            {
                if (add)
                {
                    cmiHologram.addLastHoloInRange(p.getUniqueId());
                    cmiHologram.addLastHoloInRangeExtra(p.getUniqueId());
                    cmiHologram.update(p);
                }
                else
                {
                    cmiHologram.removeLastHoloInRange(p.getUniqueId());
                    cmiHologram.removeLastHoloInRangeExtra(p.getUniqueId());
                }
            }
        }
    }
}
