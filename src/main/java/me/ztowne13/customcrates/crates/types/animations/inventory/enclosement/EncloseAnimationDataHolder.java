package me.ztowne13.customcrates.crates.types.animations.inventory.enclosement;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.inventory.InventoryAnimationDataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 7/5/16.
 */
public class EncloseAnimationDataHolder extends InventoryAnimationDataHolder
{
    int currentTicksIn;

    ArrayList<Reward> lastDisplayRewards = new ArrayList<>();

    public EncloseAnimationDataHolder(Player player, Location location, EncloseAnimation crateAnimation)
    {
        super(player, location, crateAnimation, ((crateAnimation.getInventoryRows()) * 9 * 2) + 9);

        currentTicksIn = (((((crateAnimation.getInventoryRows() * 2) + 1) * 9) - 1) / 2) - crateAnimation.getRewardAmount();
    }

    public int getCurrentTicksIn()
    {
        return currentTicksIn;
    }

    public void setCurrentTicksIn(int currentTicksIn)
    {
        this.currentTicksIn = currentTicksIn;
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
