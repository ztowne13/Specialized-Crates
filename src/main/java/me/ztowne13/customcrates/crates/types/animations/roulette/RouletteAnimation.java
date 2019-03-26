package me.ztowne13.customcrates.crates.types.animations.roulette;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class RouletteAnimation extends RouletteManager
{
	static double baseSpeed = 1;

	public RouletteAnimation(Inventory inv, Crate crates)
	{
		super(inv, crates);
	}

	@Override
	public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
	{
		if(canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
		{
			RouletteDataHolder rdh = new RouletteDataHolder(p, l, this);
			playSequence(rdh, true);
			playRequiredOpenActions(p, !requireKeyInHand);
			return true;
		}

		playFailToOpen(p);
		return false;
	}

	public void playSequence(final RouletteDataHolder rdh, final boolean first)
	{
		if(!rdh.isCompleted())
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
			{
				@Override
				public void run()
				{
					/*rdh.getP().openInventory(buildNewInventory(rdh).getInv());

					if(getTickSound() != null)
					{
						rdh.getP().playSound(rdh.getL(), getTickSound().getSound(), getTickSound().getVolume(), getTickSound().getPitch());
					}

					if (rdh.getCurrentTicks() > rdh.getDisplayAmount())
					{
						finishUp(rdh.getP(), 20);
						return;
					}

					rdh.setCurrentTicks(rdh.getCurrentTicks() + getTickIncrease());

					playSequence(rdh);*/

					rdh.setIndividualTicks(rdh.getIndividualTicks() + baseSpeed);
					rdh.setTotalTicks(rdh.getTotalTicks() + baseSpeed);

					boolean b = false;
					if (rdh.getIndividualTicks() * baseSpeed >= rdh.getCurrentTicks() - 1.1)
					{
						rdh.setUpdates(rdh.getUpdates() + 1);
						b = true;
						rdh.setIndividualTicks(0);
						if (getTickSound() != null)
						{
							rdh.getP().playSound(rdh.getL(), getTickSound().getSound(), getTickSound().getVolume(), getTickSound().getPitch());
						}

						//if (cdh.getCurrentTicks() > cdh.getDisplayAmount())
						if(rdh.getCurrentTicks() > getFinalTickLength())
						{
							finishUp(rdh.getP(), 50);
							return;
						}

						rdh.setCurrentTicks(.05 * Math.pow((getTickIncrease()/40) + 1, rdh.getUpdates()));
					}

					buildNewInventory(rdh, rdh.getTotalTicks() % glassUpdateTicks == 0, b);

					if (first || !rdh.getP().getOpenInventory().getTopInventory().getName().equals(rdh.getInv().getInv().getName()))
					{
						rdh.getP().openInventory(rdh.getInv().getInv());
					}

					playSequence(rdh, false);
				}
			}, (long) baseSpeed);
		}
	}
}
