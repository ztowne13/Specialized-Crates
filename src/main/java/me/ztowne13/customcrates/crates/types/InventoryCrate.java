package me.ztowne13.customcrates.crates.types;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.UUID;

public abstract class InventoryCrate extends CrateHead
{
    protected HashMap<UUID, Double> rotateTime = new HashMap<UUID, Double>();
    protected HashMap<UUID, BukkitTask> rotateTimers = new HashMap<UUID, BukkitTask>();
    protected Inventory inv;
    protected PlacedCrate placedCrate;

    String prefix;

    public InventoryCrate(Inventory inv, Crate crates)
    {
        super(crates);
        this.inv = inv;
    }

    public InventoryCrate(Inventory inv, PlacedCrate placedCrate)
    {
        this(inv, placedCrate.getCrates());
        this.placedCrate = placedCrate;
    }

    public Inventory getInv()
    {
        return inv;
    }

    public void setInv(Inventory inv)
    {
        this.inv = inv;
    }

    public HashMap<UUID, Double> getRotateTime()
    {
        return rotateTime;
    }

    public void setRotateTime(HashMap<UUID, Double> rotateTime)
    {
        this.rotateTime = rotateTime;
    }

    public HashMap<UUID, BukkitTask> getRotateTimers()
    {
        return rotateTimers;
    }

    public void setRotateTimers(HashMap<UUID, BukkitTask> rotateTimers)
    {
        this.rotateTimers = rotateTimers;
    }


}
