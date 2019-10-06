package me.ztowne13.customcrates.crates.options.holograms;

import com.micrlink.holo.HologramManager;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

import java.util.UUID;

@Deprecated
public class IndividualHologramsHologram extends DynamicHologram
{
    UUID uuid;
    double defaultYOffSet = -1.4D;

    public IndividualHologramsHologram(SpecializedCrates cc, PlacedCrate cm)
    {
        super(cc, cm);
    }

    public void create(Location l)
    {
        setUuid(UUID.randomUUID());
        HologramManager.createNewHologram(getUuid().toString(), l, "LINE 1 - .");
        teleport(l);
    }

    public void addLine(String line)
    {
        if (HologramManager.getHologram(getUuid().toString()).getLine(0).equalsIgnoreCase("LINE 1 - ."))
        {
            HologramManager.getHologram(getUuid().toString()).setLine(0, line);
        }
        else
        {
            HologramManager.addLine(getUuid().toString(), line);
        }
    }

    public void delete()
    {
        if (uuid != null)
        {
            HologramManager.removeHologram(getUuid().toString());
        }
    }

    public void teleport(Location l)
    {
        HologramManager.moveHologram(getUuid().toString(), LocationUtils.getLocationCentered(l));
    }

    public void setLine(int lineNum, String line)
    {
        HologramManager.getHologram(getUuid().toString()).setLine(lineNum, line);
    }

    public UUID getUuid()
    {
        return this.uuid;
    }

    public void setUuid(UUID uuid)
    {
        this.uuid = uuid;
    }

    public double getDefaultYOffSet()
    {
        return this.defaultYOffSet;
    }

    public void setDefaultYOffSet(double defaultYOffSet)
    {
        this.defaultYOffSet = defaultYOffSet;
    }
}
