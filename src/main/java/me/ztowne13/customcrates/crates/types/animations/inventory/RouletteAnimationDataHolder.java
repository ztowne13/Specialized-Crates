package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class RouletteAnimationDataHolder extends InventoryAnimationDataHolder {
    double displayAmount;
    double currentTicks = 1.1;
    double updates = 0;

    Reward lastShownReward;

    public RouletteAnimationDataHolder(Player player, Location location, RouletteAnimation crateAnimation) {
        super(player, location, crateAnimation, 27);

        displayAmount = crateAnimation.getRandomTickTime(crateAnimation.getFinalTickLength());
    }

    public double getDisplayAmount() {
        return displayAmount;
    }

    public double getCurrentTicks() {
        return currentTicks;
    }

    public void setCurrentTicks(double currentTicks) {
        this.currentTicks = currentTicks;
    }

    public Reward getLastShownReward() {
        return lastShownReward;
    }

    public void setLastShownReward(Reward lastShownReward) {
        this.lastShownReward = lastShownReward;
    }

    public double getUpdates() {
        return updates;
    }

    public void setUpdates(double updates) {
        this.updates = updates;
    }
}
