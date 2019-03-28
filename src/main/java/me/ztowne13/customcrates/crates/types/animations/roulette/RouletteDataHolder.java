package me.ztowne13.customcrates.crates.types.animations.roulette;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class RouletteDataHolder
{
    static HashMap<Player, RouletteDataHolder> holders = new HashMap<>();

    Player p;
    Location l;
    BukkitRunnable br;

    double displayAmount, currentTicks = 1.1, individualTicks = 0, totalTicks = 0, updates = 0;

    Reward lastShownReward;

    boolean completed;

    InventoryBuilder inv;

    public RouletteDataHolder(Player p, Location l, RouletteManager ir)
    {
        this.p = p;
        this.l = l;
        displayAmount = ir.getRandomTickTime(ir.getFinalTickLength());
        currentTicks = 0;
        completed = false;
        inv = new InventoryBuilder(p, 27, ir.getCrates().getCs().getCrateInventoryName() == null ? ir.getInvName() :
                ir.getCrates().getCs().getCrateInventoryName());
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

    public double getDisplayAmount()
    {
        return displayAmount;
    }

    public void setDisplayAmount(double displayAmount)
    {
        this.displayAmount = displayAmount;
    }

    public InventoryBuilder getInv()
    {
        return inv;
    }

    public void setInv(InventoryBuilder inv)
    {
        this.inv = inv;
    }

    public double getCurrentTicks()
    {
        return currentTicks;
    }

    public void setCurrentTicks(double currentTicks)
    {
        this.currentTicks = currentTicks;
    }

    public Reward getLastShownReward()
    {
        return lastShownReward;
    }

    public void setLastShownReward(Reward lastShownReward)
    {
        this.lastShownReward = lastShownReward;
    }

    public BukkitRunnable getBr()
    {
        return br;
    }

    public void setBr(BukkitRunnable br)
    {
        this.br = br;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public static HashMap<Player, RouletteDataHolder> getHolders()
    {
        return holders;
    }

    public static void setHolders(HashMap<Player, RouletteDataHolder> holders)
    {
        RouletteDataHolder.holders = holders;
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

    public double getUpdates()
    {
        return updates;
    }

    public void setUpdates(double updates)
    {
        this.updates = updates;
    }
}
