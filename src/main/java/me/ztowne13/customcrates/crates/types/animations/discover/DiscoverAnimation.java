package me.ztowne13.customcrates.crates.types.animations.discover;

import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.crates.types.InventoryCrate;
import me.ztowne13.customcrates.crates.types.animations.discover.DiscoverDataHolder;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ztowne13 on 7/7/16.
 *
 *	 inv-name: '&8&l> &6&l%crate%'
 *	 tick-sound: BLOCK_STONE_HIT, 5, 5
 *	 click-sound: ENTITY_HORSE_GALLOP, 5, 5
 *	 uncover-sound: ENTITY_PLAYER_LEVELUP, 5, 5
 *	 minimum-rewards: 1
 *	 maximum-rewards: 4
 *	 count: true
 *	 random-display-duration: 50
 *	 uncover-block: CHEST;0
 */
public class DiscoverAnimation extends InventoryCrate
{
	String invName, prefix;
	SoundData tickSound, clickSound, uncoverSound;
	int minRewards, maxRewards, randomDisplayDuration, invRows;
	ItemStack uncoverBlock;
	boolean count;

	public DiscoverAnimation(Inventory inv, Crate crate)
	{
		super(inv, crate);
		prefix = crate.getCs().getCt().getPrefix() + ".";
	}


	@Override
	public boolean tick(Player p, Location l, CrateState cs, boolean requireKeyInHand)
	{
		if(canExecuteFor(cs, CrateState.OPEN, p, requireKeyInHand))
		{
			DiscoverDataHolder ddh = new DiscoverDataHolder(p, l, this);
			playSequence(ddh);
			playReopenTimer(ddh);
			playRequiredOpenActions(p, !requireKeyInHand);
			return true;
		}

		playFailToOpen(p);
		return false;
	}

