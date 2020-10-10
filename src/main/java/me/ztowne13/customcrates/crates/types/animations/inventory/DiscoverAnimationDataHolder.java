package me.ztowne13.customcrates.crates.types.animations.inventory;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class DiscoverAnimationDataHolder extends InventoryAnimationDataHolder {
    int remainingClicks;
    int currentSequence = 1;
    int sequence2Ticks;

    ArrayList<Integer> alreadyChosenSlots = new ArrayList<>();
    HashMap<Integer, Reward> alreadyDisplayedRewards = new HashMap<>();
    ArrayList<Integer> shufflingTiles = new ArrayList<>();

    public DiscoverAnimationDataHolder(Player player, Location location, DiscoverAnimation crateAnimation) {
        super(player, location, crateAnimation, crateAnimation.getInvRows() * 9);

        int difference = crateAnimation.getMaxRewards() - crateAnimation.getMinRewards();
        remainingClicks = (difference == 0 ? 0 : new Random().nextInt(difference + 1)) + crateAnimation.getMinRewards();
    }

    public int getRemainingClicks() {
        return remainingClicks;
    }

    public void setRemainingClicks(int remainingClicks) {
        this.remainingClicks = remainingClicks;
    }

    public int getCurrentSequence() {
        return currentSequence;
    }

    public void setCurrentSequence(int currentSequence) {
        this.currentSequence = currentSequence;
    }

    public List<Integer> getAlreadyChosenSlots() {
        return alreadyChosenSlots;
    }

    public int getShuffleTicks() {
        return sequence2Ticks;
    }

    public void setShuffleTicks(int sequence2Ticks) {
        this.sequence2Ticks = sequence2Ticks;
    }

    public Map<Integer, Reward> getAlreadyDisplayedRewards() {
        return alreadyDisplayedRewards;
    }

    public List<Integer> getShufflingTiles() {
        return shufflingTiles;
    }
}
