package me.ztowne13.customcrates.crates.types.animations.inventory;


import me.ztowne13.customcrates.crates.options.rewards.Reward;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class MenuAnimationDataHolder extends InventoryAnimationDataHolder {
    private final List<Reward> displayedRewards;
    private final List<Integer> usedNumbers;

    public MenuAnimationDataHolder(Player player, Location location, MenuAnimation crateAnimation) {
        super(player, location, crateAnimation, crateAnimation.getInventoryRows() * 9);

        displayedRewards = new ArrayList<>();
        usedNumbers = new ArrayList<>();
    }

    public List<Reward> getDisplayedRewards() {
        return displayedRewards;
    }

    public List<Integer> getUsedNumbers() {
        return usedNumbers;
    }

}
