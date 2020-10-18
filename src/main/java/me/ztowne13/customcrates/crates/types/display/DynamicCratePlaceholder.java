package me.ztowne13.customcrates.crates.types.display;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;

/**
 * Created by ztowne13 on 2/24/16.
 */
public abstract class DynamicCratePlaceholder {
    protected final SpecializedCrates instance;

    public DynamicCratePlaceholder(SpecializedCrates instance) {
        this.instance = instance;
    }

    public abstract void place(PlacedCrate placedCrate);

    public abstract void remove(PlacedCrate placedCrate);

    public abstract String getType();

    public abstract void setType(Object obj);

    public abstract void fixHologram(PlacedCrate placedCrate);

    public abstract String toString();
}
