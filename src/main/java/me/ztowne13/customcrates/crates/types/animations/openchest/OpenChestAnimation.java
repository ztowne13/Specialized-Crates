package me.ztowne13.customcrates.crates.types.animations.openchest;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.CrateType;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;

public class OpenChestAnimation extends CrateAnimation
{
    public static ArrayList<Item> items = new ArrayList<>();

    int openDuration;
    boolean earlyRewardHologram;
    boolean attachTo = true;
    boolean earlyEffects = false;
    long rewardHoloDelay;

    Location loc;
    HashMap<Player, Reward> rewardMap = new HashMap<>();
    HashMap<Player, PlacedCrate> placedCrateMap = new HashMap<>();

    public OpenChestAnimation(Inventory inventory, Crate crate)
    {
        super(CrateType.BLOCK_CRATEOPEN.getPrefixDotted(), crate);
    }

    @Override
    public boolean runAnimation(Player p, Location l, CrateState cs, boolean requireKeyInHand, boolean force)
    {
        this.loc = l;

        if (force || canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            if(getCrate().getSettings().getCrateType().isSpecialDynamicHandling() && !getCrate().getSettings().getObtainType().isStatic())
            {
                PlacedCrate placedCrate = PlayerManager.get(cc, p).getLastOpenedPlacedCrate();
                placedCrate.setCratesEnabled(false);

                placedCrateMap.put(p, placedCrate);
            }
            playAnimation(p, l);
            playRequiredOpenActions(p, !requireKeyInHand, force);
            return true;
        }

        playFailToOpen(p, true, !PlayerManager.get(cc, p).isInCrate());
        return false;
    }

    public void playAnimation(final Player p, final Location l)
    {
        Reward reward = getCrate().getSettings().getRewards().getRandomReward(p);
        final ArrayList<String> rewards = new ArrayList<String>();
        rewards.add(reward.getDisplayName());
        rewardMap.put(p, reward);

        Location upOne = l.clone();
        upOne.setY(upOne.getY() + 1);
        upOne.setX(upOne.getX() + .5);
        upOne.setZ(upOne.getZ() + .5);

        final Item item = l.getWorld().dropItem(upOne, reward.getDisplayBuilder().getStack());
        item.setPickupDelay(100000);
        item.setVelocity(new Vector(0, item.getVelocity().getY(), 0));
        items.add(item);

        ArrayList<Reward> rewardsStr = new ArrayList<Reward>();
        rewardsStr.add(reward);

        if(earlyEffects)
        {
            getCrate().tick(loc, CrateState.OPEN, p, rewardsStr);
        }

        if(attachTo)
        {
            crates.getSettings().getActions().playRewardHologram(p, rewards, .6, true, item, openDuration);
        }
        else if (isEarlyRewardHologram())
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
            {
                @Override
                public void run()
                {
                    crates.getSettings().getActions().playRewardHologram(p, rewards, .6);
                }
            }, rewardHoloDelay);
        }

        new NMSChestState().playChestAction(l.getBlock(), true);

        Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
        {
            @Override
            public void run()
            {
                new NMSChestState().playChestAction(l.getBlock(), false);
                items.remove(item);
                item.remove();
                endAnimation(p);
            }
        }, openDuration);
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {
        FileConfiguration fc = getFileHandler().get();

        openDuration = FileUtils.loadInt(fc.getString(prefix + "chest-open-duration"), 80, sl,
                StatusLoggerEvent.ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_CHEST_OPEN_DURATION_INVALID);

        earlyRewardHologram = FileUtils.loadBoolean(fc.getString(prefix + "early-reward-hologram"), true, sl,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_REWARD_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_REWARD_INVALID);

        rewardHoloDelay = FileUtils.loadLong(fc.getString(prefix + "reward-hologram-delay"), 0, sl,
                StatusLoggerEvent.ANIMATION_OPENCHEST_REWARD_HOLO_DELAY_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_REWARD_HOLO_DELAY_INVALID);

        attachTo = FileUtils.loadBoolean(fc.getString(prefix + "reward-holo-attach-to-item"), true, sl,
                StatusLoggerEvent.ANIMATION_OPENCHEST_ATTACH_TO_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_ATTACH_TO_INVALID);

        earlyEffects = FileUtils.loadBoolean(fc.getString(prefix + "early-open-actions"), false, sl,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_OPEN_ACTIONS_SUCCESS,
                StatusLoggerEvent.ANIMATION_OPENCHEST_EARLY_OPEN_ACTIONS_INVALID);

        if(attachTo == true)
            earlyRewardHologram = true;
    }

    @Override
    public void endAnimation(Player p)
    {
        Reward reward = rewardMap.get(p);
        rewardMap.remove(p);
        PlacedCrate placedCrate = placedCrateMap.get(p);
        placedCrateMap.remove(p);

        ArrayList<Reward> rewards = new ArrayList<Reward>();
        rewards.add(reward);

        completeCrateRun(p, rewards, false, placedCrate);
        if(!earlyEffects)
            getCrate().tick(loc, CrateState.OPEN, p, rewards);
    }

    public static void removeAllItems()
    {
        for (Item item : items)
            item.remove();
    }

    public boolean isEarlyRewardHologram()
    {
        return earlyRewardHologram;
    }

    public void setEarlyRewardHologram(boolean earlyRewardHologram)
    {
        this.earlyRewardHologram = earlyRewardHologram;
    }
}
