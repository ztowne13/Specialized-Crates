package me.ztowne13.customcrates.crates.options.holograms;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

public class CMIHologram extends DynamicHologram
{
    HologramManager hologramManager;
    Location l;
    private com.Zrips.CMI.Modules.Holograms.CMIHologram cmiHologram;

    public CMIHologram(CustomCrates customCrates, PlacedCrate placedCrate)
    {
        super(customCrates, placedCrate);
        hologramManager = CMI.getInstance().getHologramManager();
    }

    @Override
    public void create(Location l)
    {
//        com.Zrips.CMI.Modules.Holograms.CMIHologram holo = new com.Zrips.CMI.Modules.Holograms.CMIHologram("TestHologram", l);
//        holo.setLines(Arrays.asList("Line 1","Line 2"));
//        CMI.getInstance().getHologramManager().addHologram(holo);
//        holo.update();

        if (!getCm().isDeleted())
        {

            l.setY(l.getY() + getCm().getCholo().getHologramOffset() - 1);
            l = LocationUtils.getLocationCentered(l);

            this.l = l;

            this.cmiHologram =
                    new com.Zrips.CMI.Modules.Holograms.CMIHologram(
                            "Crate(" + l.getWorld().getName() + "," + l.getBlockX() + "," + l.getBlockY() + "," +
                                    l.getBlockZ(), l);

            CMI.getInstance().getHologramManager().addHologram(this.cmiHologram);
            this.cmiHologram.update();
        }
    }

    @Override
    public void addLine(String line)
    {
        cmiHologram.getLines().add(line);
        cmiHologram.refresh();
    }

    @Override
    public void setLine(int lineNum, String line)
    {
        if (lineNum >= cmiHologram.getLines().size())
        {
            addLine(line);
        }
        else
        {
            cmiHologram.getLines().set(lineNum, line);
            cmiHologram.refresh();
        }
    }

    @Override
    public void delete()
    {
        hologramManager.removeHolo(cmiHologram);
        cmiHologram.disable();
        //hologramManager.handleHoloUpdates(Bukkit.getOnlinePlayers().iterator().next(), l);
    }

    @Override
    public void teleport(Location l)
    {
        l.setY(l.getY() + getCm().getCholo().getHologramOffset());
        cmiHologram.setLoc(LocationUtils.getLocationCentered(l));
        cmiHologram.refresh();
    }
}
