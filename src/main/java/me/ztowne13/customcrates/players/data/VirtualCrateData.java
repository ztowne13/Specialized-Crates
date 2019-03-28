package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.crates.Crate;

/**
 * Created by ztowne13 on 7/24/16.
 */
public class VirtualCrateData
{
    Crate crate;
    int crates, keys;

    public VirtualCrateData(Crate crate, int crates, int keys)
    {
        this.crate = crate;
        this.crates = crates;
        this.keys = keys;
    }

    public String toString()
    {
        return crate.getName() + ";" + crates + ";" + keys;
    }

    public Crate getCrate()
    {
        return crate;
    }

    public void setCrate(Crate crate)
    {
        this.crate = crate;
    }

    public int getCrates()
    {
        return crates;
    }

    public void setCrates(int crates)
    {
        this.crates = crates;
    }

    public int getKeys()
    {
        return keys;
    }

    public void setKeys(int keys)
    {
        this.keys = keys;
    }
}
