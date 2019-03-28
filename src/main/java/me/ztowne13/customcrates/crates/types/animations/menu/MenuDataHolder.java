package me.ztowne13.customcrates.crates.types.animations.menu;


import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class MenuDataHolder
{
    static HashMap<Player, MenuDataHolder> holders = new HashMap<>();

    Player p;
    Location l;

    ArrayList<Reward> displayedRewards;
    ArrayList<Integer> usedNumbers;

    InventoryBuilder inv;

    public MenuDataHolder(Player p, Location l, MenuAnimation im)
    {
        this.p = p;
        this.l = l;

        inv = new InventoryBuilder(p, im.getInventoryRows() * 9,
                im.getCrates().getCs().getCrateInventoryName() == null ? im.getInvName() :
                        im.getCrates().getCs().getCrateInventoryName());
        ;
        displayedRewards = new ArrayList<>();
        usedNumbers = new ArrayList<>();
        holders.put(p, this);
    }

    public static HashMap<Player, MenuDataHolder> getHolders()
    {
        return holders;
    }

    public static void setHolders(HashMap<Player, MenuDataHolder> holders)
    {
        MenuDataHolder.holders = holders;
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

    public ArrayList<Reward> getDisplayedRewards()
    {
        return displayedRewards;
    }

    public void setDisplayedRewards(ArrayList<Reward> displayedRewards)
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

    public ArrayList<Integer> getUsedNumbers()
    {
        return usedNumbers;
    }

    public void setUsedNumbers(ArrayList<Integer> usedNumbers)
    {
        this.usedNumbers = usedNumbers;
    }
}
