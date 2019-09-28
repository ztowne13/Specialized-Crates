package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.animations.holo.HoloAnimType;
import me.ztowne13.customcrates.animations.holo.HoloAnimation;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.Location;

public abstract class DynamicHologram
{
    CustomCrates cc;
    PlacedCrate cm;

    HoloAnimation ha;

    boolean displayingRewardHologram = false;

    public DynamicHologram(CustomCrates cc, PlacedCrate cm)
    {
        this.cc = cc;
        this.cm = cm;

        if (cm.getCholo().getHat() != null && cm.getCholo().getHat() != HoloAnimType.NONE)
        {
            setHa(cm.getCholo().getHat().getAsHoloAnimation(cc, this));
        }
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
            if(!cm.getCholo().getPrefixes().isEmpty())
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

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
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
