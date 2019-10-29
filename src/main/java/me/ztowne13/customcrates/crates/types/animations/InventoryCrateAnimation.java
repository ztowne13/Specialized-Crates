package me.ztowne13.customcrates.crates.types.animations;

import me.ztowne13.customcrates.crates.Crate;
import org.bukkit.inventory.Inventory;

public abstract class InventoryCrateAnimation extends CrateAnimation
{
    protected Inventory inv;

    public InventoryCrateAnimation(String prefix, Crate crate, Inventory inv)
    {
        super(prefix, crate);
        this.inv = inv;
    }

    public Inventory getInv()
    {
        return inv;
    }

    public void setInv(Inventory inv)
    {
        this.inv = inv;
    }
}
