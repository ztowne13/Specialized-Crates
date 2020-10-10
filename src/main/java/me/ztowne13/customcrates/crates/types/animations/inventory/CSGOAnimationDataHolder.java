package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/6/16.
 */
public class CSGOAnimationDataHolder extends InventoryAnimationDataHolder {
    double currentTicks = 1.1;
    double updates = 0;
    int animatedCloseTicks = 0;
    boolean updateAnimatedClose = false;

    Reward[] displayedRewards = new Reward[7];

    public CSGOAnimationDataHolder(Player player, Location location, CSGOAnimation crateAnimation) {
        super(player, location, crateAnimation, 27);
    }

    public double getCurrentTicks() {
        return currentTicks;
    }

    public void setCurrentTicks(double currentTicks) {
        this.currentTicks = currentTicks;
    }

    public double getUpdates() {
        return updates;
    }

    public void setUpdates(double updates) {
        this.updates = updates;
    }

    public int getAnimatedCloseTicks() {
        return animatedCloseTicks;
    }

    public void setAnimatedCloseTicks(int animatedCloseTicks) {
        this.animatedCloseTicks = animatedCloseTicks;
    }

    public Reward[] getDisplayedRewards() {
        return displayedRewards;
    }

    public boolean isUpdateAnimatedClose() {
        return updateAnimatedClose;
    }

    public void setUpdateAnimatedClose(boolean updateAnimatedClose) {
        this.updateAnimatedClose = updateAnimatedClose;
    }
}
