package me.ztowne13.customcrates.crates.types.animations.inventory.menu;


import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.inventory.InventoryAnimationDataHolder;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class MenuAnimationDataHolder extends InventoryAnimationDataHolder
{
    ArrayList<Reward> displayedRewards;
    ArrayList<Integer> usedNumbers;

    public MenuAnimationDataHolder(Player player, Location location, MenuAnimation crateAnimation)
    {
        super(player, location, crateAnimation, crateAnimation.getInventoryRows() * 9);

        displayedRewards = new ArrayList<>();
        usedNumbers = new ArrayList<>();
    }

    public ArrayList<Reward> getDisplayedRewards()
    {
        return displayedRewards;
    }

    public ArrayList<Integer> getUsedNumbers()
    {
        return usedNumbers;
    }

}
