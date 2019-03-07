package me.ztowne13.customcrates.crates.options.rewards;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.*;
import me.ztowne13.customcrates.utils.nbt_utils.NBTTagManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Reward 
{
	CustomCrates cc;
	FileConfiguration fc;

	CRewards cr;
	String rewardName, displayName, rarity;
	ItemStack displayItem;

	double chance = -1;
	List<String> commands;
	int totalUses;
	Material m;
	boolean needsMoreConfig = false, glow = false;

	boolean toLog = false;

	public Reward(CustomCrates cc, String rewardName)
	{
		this.cc = cc;
		setRewardName(rewardName);
	}

	public Reward(CustomCrates cc, CRewards cr, String rewardName)
	{
		this(cc, rewardName);
		this.cr = cr;
		toLog = true;
		loadChance();
	}
	
	public void runCommands(Player p)
	{
		for(String command : getCommands())
		{
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.toString().replace("{name}", p.getName()));
		}
		//new RewardLimitEvent(this, PlayerManager.get(cc, p).getPdm().getCurrentRewardLimitUses(this), 1).addTo(PlayerManager.get(cc, p).getPdm());
	}

	public void writeToFile()
	{
		FileHandler fu = getCc().getRewardsFile();
		FileConfiguration fc = fu.get();
		fc.set(getPath("name"), getDisplayName());
		fc.set(getPath("commands"), getCommands());
		fc.set(getPath("item"), getDisplayItem().getType() + ";" + getDisplayItem().getDurability());
		fc.set(getPath("glow"), glow);

		ArrayList<String> parsedEnchs = new ArrayList<>();

		if(!getDisplayItem().getEnchantments().isEmpty())
		{
			for(Enchantment ench: getDisplayItem().getEnchantments().keySet())
			{
				parsedEnchs.add(ench.getName() + ";" + getDisplayItem().getEnchantments().get(ench));
			}
			fc.set(getPath("enchantments"), parsedEnchs);
		}

		fc.set(getPath("chance"), getChance());
		fc.set(getPath("rarity"), getRarity());
		fc.set(getPath("receive-limit"), getTotalUses());

		if(NMSUtils.Version.v1_12.isServerVersionOrEarlier() && NMSUtils.Version.v1_8.isServerVersionOrLater())
		{
			fc.set(getPath("nbt-tags"), NBTTagManager.getFrom(getDisplayItem()));
		}

		fu.save();
	}

	public String delete(boolean forSure)
	{
		if(!forSure)
		{
			ArrayList<String> cratesThatUse = new ArrayList<>();
			for(Crate cs: Crate.getLoadedCrates().values())
			{
				if(!cs.isMultiCrate())
				{
					for (Reward r : cs.getCs().getCr().getCrateRewards())
					{
						if (r.equals(this))
						{
							cratesThatUse.add(cs.getName());
							break;
						}
					}
				}
			}

			return cratesThatUse.toString();
		}
		else
		{
			getCc().getRewardsFile().get().set(getRewardName(), null);
			getCc().getRewardsFile().save();
			CRewards.getAllRewards().remove(getRewardName());
		}
		return "";
	}
	
	public String applyVariablesTo(String s)
	{
		return ChatUtils.toChatColor(s.replace("%rewardname%", getRewardName()).
				replace("%displayname%", getDisplayName()).
				replace("%writtenchance%", getChance() + "").
				replace("%rarity%", rarity)).
				replace("%chance%", getFormattedChance());
	}
	
	public String getFormattedChance()
	{
		if(toLog)
		{
			double ch = getChance() / cr.getTotalOdds();
			ch = ch * 100;
			return new DecimalFormat("#.##").format(ch);
		}
		else
		{
			return -1 + "";
		}
	}
	
	public void loadChance()
	{
		try
		{
			setChance(getCc().getRewardsFile().get().getInt(getPath("chance")));
		}
		catch(Exception exc)
		{
			needsMoreConfig = true;
			if(toLog)
			{
				setChance(-1);
				StatusLoggerEvent.REWARD_CHANCE_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
			}
		}
	}
	
	public boolean loadFromConfig()
	{
		setFc(getCc().getRewardsFile().get());

		try
		{
			setDisplayName(getFc().getString(getPath("name")));

		}
		catch(Exception exc)
		{
			needsMoreConfig = true;
			if(toLog)
			{
				StatusLoggerEvent.REWARD_NAME_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
				return false;
			}
		}

		try
		{
			setRarity(getFc().getString(getPath("rarity")));
		}
		catch(Exception exc)
		{
			needsMoreConfig = true;
			if (toLog)
			{
				StatusLoggerEvent.REWARD_RARITY_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
				return false;
			}
		}

		try
		{
			setGlow(getFc().getBoolean(getPath("glow")));
		}
		catch(Exception exc)
		{

		}

		try
		{
			buildDisplayItemFromConfig();
		}
		catch(Exception exc)
		{
			needsMoreConfig = true;
			if(toLog)
			{
				StatusLoggerEvent.REWARD_ITEM_NONEXISTENT.log(getCr().getCrates(), new String[]{this.toString()});
				return false;
			}
		}

		try
		{
			setCommands(getFc().getStringList(getPath("commands")));
		}
		catch(Exception exc)
		{
			if(toLog)
			{
				StatusLoggerEvent.REWARD_COMMAND_INVALID.log(getCr().getCrates(), new String[]{this.toString()});
				return false;
			}
		}

		try
		{
			setTotalUses(getFc().getInt(getPath("receive-limit")));
		}
		catch(Exception exc)
		{
			setTotalUses(-1);
		}

		return true;
	}

	public void buildDisplayItemFromConfig()
	{
		String unsplitMat = getFc().getString(getPath("item"));

		DynamicMaterial m = DynamicMaterial.fromString(unsplitMat);

		ItemBuilder ib = new ItemBuilder(m, 1);
		ib.setName(applyVariablesTo(cc.getSettings().getConfigValues().get("inv-reward-item-name").toString()));

		// If an item has a custom lore, apply that. Otherwise apply the general lore.
		if(getFc().contains(getPath("lore")))
		{
			for (String s : getFc().getStringList("lore"))
			{
				ib.addLore(applyVariablesTo(s));
			}
		}
		else
		{
			for (Object s : (ArrayList<String>) cc.getSettings().getConfigValues().get("inv-reward-item-lore"))
			{
				ib.addLore(applyVariablesTo(s.toString()));
			}
		}

		if(NMSUtils.Version.v1_12.isServerVersionOrEarlier() && NMSUtils.Version.v1_8.isServerVersionOrLater())
		{
			if(getFc().contains(getPath("nbt-tags")))
			{
				for(String s : fc.getStringList(getPath("nbt-tags")))
				{
					ib.applyNBTTag(s);
				}
			}
		}
		
		if(getFc().contains(getPath("enchantments")))
		{
			String cause = getPath("enchantments") + " value is not a valid list of enchantments.";
			try
			{
				for(String s: getFc().getStringList(getPath("enchantments")))
				{
					cause = "Enchantment " + s + " is not formatted ENCHANTMENT;LEVEL";
					String[] args = s.split(";");
					
					cause = args[0] + " is not a valid enchantment.";
					Enchantment ench = Enchantment.getByName(args[0].toUpperCase());

					if(ench == null)
					{
						throw new NullPointerException(cause);
					}

					cause = "Enchantment " + s + " is not formatted ENCHANTMENT;LEVEL";
					cause = args[1] + " is not a valid Integer.";
					int level = Integer.parseInt(args[1]);

					ib.addEnchantment(ench, level);
					continue;
				}
			}
			catch(Exception exc)
			{
				StatusLoggerEvent.REWARD_ENCHANT_INVALID.log(getCr().getCrates(), new String[]{getDisplayName(), cause});
			}
		}

		if(glow)
		{
			ib.setStack(NMSUtils.Version.v1_7.isServerVersionOrLater() ? new BukkitGlowEffect(ib.get()).apply() : new NMSGlowEffect(ib.get()).apply());
		}

		setDisplayItem(ib.get());
	}

	public void checkIsNeedMoreConfig()
	{
		needsMoreConfig = !(chance != -1 && getDisplayName() != null && rarity != null && displayItem != null);
	}

	public boolean equals(Reward r)
	{
		return r.getRewardName().equalsIgnoreCase(getRewardName());
	}

	public String toString()
	{
		return getRewardName();
	}
	
	public String getPath(String s)
	{
		return getRewardName() + "." + s;
	}
	
	public ItemStack getDisplayItem() 
	{
		return displayItem;
	}

	public void setDisplayItem(ItemStack displayItem)
	{
		this.displayItem = displayItem;
	}

	public List<String> getCommands() 
	{
		return commands;
	}

	public void setCommands(List<String> list) 
	{
		this.commands = list;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public void setDisplayName(String displayName)
	{
		this.displayName = displayName;
	}

	public String getRewardName()
	{
		return rewardName;
	}

	public void setRewardName(String rewardName)
	{
		this.rewardName = rewardName;
	}

	public String getRarity()
	{
		return rarity;
	}

	public void setRarity(String rarity)
	{
		this.rarity = rarity;
	}

	public Double getChance()
	{
		return chance;
	}

	public void setChance(Integer chance) 
	{
		this.chance = chance;
	}

	public Material getM()
	{
		return m;
	}

	public void setM(Material m)
	{
		this.m = m;
	}

	public CustomCrates getCc() {
		return cc;
	}

	public void setCc(CustomCrates cc) {
		this.cc = cc;
	}

	public int getTotalUses() {
		return totalUses;
	}

	public void setTotalUses(int totalUses) {
		this.totalUses = totalUses;
	}

	public FileConfiguration getFc()
	{
		return fc;
	}

	public void setFc(FileConfiguration fc)
	{
		this.fc = fc;
	}

	public void setChance(double chance)
	{
		this.chance = chance;
	}

	public CRewards getCr()
	{
		return cr;
	}

	public void setCr(CRewards cr)
	{
		this.cr = cr;
	}

	public boolean isNeedsMoreConfig()
	{
		return needsMoreConfig;
	}

	public void setNeedsMoreConfig(boolean needsMoreConfig)
	{
		this.needsMoreConfig = needsMoreConfig;
	}

	public boolean isGlow()
	{
		return glow;
	}

	public void setGlow(boolean glow)
	{
		this.glow = glow;
	}
}
