package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimType;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimation;
import org.bukkit.Location;

import java.util.UUID;

public abstract class DynamicHologram
{
    UUID uuid;

    SpecializedCrates cc;
    PlacedCrate cm;

    HoloAnimation ha;

    boolean displayingRewardHologram = false;

    public DynamicHologram(SpecializedCrates cc, PlacedCrate cm)
    {
        this.uuid = UUID.randomUUID();

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

    public UUID getUuid()
    {
        return uuid;
    }
}
