package me.ztowne13.customcrates.visuals;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;

/**
 * Created by ztowne13 on 2/24/16.
 */
public abstract class DynamicCratePlaceholder
{
    SpecializedCrates cc;

    public DynamicCratePlaceholder(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public abstract void place(PlacedCrate cm);

    public abstract void remove(PlacedCrate cm);

    public abstract void setType(Object obj);

    public abstract String getType();

    public abstract void fixHologram(PlacedCrate cm);

    public abstract String toString();
}