	public void playReopenTimer(final DiscoverDataHolder ddh)
	{
		Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
		{
			@Override
			public void run()
			{
				if(!ddh.isCanCloseInventory())
				{
					if (!ddh.getP().getOpenInventory().getTopInventory().getName().equals(ddh.getIb().getInv().getName()))
					{
						ddh.getIb().open();
					}
					playReopenTimer(ddh);
				}
			}
		}, 2);
	}

	public void playSequence(final DiscoverDataHolder ddh)
	{
		int sequenceNum = ddh.getCurrentSequence();
		switch (sequenceNum)
		{
			case 1:
				buildInventory(ddh);
				ddh.getIb().open();
				break;
			case 2:
				Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
				{
					@Override
					public void run()
					{
						tickSound.playTo(ddh.getP(), ddh.getL());
						if(!(ddh.getSequence2Ticks() > getRandomDisplayDuration()))
						{
							playSequence(ddh);
							buildInventory(ddh);
						}
						else
						{
							ddh.setCurrentSequence(3);
							buildInventory(ddh);
						}
						ddh.setSequence2Ticks(ddh.getSequence2Ticks() + 2);
					}
				}, 2);
				break;
		}
	}

	public void buildInventory(DiscoverDataHolder ddh)
	{
		InventoryBuilder ib = ddh.getIb();
		int sequenceNum = ddh.getCurrentSequence();
		boolean correctDisplay = false;
		switch (sequenceNum)
		{
			case 1:
				ItemBuilder uncoverBlockIb = new ItemBuilder(uncoverBlock).setLore("").addLore("&7You have &f" + ddh.getRemainingClicks() + " rewards to chose from.");
				ItemBuilder alreadyUncoveredIb = new ItemBuilder(DynamicMaterial.LIME_STAINED_GLASS_PANE, 1).setName("&aUncover all rewards to unlock");
				for(int i = 0; i < ib.getInv().getSize(); i++)
				{
					if(ddh.getAlreadyChosenSlots().contains(i))
					{
						ib.setItem(i, alreadyUncoveredIb);
					}
					else
					{
						uncoverBlockIb.setName("&aReward #" + (i + 1));
						if (count)
						{
							uncoverBlockIb.get().setAmount(i + 1);
						}
						ib.setItem(i, uncoverBlockIb);
					}
				}
				break;
			case 3:
				correctDisplay = true;
			case 2:
				ib.getInv().clear();
				ItemBuilder reward = new ItemBuilder(DynamicMaterial.LIME_STAINED_GLASS_PANE, 1).setName("&Reward");
				Random r = new Random();
				if(!correctDisplay)
				{
					for (int i = 0; i < ib.getInv().getSize(); i++)
					{
						if (r.nextInt(7) == 1)
						{
							ib.setItem(i, reward);
						}
					}
				}
				else
				{
					reward.setName("&aClick me to unlock your reward");
					for(int i : ddh.getAlreadyChosenSlots())
					{
						if(ddh.getAlreadyDisplayedRewards().keySet().contains(i))
						{
							ib.setItem(i, ddh.getAlreadyDisplayedRewards().get(i).getDisplayItem());
						}
						else
						{
							ib.setItem(i, reward);
						}
					}
				}
		}


	}

	public void handleClick(final DiscoverDataHolder ddh, int slot)
	{
		switch(ddh.getCurrentSequence())
		{
			case 1:
				if(!ddh.getAlreadyChosenSlots().contains(slot))
				{
					ddh.getAlreadyChosenSlots().add(slot);
					ddh.setRemainingClicks(ddh.getRemainingClicks() - 1);
					clickSound.playTo(ddh.getP(), ddh.getL());
					buildInventory(ddh);

					if(ddh.getRemainingClicks() <= 0)
					{
						ddh.setCurrentSequence(2);
						Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
						{
							@Override
							public void run()
							{
								ddh.setCurrentSequence(2);
								playSequence(ddh);
							}
						}, 20);
						return;
					}
				}
				break;
			case 3:
				if(ddh.getAlreadyChosenSlots().contains(slot))
				{
					if(!ddh.getAlreadyDisplayedRewards().keySet().contains(slot))
					{
						Reward newR = getCrates().getCs().getCr().getRandomReward(ddh.getP());
						uncoverSound.playTo(ddh.getP(), ddh.getL());
						ddh.getAlreadyDisplayedRewards().put(slot, newR);
						buildInventory(ddh);
						if(ddh.getAlreadyDisplayedRewards().keySet().size() == ddh.getAlreadyChosenSlots().size())
						{
							ddh.setCurrentSequence(4);
							ddh.setCanCloseInventory(true);
							finishUp(ddh.getP(), 50);
						}
					}
				}
				break;
		}
	}

	@Override
	public void loadValueFromConfig(StatusLogger sl)
	{
		FileConfiguration fc = getFu().get();

		invName = fc.getString(prefix + "inv-name");

		invRows = FileUtils.loadInt(fc.getString(prefix + "inventory-rows"), 3, sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_INVROWS_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_INVROWS_INVALID);

		minRewards = FileUtils.loadInt(fc.getString(prefix + "minimum-rewards"), 1, sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_MINREWARDS_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_MINREWARDS_INVALID);
		maxRewards = FileUtils.loadInt(fc.getString(prefix + "maximum-rewards"), 1, sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_MAXREWARDS_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_MAXREWARDS_INVALID);
		randomDisplayDuration = FileUtils.loadInt(fc.getString(prefix + "random-display-duration"), 1, sl, StatusLoggerEvent.ANIMATION_DISCOVER_RANDDISPLAYLOCATION_SUCCESS, StatusLoggerEvent.ANIMATION_DISCOVER_RANDDISPLAYLOCATION_INVALID);

		uncoverBlock = FileUtils.loadItem(fc.getString(prefix + "cover-block"), sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_MATERIAL_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_DURABILITY_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_COVERBLOCK_SUCCESS);

		count = FileUtils.loadBoolean(fc.getString(prefix + "count"), true, sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_COUNT_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_COUNT_INVALID);

		tickSound = FileUtils.loadSound(fc.getString(prefix + "tick-sound"), sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_SOUND_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_SOUND_FAILURE,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_VOLUME_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_VOLUME_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_PITCHVOL_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_PITCH_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_TICKSOUND_PITCH_INVALID);

		clickSound = FileUtils.loadSound(fc.getString(prefix + "click-sound"), sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_SOUND_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_SOUND_FAILURE,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_VOLUME_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_VOLUME_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_PITCHVOL_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_PITCH_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_CLICKSOUND_PITCH_INVALID);

		uncoverSound = FileUtils.loadSound(fc.getString(prefix + "uncover-sound"), sl,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_SOUND_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_SOUND_FAILURE,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_VOLUME_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_VOLUME_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_PITCHVOL_INVALID,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_PITCH_SUCCESS,
				StatusLoggerEvent.ANIMATION_DISCOVER_UNCOVERSOUND_PITCH_INVALID);
	}

	@Override
	public void finishUp(Player p)
	{
		DiscoverDataHolder ddh = DiscoverDataHolder.getHolders().get(p);
		ddh.setCompleted(true);

		ArrayList<Reward> rewards = new ArrayList<>();
		rewards.addAll(ddh.getAlreadyDisplayedRewards().values());

		completeCrateRun(p, rewards, false);
		getCrates().tick(ddh.getL(), CrateState.OPEN, p, rewards);
		ddh.getHolders().remove(p);
	}

	public String getInvName()
	{
		return invName;
	}

	public void setInvName(String invName)
	{
		this.invName = invName;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}

	public SoundData getTickSound()
	{
		return tickSound;
	}

	public void setTickSound(SoundData tickSound)
	{
		this.tickSound = tickSound;
	}

	public SoundData getClickSound()
	{
		return clickSound;
	}

	public void setClickSound(SoundData clickSound)
	{
		this.clickSound = clickSound;
	}

	public SoundData getUncoverSound()
	{
		return uncoverSound;
	}

	public void setUncoverSound(SoundData uncoverSound)
	{
		this.uncoverSound = uncoverSound;
	}

	public int getMinRewards()
	{
		return minRewards;
	}

	public void setMinRewards(int minRewards)
	{
		this.minRewards = minRewards;
	}

	public int getMaxRewards()
	{
		return maxRewards;
	}

	public void setMaxRewards(int maxRewards)
	{
		this.maxRewards = maxRewards;
	}

	public int getRandomDisplayDuration()
	{
		return randomDisplayDuration;
	}

	public void setRandomDisplayDuration(int randomDisplayDuration)
	{
		this.randomDisplayDuration = randomDisplayDuration;
	}

	public ItemStack getUncoverBlock()
	{
		return uncoverBlock;
	}

	public void setUncoverBlock(ItemStack uncoverBlock)
	{
		this.uncoverBlock = uncoverBlock;
	}

	public boolean isCount()
	{
		return count;
	}

	public void setCount(boolean count)
	{
		this.count = count;
	}

	public int getInvRows()
	{
		return invRows;
	}

	public void setInvRows(int invRows)
	{
		this.invRows = invRows;
	}
}
