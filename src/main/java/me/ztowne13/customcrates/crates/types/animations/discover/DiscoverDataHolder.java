package me.ztowne13.customcrates.crates.types.animations.discover;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class DiscoverDataHolder
{
    static HashMap<Player, DiscoverDataHolder> holders = new HashMap<>();

    Player p;
    Location l;
    DiscoverAnimation id;

    int remainingClicks, currentSequence = 1, sequence2Ticks;
    boolean completed = false, canCloseInventory = false;

    ArrayList<Integer> alreadyChosenSlots = new ArrayList<>();
    HashMap<Integer, Reward> alreadyDisplayedRewards = new HashMap<>();

    InventoryBuilder ib;

    public DiscoverDataHolder(Player p, Location l, DiscoverAnimation id)
    {
        this.p = p;
        this.l = l;
        this.id = id;

        int difference = id.getMaxRewards() - id.getMinRewards();
        ib = new InventoryBuilder(p, id.getInvRows() * 9,
                id.getCrates().getCs().getCrateInventoryName() == null ? id.getInvName() :
                        id.getCrates().getCs().getCrateInventoryName());

        remainingClicks = (difference == 0 ? 0 : new Random().nextInt(difference + 1)) + id.getMinRewards();

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

    public DiscoverAnimation getId()
    {
        return id;
    }

    public void setId(DiscoverAnimation id)
    {
        this.id = id;
    }

    public int getRemainingClicks()
    {
        return remainingClicks;
    }

    public void setRemainingClicks(int remainingClicks)
    {
        this.remainingClicks = remainingClicks;
    }

    public InventoryBuilder getIb()
    {
        return ib;
    }

    public void setIb(InventoryBuilder ib)
    {
        this.ib = ib;
    }

    public int getCurrentSequence()
    {
        return currentSequence;
    }

    public void setCurrentSequence(int currentSequence)
    {
        this.currentSequence = currentSequence;
    }

    public ArrayList<Integer> getAlreadyChosenSlots()
    {
        return alreadyChosenSlots;
    }

    public void setAlreadyChosenSlots(ArrayList<Integer> alreadyChosenSlots)
    {
        this.alreadyChosenSlots = alreadyChosenSlots;
    }

    public static HashMap<Player, DiscoverDataHolder> getHolders()
    {
        return holders;
    }

    public static void setHolders(HashMap<Player, DiscoverDataHolder> holders)
    {
        DiscoverDataHolder.holders = holders;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public void setCompleted(boolean completed)
    {
        this.completed = completed;
    }

    public int getSequence2Ticks()
    {
        return sequence2Ticks;
    }

    public void setSequence2Ticks(int sequence2Ticks)
    {
        this.sequence2Ticks = sequence2Ticks;
    }

    public HashMap<Integer, Reward> getAlreadyDisplayedRewards()
    {
        return alreadyDisplayedRewards;
    }

    public void setAlreadyDisplayedRewards(HashMap<Integer, Reward> alreadyDisplayedRewards)
    {
        this.alreadyDisplayedRewards = alreadyDisplayedRewards;
    }

    public boolean isCanCloseInventory()
    {
        return canCloseInventory;
    }

    public void setCanCloseInventory(boolean canCloseInventory)
    {
        this.canCloseInventory = canCloseInventory;
    }
}
