package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.animations.holo.HoloAnimation;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.Location;

public abstract class DynamicHologram
{
    SpecializedCrates cc;
    PlacedCrate cm;

    HoloAnimation ha;

    boolean displayingRewardHologram = false;

    public DynamicHologram(SpecializedCrates cc, PlacedCrate cm)
    {
        this.cc = cc;
        this.cm = cm;

    }

    public abstract void create(Location l);

    public abstract void addLine(String line);

    public abstract void setLine(int lineNum, String line);

    public abstract void delete();

    public abstract void teleport(Location l);

    public void tick()
    {
        if (getHa() != null)
        {
                getHa().tick();
        }
    }

    public PlacedCrate getCm()
    {
        return cm;
    }

    public void setCm(PlacedCrate cm)
    {
        this.cm = cm;
    }

    public HoloAnimation getHa()
    {
        return ha;
    }

    public void setHa(HoloAnimation ha)
    {
        this.ha = ha;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public boolean getDisplayingRewardHologram()
    {
        return displayingRewardHologram;
    }

    public void setDisplayingRewardHologram(boolean displayingRewardHologram)
    {
        this.displayingRewardHologram = displayingRewardHologram;
    }
}
