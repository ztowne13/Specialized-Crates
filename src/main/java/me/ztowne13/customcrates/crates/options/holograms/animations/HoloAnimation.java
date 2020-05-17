package me.ztowne13.customcrates.crates.options.holograms.animations;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.CHolograms;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;

/**
 * Hologram, Animation subtype
 */
public abstract class HoloAnimation extends Animation
{
    protected DynamicHologram dh;
    protected CHolograms ch;
    int intTicks = 0;

    public HoloAnimation(SpecializedCrates cc, DynamicHologram dh)
    {
        super(cc);
        this.dh = dh;
        this.ch = dh.getCm().getHologram();
    }

    public int getIntTicks()
    {
        return intTicks;
    }

    public void setIntTicks(int intTicks)
    {
        this.intTicks = intTicks;
    }

    public DynamicHologram getDh()
    {
        return dh;
    }

    public void setDh(DynamicHologram dh)
    {
        this.dh = dh;
    }

    public CHolograms getCh()
    {
        return ch;
    }

    public void setCh(CHolograms ch)
    {
        this.ch = ch;
    }


}
