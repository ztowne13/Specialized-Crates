package me.ztowne13.customcrates.crates.types.animations.enclosement;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ztowne13 on 7/5/16.
 */
public class EnclosementDataHolder
{
    static HashMap<Player, EnclosementDataHolder> holders = new HashMap<>();

    Player p;
    Location l;
    EnclosementAnimation ie;

    int currentTicksIn, ticks;
    boolean completed;

    InventoryBuilder ib;
    ArrayList<Reward> lastDisplayRewards = new ArrayList<>();

    public EnclosementDataHolder(Player p, Location l, EnclosementAnimation ie)
    {
        this.p = p;
        this.l = l;
        this.ie = ie;

        if (ie.getInventoryRows() > 2)
        {
            ie.setInventoryRows(2);
        }

        currentTicksIn = (((((ie.getInventoryRows() * 2) + 1) * 9) - 1) / 2) - ie.getRewardAmount();

        ib = new InventoryBuilder(p, ((ie.getInventoryRows()) * 9 * 2) + 9,
                ie.getCrates().getCs().getCrateInventoryName() == null ? ie.getInvName() :
                        ie.getCrates().getCs().getCrateInventoryName());
        ib.open();
        holders.put(p, this);
    }

    public Player getP()
    {
        return p;
    }

    public void setP(Player p)
    {
        this.p = p;
    }

    public Location getL()
    {
        return l;
    }

    public void setL(Location l)
    {
        this.l = l;
    }

    public EnclosementAnimation getIe()
    {
        return ie;
    }

    public void setIe(EnclosementAnimation ie)
    {
        this.ie = ie;
    }

    public int getCurrentTicksIn()
    {
        return currentTicksIn;
    }

    public void setCurrentTicksIn(int currentTicksIn)
    {
        this.currentTicksIn = currentTicksIn;
    }

    public int getTicks()
    {
        return ticks;
    }

    public void setTicks(int ticks)
    {
        this.ticks = ticks;
    }

    public InventoryBuilder getIb()
    {
        return ib;
    }

    public void setIb(InventoryBuilder ib)
    {
        this.ib = ib;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public static HashMap<Player, EnclosementDataHolder> getHolders()
    {
        return holders;
    }

    public static void setHolders(HashMap<Player, EnclosementDataHolder> holders)
    {
        EnclosementDataHolder.holders = holders;
    }

    public ArrayList<Reward> getLastDisplayRewards()
    {
        return lastDisplayRewards;
    }

    public void setLastDisplayRewards(ArrayList<Reward> lastDisplayRewards)
    {
        this.lastDisplayRewards = lastDisplayRewards;
    }
}
