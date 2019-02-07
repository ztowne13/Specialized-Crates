package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.actions.ActionEffect;
import me.ztowne13.customcrates.crates.options.actions.BukkitActionEffect;
import me.ztowne13.customcrates.crates.options.actions.NMSActionEffect;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.actions.Title;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CActions extends CSetting
{
	HashMap<String,HashMap<String,ArrayList<String>>> actions = new HashMap<String,HashMap<String,ArrayList<String>>>();
	
	public CActions(Crate crates)
	{
		super(crates, crates.getCc());
	}

	@Override
	public void loadFor(CrateSettingsBuilder csb, CrateState cs) 
	{
		if(csb.hasV("open.actions"))
		{
			List<String> list = getCrates().getCs().getFc().getStringList("open.actions");
			for(String s: list)
			{
				addEntryByString("DEFAULT", s);
			}
		}

		if(csb.hasV("open.crate-tiers"))
		{
			for(String tier: getCrates().getCs().getFc().getConfigurationSection("open.crate-tiers").getKeys(false))
			{
				if(csb.hasV("open.crate-tiers." + tier + ".actions"))
				{
					List<String> list = getCrates().getCs().getFc().getStringList("open.crate-tiers." + tier + ".actions");
					for(String s: list)
					{
						addEntryByString(tier, s);
					}
				}
			}
		}
	}

	public void saveToFile()
	{
		if(!actions.isEmpty())
		{
			for (String tier : actions.keySet())
			{
				ArrayList<String> toSetList = new ArrayList<>();
				String path = "open." + (tier.equalsIgnoreCase("DEFAULT") ? "" : "crate-tiers." + tier + ".") + "actions";

				for (String actionType : actions.get(tier).keySet())
				{
					for (String action : actions.get(tier).get(actionType))
					{
						toSetList.add(ChatUtils.fromChatColor(actionType + ", " + action));
					}
				}

				getFu().get().set(path, toSetList);
			}
		}
	}
	
	public void addEntry(String type, String action, String tier)
	{
 		HashMap<String,ArrayList<String>> map = getActions().containsKey(tier) ? getActions().get(tier) : new HashMap<String,ArrayList<String>>();

		ArrayList<String> list = map.containsKey(type) ? map.get(type) : new ArrayList<String>();
		list.add(action);
		map.put(type, list);

		StatusLoggerEvent.ACTION_ADD.log(getCrates(), new String[]{action, tier});
		getActions().put(tier, map);
	}

	public void removeEntry(String type, String action, String tier)
	{
		getActions().get(tier).get(type).remove(action);
	}
	
	public void addEntryByString(String crateTier, String toAdd)
	{
		String[] split = toAdd.split(",");
		String type = split[0].replace(" ", "").replace(",", "");
		String action = "";
		
		boolean b = false;
		for(String words: split)
		{
			if(b)
			{
				action = words + " ";
			}
			b = true;
		}
		
		if(action.startsWith(" "))
		{
			action = action.substring(1);
		}
		
		action = ChatUtils.toChatColor(action);

		addEntry(type, action, crateTier);
	}

	public void playAll(Player p, boolean pre)
	{
		playAll(p, new ArrayList<Reward>(), pre);
	}

	public void playAll(Player p, PlacedCrate placedCrate, boolean pre)
	{
		playAll(p, placedCrate, new ArrayList<Reward>(), pre);
	}

	public void playAll(Player p, ArrayList<Reward> rewards, boolean pre)
	{
		playAll(p, null, rewards, pre);
	}

	public void playAll(Player p, PlacedCrate placedCrate, ArrayList<Reward> rewards, boolean pre)
	{
		ActionEffect actionEffect = new NMSActionEffect(cc);
		//ActionEffect actionEffect = NMSUtils.serverVersion111OrLater() ? new BukkitActionEffect(cc) : new NMSActionEffect(cc);
		actionEffect.newTitle();
		boolean toRunTitle = false;

		ArrayList<String> rewardsAsDisplayname = new ArrayList<>();
		for(Reward r : rewards)
		{
			rewardsAsDisplayname.add(r.getDisplayName());
		}

		for(String tier: getActions().keySet())
		{
			if(pre || (tier.equalsIgnoreCase("DEFAULT") && !getActions().keySet().contains(rewards.get(0).getRarity().toLowerCase())) || rewards.get(0).getRarity().equalsIgnoreCase(tier))
			{
				for(String s: getActions().get(tier).keySet())
				{
					s = s.toUpperCase();
					for(String msg : getActions().get(tier).get(s))
					{
						if(pre)
						{
							if(s.startsWith("PRE_"))
							{
								s = s.substring(4);
							}
							else
							{
								continue;
							}
						}
						else
						{
							if(s.startsWith("PRE_"))
							{
								continue;
							}
						}

						msg = ChatUtils.toChatColor(msg.replace("%player%", p.getName()).replace("%crate%", getCrates().getName()).replace("%reward%", rewardsAsDisplayname.toString().replace("[", "").replace("]", "")));
						if (s.equalsIgnoreCase("MESSAGE"))
						{
							p.sendMessage(msg);
						}
						else if (s.equalsIgnoreCase("BROADCAST"))
						{
							Bukkit.broadcastMessage(msg);
						}
						else if (s.equalsIgnoreCase("ACTIONBAR"))
						{
							actionEffect.playActionBar(p, msg);
						}
						else if (s.equalsIgnoreCase("TITLE"))
						{
							actionEffect.setDisplayTitle(msg);
							toRunTitle = true;
						}
						else if (s.equalsIgnoreCase("SUBTITLE"))
						{
							actionEffect.setDisplaySubtitle(msg);
							toRunTitle = true;
						}
						else
						{
							ChatUtils.log(new String[]{"Please note that an action type called " + s + " was attempted to be run", "    However, this action type does not exist. Valid types are:", "      MESSAGE, BROADCAST, ACTIONBAR, TITLE, and SUBTITLE"});
						}
					}
				}
			}
		}

		if(toRunTitle)
		{
			actionEffect.playTitle(p);
		}

		if(!pre)
		{
			playRewardCrate(p, rewardsAsDisplayname);
		}
	}

	public void playRewardCrate(Player p, ArrayList<String> rewards)
	{
		final PlayerManager pm = PlayerManager.get(cc, p);
		if(!(pm.getLastOpenedPlacedCrate() == null))
		{
			final PlacedCrate placedCrate = pm.getLastOpenedPlacedCrate();
			String msg = placedCrate.getCrates().getCs().getCholoCopy().getRewardHologram();
			if(!msg.equalsIgnoreCase(""))
			{
				msg = ChatUtils.toChatColor(msg.replace("%reward%", rewards.toString().replace("[", "").replace("]", "")));

				final DynamicHologram dynamicHologram = placedCrate.getCholo().getDh();
				dynamicHologram.setDisplayingRewardHologram(true);
				dynamicHologram.delete();

				Location rewardLoc = placedCrate.getL().clone();
				rewardLoc.setY(rewardLoc.getY() - .3);
				dynamicHologram.create(rewardLoc);
				dynamicHologram.addLine(msg);

				Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
				{
					@Override
					public void run()
					{
						dynamicHologram.delete();

						final Location cloneY = placedCrate.getL().clone();
						cloneY.setY(cloneY.getY() + .5);

						placedCrate.getCrates().getCs().getCholoCopy().createHologram(placedCrate, cloneY, dynamicHologram);

						pm.setLastOpenedPlacedCrate(null);
						dynamicHologram.setDisplayingRewardHologram(false);

						if(!(dynamicHologram.getHa() == null))
						{
							dynamicHologram.getHa().update(true);
						}
					}
				}, (int) SettingsValues.REWARD_HOLOGRAM_LENGTH.getValue(cc)*20);
			}
		}
	}

	public HashMap<String, HashMap<String, ArrayList<String>>> getActions()
	{
		return actions;
	}

	public void setActions(HashMap<String, HashMap<String, ArrayList<String>>> actions)
	{
		this.actions = actions;
	}
}
