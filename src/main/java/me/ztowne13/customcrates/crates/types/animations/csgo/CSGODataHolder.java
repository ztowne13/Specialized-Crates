package me.ztowne13.customcrates.crates.types.animations.csgo;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class CSGODataHolder
{
    static HashMap<Player, CSGODataHolder> holders = new HashMap<>();

    Player p;
    Location l;
    BukkitRunnable br;

    double displayAmount, currentTicks = 1.1, individualTicks = 0, totalTicks = 0, updates = 0;
    int animatedCloseTicks = 0;

    Reward[] displayedRewards = new Reward[7];

    boolean completed = false;

    InventoryBuilder inv;

    public CSGODataHolder(Player p, Location l, CSGOManager ic)
    {
        this.p = p;
        this.l = l;
        displayAmount = ic.getRandomTickTime(ic.getFinalTickLength());
        inv = new InventoryBuilder(p, 27, ic.getCrates().getCs().getCrateInventoryName() == null ? ic.getInvName() :
                ic.getCrates().getCs().getCrateInventoryName());
        holders.put(p, this);
    }

    public static HashMap<Player, CSGODataHolder> getHolders()
    {
        return holders;
    }

    public static void setHolders(HashMap<Player, CSGODataHolder> holders)
    {
        CSGODataHolder.holders = holders;
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

    public BukkitRunnable getBr()
    {
        return br;
    }

    public void setBr(BukkitRunnable br)
    {
        this.br = br;
    }

    public double getDisplayAmount()
    {
        return displayAmount;
    }

    public void setDisplayAmount(double displayAmount)
    {
        this.displayAmount = displayAmount;
    }

    public double getCurrentTicks()
    {
        return currentTicks;
    }

    public void setCurrentTicks(double currentTicks)
    {
        this.currentTicks = currentTicks;
    }

    public double getIndividualTicks()
    {
        return individualTicks;
    }

    public void setIndividualTicks(double individualTicks)
    {
        this.individualTicks = individualTicks;
    }

    public double getTotalTicks()
    {
        return totalTicks;
    }

    public void setTotalTicks(double totalTicks)
    {
        this.totalTicks = totalTicks;
    }

    public Reward[] getDisplayedRewards()
    {
        return displayedRewards;
    }

    public void setDisplayedRewards(Reward[] displayedRewards)
    {
        this.displayedRewards = displayedRewards;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public InventoryBuilder getInv()
    {
        return inv;
    }

    public void setInv(InventoryBuilder inv)
    {
        this.inv = inv;
    }

    public double getUpdates()
    {
        return updates;
    }

    public void setUpdates(double updates)
    {
        this.updates = updates;
    }

    public int getAnimatedCloseTicks()
    {
        return animatedCloseTicks;
    }

    public void setAnimatedCloseTicks(int animatedCloseTicks)
    {
        this.animatedCloseTicks = animatedCloseTicks;
    }
}
