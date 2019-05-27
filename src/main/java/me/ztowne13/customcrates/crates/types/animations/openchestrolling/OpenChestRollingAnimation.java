package me.ztowne13.customcrates.crates.types.animations.openchestrolling;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.InventoryCrateAnimation;
import me.ztowne13.customcrates.crates.types.animations.openchest.NMSChestState;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class OpenChestRollingAnimation extends InventoryCrateAnimation
{
    public static ArrayList<Item> items = new ArrayList<>();

    boolean completed = false;

    // values

    int ticks = 0;
    int totalTicks = 0;
    int updates = 0;
    double currentTicks = 1.1;

    double finalTickLength = 11;
    double tickSpeed = 3;

    Location loc;
    Location playedLoc;
    Reward lastReward;
    Item item;


    public OpenChestRollingAnimation(Inventory inventory, Crate crate)
    {
        super("TO SET", crate, inventory);
        //prefix = CrateType.BLOCK_CRATEOPEN_ROLLING.getPrefix() + ".";
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        this.loc = l;

        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            playAnimation(p, l, true);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p, true, !PlayerManager.get(cc, p).isInCrate());
        return false;
    }

    public void playAnimation(final Player p, final Location l, boolean first)
    {
        if(first)
        {
            playedLoc = l;
            new NMSChestState().playChestAction(l.getBlock(), true);

            Location upOne = l.clone();
            upOne.setY(upOne.getY() + 1);
            upOne.setX(upOne.getX() + .5);
            upOne.setZ(upOne.getZ() + .5);

            lastReward = getCrates().getCs().getCr().getRandomReward(p);

            item = l.getWorld().dropItem(upOne, lastReward.getItemBuilder().getStack());
            item.setPickupDelay(100000);
            item.setVelocity(new Vector(0, item.getVelocity().getY(), 0));
            items.add(item);
        }

        if (!isCompleted())
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
            {
                @Override
                public void run()
                {

                    setTicks(getTicks() + 1);
                    setTotalTicks(getTotalTicks() + 1);

                    if (getTicks() >= getCurrentTicks() - 1.1)
                    {
                        lastReward = getCrates().getCs().getCr().getRandomReward(p);
                        item.setItemStack(lastReward.getItemBuilder().getStack());

                        setUpdates(getUpdates() + 1);
                        setTicks(0);
//                        if (getTickSound() != null)
//                        {
//                            rdh.getP().playSound(rdh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
//                                    getTickSound().getPitch());
//                        }

                        if (getCurrentTicks() > getFinalTickLength())
                        {
                            finishUp(p, 40);
                            completed = true;
                            return;
                        }

                        setCurrentTicks(.05 * Math.pow((getTickSpeed() / 40) + 1, getUpdates()));
                    }

                    playAnimation(p, l, false);
                }
            }, 1);
        }

        //                    rdh.setIndividualTicks(rdh.getIndividualTicks() + baseSpeed);
        //                    rdh.setTotalTicks(rdh.getTotalTicks() + baseSpeed);
        //
        //                    boolean b = false;
        //                    if (rdh.getIndividualTicks() * baseSpeed >= rdh.getCurrentTicks() - 1.1)
        //                    {
        //                        rdh.setUpdates(rdh.getUpdates() + 1);
        //                        b = true;
        //                        rdh.setIndividualTicks(0);
        //                        if (getTickSound() != null)
        //                        {
        //                            rdh.getP().playSound(rdh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
        //                                    getTickSound().getPitch());
        //                        }
        //
        //                        //if (cdh.getCurrentTicks() > cdh.getDisplayAmount())
        //                        if (rdh.getCurrentTicks() > getFinalTickLength())
        //                        {
        //                            finishUp(rdh.getP(), 50);
        //                            return;
        //                        }
        //
        //                        rdh.setCurrentTicks(.05 * Math.pow((getTickIncrease() / 40) + 1, rdh.getUpdates()));
        //                    }
        //
        //                    buildNewInventory(rdh, rdh.getTotalTicks() % glassUpdateTicks == 0, b);
        //
        //                    if (first || !rdh.getP().getOpenInventory().getTopInventory().getName()
        //                            .equals(rdh.getInv().getInv().getName()))
        //                    {
        //                        rdh.getP().openInventory(rdh.getInv().getInv());
        //                    }
        //
        //                    playSequence(rdh, false);
    }

    @Override
    public void loadValueFromConfig(StatusLogger sl)
    {
        FileConfiguration fc = getFu().get();
    }

    @Override
    public void finishUp(Player p)
    {
        ArrayList<Reward> rewards = new ArrayList<Reward>();
        rewards.add(lastReward);

        completeCrateRun(p, rewards, false);
        getCrates().tick(loc, CrateState.OPEN, p, rewards);
        item.remove();
        items.remove(item);
        new NMSChestState().playChestAction(playedLoc.getBlock(), false);
    }

    public void setUpdates(int updates)
    {
        this.updates = updates;
    }

    public int getUpdates()
    {
        return updates;
    }

    public double getFinalTickLength()
    {
        return finalTickLength;
    }

    public double getTickSpeed()
    {
        return tickSpeed;
    }

    public double getCurrentTicks()
    {
        return currentTicks;
    }

    public void setCurrentTicks(double currentTicks)
    {
        this.currentTicks = currentTicks;
    }

    public int getTicks()
    {
        return ticks;
    }

    public void setTicks(int ticks)
    {
        this.ticks = ticks;
    }

    public int getTotalTicks()
    {
        return totalTicks;
    }

    public void setTotalTicks(int totalTicks)
    {
        this.totalTicks = totalTicks;
    }

    public static void removeAllItems()
    {
        for (Item item : items)
            item.remove();
    }

    public boolean isCompleted()
    {
        return completed;
    }
}
