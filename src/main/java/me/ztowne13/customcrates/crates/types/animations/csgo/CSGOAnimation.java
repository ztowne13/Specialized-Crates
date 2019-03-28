package me.ztowne13.customcrates.crates.types.animations.csgo;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class CSGOAnimation extends CSGOManager
{
    static double baseSpeed = 1;

    public CSGOAnimation(Inventory inv, Crate crates)
    {
        super(inv, crates);
    }

    @Override
    public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
    {
        if (canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
        {
            CSGODataHolder cdh = new CSGODataHolder(p, l, this);
            playSequence(cdh, true);
            playRequiredOpenActions(p, !requireKeyInHand);
            return true;
        }

        playFailToOpen(p);
        return false;
    }

    public void playSequence(final CSGODataHolder cdh, final boolean first)
    {
        if (!cdh.isCompleted())
        {
            Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
            {
                @Override
                public void run()
                {
                    cdh.setIndividualTicks(cdh.getIndividualTicks() + baseSpeed);
                    cdh.setTotalTicks(cdh.getTotalTicks() + baseSpeed);

                    boolean b = false;
                    if (cdh.getIndividualTicks() * baseSpeed >= cdh.getCurrentTicks() - 1.1)
                    {
                        cdh.setUpdates(cdh.getUpdates() + 1);
                        b = true;
                        cdh.setIndividualTicks(0);
                        if (getTickSound() != null)
                        {
                            cdh.getP().playSound(cdh.getL(), getTickSound().getSound(), getTickSound().getVolume(),
                                    getTickSound().getPitch());
                        }

                        //if (cdh.getCurrentTicks() > cdh.getDisplayAmount())
                        if (cdh.getCurrentTicks() > getFinalTickLength())
                        {
                            finishUp(cdh.getP(), 50);

                            Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    closeAnim(cdh);
                                }
                            }, 10);

                            return;
                        }

                        cdh.setCurrentTicks(.05 * Math.pow((getTickIncrease() / 40) + 1, cdh.getUpdates()));
                    }

                    buildNewInventory(cdh, cdh.getTotalTicks() % glassUpdateTicks == 0, b, 0);

                    if (first || !cdh.getP().getOpenInventory().getTopInventory().getName()
                            .equals(cdh.getInv().getInv().getName()))
                    {
                        cdh.getP().openInventory(cdh.getInv().getInv());
                    }

                    playSequence(cdh, false);
                }
            }, (long) baseSpeed);
        }
    }
}
