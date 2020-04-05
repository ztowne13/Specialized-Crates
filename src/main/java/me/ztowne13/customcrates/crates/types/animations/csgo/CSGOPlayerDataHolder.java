package me.ztowne13.customcrates.crates.types.animations.csgo;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class CSGOPlayerDataHolder
{
    public enum State
    {
        SCROLLING,

        WAITING,

        CLOSING,

        ENDING,

        COMPLETED;
    }

    static HashMap<Player, CSGOPlayerDataHolder> holders = new HashMap<>();

    Player p;
    Location l;
    BukkitRunnable br;

    double currentTicks = 1.1, individualTicks = 0, totalTicks = 0, updates = 0;
    int animatedCloseTicks = 0;
    int waitingTicks = 0;

    Reward[] displayedRewards = new Reward[7];

    State currentState = State.SCROLLING;

    InventoryBuilder inv;

    public CSGOPlayerDataHolder(Player p, Location l, CSGOAnimation ic)
    {
        this.p = p;
        this.l = l;
        inv = new InventoryBuilder(p, 27, ic.getCrate().getSettings().getCrateInventoryName() == null ? ic.getInvName() :
                ic.getCrate().getSettings().getCrateInventoryName());
        holders.put(p, this);
    }

    public static HashMap<Player, CSGOPlayerDataHolder> getHolders()
    {
        return holders;
    }

    public static void setHolders(HashMap<Player, CSGOPlayerDataHolder> holders)
    {
        CSGOPlayerDataHolder.holders = holders;
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

    public State getCurrentState()
    {
        return currentState;
    }

    public void setCurrentState(State currentState)
    {
        this.currentState = currentState;
    }

    public int getWaitingTicks()
    {
        return waitingTicks;
    }

    public void setWaitingTicks(int waitingTicks)
    {
        this.waitingTicks = waitingTicks;
    }
}
