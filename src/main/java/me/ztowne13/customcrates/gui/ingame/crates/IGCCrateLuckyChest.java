package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/5/16.
 */
public class IGCCrateLuckyChest extends IGCMenuCrate
{
	public IGCCrateLuckyChest(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates)
	{
		super(cc, p, lastMenu, "&7&l> &6&lMine Crate", crates);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(27);

		ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

		ib.setItem(11, new ItemBuilder(Material.FISHING_ROD, 1, 0).setName("&aChance").setLore("&7Current value: ").addLore("&7" + cs.getClc().getChance() + "/" + cs.getClc().getOutOfChance()));
		ib.setItem(12, new ItemBuilder(DynamicMaterial.LIGHT_GRAY_DYE, 1).setName("&aBlacklist?").setLore("Is block-list a blacklist? (false)").addLore("or whitelist? (true)").addLore("").addLore("&7Current value: ").addLore("&7" + cs.getClc().isBLWL() + ""));
		ItemBuilder bList = new ItemBuilder(Material.STONE, 1, 0).setName("&aAdd to the block-list").setLore("&7Current values: ");

		for(Material m : cs.getClc().getWhiteList())
		{
			bList.addLore("&7" + m.name());
		}
		ib.setItem(13, bList);
		ib.setItem(14, bList.setName("&aRemove from the block-list"));

		ItemBuilder wList = new ItemBuilder(DynamicMaterial.PURPLE_DYE, 1).setName("&aAdd to the worlds").setLore("&7Current values: ");
		for(World w : cs.getClc().getWorlds())
		{
			if(w != null)
			{
				wList.addLore("&7" + w.getName());
			}
		}
		ib.setItem(15, wList);
		ib.setItem(16, wList.setName("&aRemove from the worlds"));

		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		switch(slot)
		{
			case 9:
				up();
				break;
			case 11:
				new InputMenu(getCc(), getP(), "chance", cs.getClc().getChance() + "/" + cs.getClc().getOutOfChance(), "Format it 'chance/out of what chance'.", String.class, this);
				break;
			case 12:
				new InputMenu(getCc(), getP(), "blacklist", cs.getClc().isBLWL() + "", Boolean.class, this);
				break;
			case 13:
				new InputMenu(getCc(), getP(), "add block-list", cs.getClc().getWhiteList().toString(), "If it's a blacklist, these are the values players CANT mine. If it's a whitelist, these are the ONLY blocks player's can mine.", String.class, this);
				break;
			case 14:
				new InputMenu(getCc(), getP(), "remove block-list", cs.getClc().getWhiteList().toString(), "If it's a blacklist, these are the values players CANT mine. If it's a whitelist, these are the ONLY blocks player's can mine.", String.class, this);
				break;
			case 15:
				new InputMenu(getCc(), getP(), "add worlds", cs.getClc().getWorlds().toString(), "Current valid worlds: " + Bukkit.getWorlds(), String.class, this);
				break;
			case 16:
				new InputMenu(getCc(), getP(), "remove worlds", cs.getClc().getWorlds().toString(), "Current valid worlds: " + Bukkit.getWorlds(), String.class, this);
				break;
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		if(value.equalsIgnoreCase("chance"))
		{
			try
			{
				String[] split = input.split("/");
				if(Utils.isInt(split[0]))
				{
					if(Utils.isInt(split[1]))
					{
						cs.getClc().setChance(Double.valueOf(split[0]));
						cs.getClc().setOutOfChance(Double.valueOf(split[1]));
						ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
						return true;
					}
					else
					{
						ChatUtils.msgError(getP(), split[1] + " is not a valid number.");
					}
				}
				else
				{
					ChatUtils.msgError(getP(), split[0] + " is not a valid number.");
				}
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not formatted 'number/number' or 'chance/out of chance'");
			}
		}
		else if(value.equalsIgnoreCase("blacklist"))
		{
			try
			{
				Boolean b = Boolean.valueOf(input);
				cs.getClc().setBLWL(b);
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not a valid true/false value.");
			}
		}
		else if(value.equalsIgnoreCase("add block-list"))
		{
			try
			{
				Material m = Material.valueOf(input.toUpperCase());
				cs.getClc().getWhiteList().add(m);
				ChatUtils.msgSuccess(getP(), "Added " + input + " to the whitelist / blacklist.");
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not a valid material.");
			}
		}
		else if(value.equalsIgnoreCase("remove block-list"))
		{
			try
			{
				Material m = Material.valueOf(input.toUpperCase());
				if(cs.getClc().getWhiteList().contains(m))
				{
					cs.getClc().getWhiteList().remove(m);
					ChatUtils.msgSuccess(getP(), "Removed the " + input + " value from the whitelist / blacklist");
					return true;
				}
				else
				{
					ChatUtils.msgError(getP(), input + " does not exist in the blacklist / whitelist.");
				}
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is not a valid material.");
			}
		}
		else if(value.equalsIgnoreCase("add worlds"))
		{
			try
			{
				World w = Bukkit.getWorld(input);
				cs.getClc().getWorlds().add(w);
				ChatUtils.msgSuccess(getP(), "Added " + input + " to the list of allowed worlds.");
				return true;
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is a non-existent world from the list of worlds: " + Bukkit.getWorlds().toString());
			}
		}
		else if(value.equalsIgnoreCase("remove worlds"))
		{
			try
			{
				World w = Bukkit.getWorld(input);
				if(w != null)
				{
					if (cs.getClc().getWorlds().contains(w))
					{
						cs.getClc().getWorlds().remove(w);
						ChatUtils.msgSuccess(getP(), "Removed " + input + " from the list of allowed worlds.");
						return true;
					}
					else
					{
						ChatUtils.msgError(getP(), input + " is not currently allowed in the worlds list to be remobed. Current worlds: " + cs.getClc().getWorlds().toString());
					}
				}
				else
				{
					throw new Exception();
				}
			}
			catch(Exception exc)
			{
				ChatUtils.msgError(getP(), input + " is a non-existent world from the list of worlds: " + Bukkit.getWorlds().toString());
			}
		}
		return false;
	}
}
