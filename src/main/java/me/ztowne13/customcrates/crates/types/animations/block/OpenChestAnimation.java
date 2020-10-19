package me.ztowne13.customcrates.crates.types.animations.block;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.AnimationDataHolder;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OpenChestAnimation extends CrateAnimation {
    private static final List<Item> items = new ArrayList<>();
    private final HashMap<Player, Reward> rewardMap = new HashMap<>();
    private final HashMap<Player, PlacedCrate> placedCrateMap = new HashMap<>();
    private int openDuration;
    private boolean earlyRewardHologram;
    private boolean attachTo = true;
    private boolean earlyEffects = false;
    private long rewardHoloDelay;
    private Location loc;

    public OpenChestAnimation(Crate crate) {
        super(crate, CrateAnimationType.BLOCK_CRATEOPEN);
    }

    public static void removeAllItems() {
        for (Item item : items)
            item.remove();
    }

    @Override
    public void tickAnimation(AnimationDataHolder dataHolder, boolean update) {
        // EMPTY
    }

    @Override
    public void endAnimation(AnimationDataHolder dataHolder) {
        // EMPTY
    }

    @Override
    public boolean updateTicks(AnimationDataHolder dataHolder) {
        return false;
    }

    @Override
    public void checkStateChange(AnimationDataHolder dataHolder, boolean update) {
        // EMPTY
    }

    @Override
    public boolean startAnimation(Player player, Location location, boolean requireKeyInHand, boolean force) {
        this.loc = location;

        if (force || canExecuteFor(player, requireKeyInHand)) {
            if (getCrate().getSettings().getCrateType().isSpecialDynamicHandling() && !getCrate().getSettings().getObtainType().isStatic()) {
                PlacedCrate placedCrate = PlayerManager.get(instance, player).getLastOpenedPlacedCrate();
                placedCrate.setCratesEnabled(false);

                placedCrateMap.put(player, placedCrate);
            }
            playAnimation(player, location);
            playRequiredOpenActions(player, !requireKeyInHand, force);
            return true;
        }

        playFailToOpen(player, true, !PlayerManager.get(instance, player).isInCrate());
        return false;
    }

    public void playAnimation(final Player player, final Location location) {
        Reward reward = getCrate().getSettings().getReward().getRandomReward();
        final ArrayList<String> rewards = new ArrayList<>();
        rewards.add(reward.getDisplayName(true));
        rewardMap.put(player, reward);

        Location upOne = location.clone();
        upOne.setY(upOne.getY() + 1);
        upOne.setX(upOne.getX() + .5);
        upOne.setZ(upOne.getZ() + .5);

        final Item item = location.getWorld().dropItem(upOne, reward.getDisplayBuilder().getStack());
        item.setPickupDelay(100000);
        item.setVelocity(new Vector(0, item.getVelocity().getY(), 0));
        items.add(item);

        ArrayList<Reward> rewardsStr = new ArrayList<>();
        rewardsStr.add(reward);

        if (earlyEffects) {
            getCrate().tick(this.loc, CrateState.OPEN, player, rewardsStr);
        }

        if (attachTo) {
            crate.getSettings().getAction().playRewardHologram(player, rewards, .6, true, item, openDuration);
        } else if (isEarlyRewardHologram()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> crate.getSettings().getAction().playRewardHologram(player, rewards, .6), rewardHoloDelay);
        }

        new NMSChestState().playChestAction(location.getBlock(), true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> {
            new NMSChestState().playChestAction(location.getBlock(), false);
            items.remove(item);
            item.remove();
            endAnimation(player);
        }, openDuration);
    }

    @Override
    public void loadDataValues(StatusLogger statusLogger) {
        openDuration = fileHandler.getFileDataLoader().loadInt(prefix + "chest-open-duration", 80, statusLogger,
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_INVALID);

        earlyRewardHologram = fileHandler.getFileDataLoader().loadBoolean(prefix + "early-reward-hologram", true, statusLogger,
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_REWARD_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_REWARD_INVALID);

        rewardHoloDelay = fileHandler.getFileDataLoader().loadLong(prefix + "reward-hologram-delay", 0, statusLogger,
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_OPENCHEST_REWARD_HOLO_DELAY_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_REWARD_HOLO_DELAY_INVALID);

        attachTo = fileHandler.getFileDataLoader().loadBoolean(prefix + "reward-holo-attach-to-item", true, statusLogger,
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_OPENCHEST_ATTACH_TO_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_ATTACH_TO_INVALID);

        earlyEffects = fileHandler.getFileDataLoader().loadBoolean(prefix + "early-open-actions", false, statusLogger,
                StatusLoggerEvent.ANIMATION_VALUE_NONEXISTENT,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_OPEN_ACTIONS_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_OPEN_ACTIONS_INVALID);

        if (attachTo)
            earlyRewardHologram = true;
    }

    public void endAnimation(Player player) {
        Reward reward = rewardMap.get(player);
        rewardMap.remove(player);
        PlacedCrate placedCrate = placedCrateMap.get(player);
        placedCrateMap.remove(player);

        ArrayList<Reward> rewards = new ArrayList<>();
        rewards.add(reward);

        finishAnimation(player, rewards, placedCrate);
        if (!earlyEffects)
            getCrate().tick(loc, CrateState.OPEN, player, rewards);
    }

    public boolean isEarlyRewardHologram() {
        return earlyRewardHologram;
    }

    public void setEarlyRewardHologram(boolean earlyRewardHologram) {
        this.earlyRewardHologram = earlyRewardHologram;
    }
}
