package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.ingame.crateanimations.*;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.FileHandler;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 3/20/16.
 */
public class IGCMenuCrateConfig extends IGCMenu
{
	public IGCMenuCrateConfig(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lCrateConfig.YML");
	}

	@Override
	public void open()
	{
		p.closeInventory();
		putInMenu();

		FileHandler fu = cc.getCrateconfigFile();
		FileConfiguration fc = fu.get();

		InventoryBuilder ib = createDefault(27);

		ib.setItem(18, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
		ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());

		ib.setItem(11, new ItemBuilder(Material.PAPER, 1, 0).setName("&aCSGO Animation").setLore("&7Animation name: &fINV_CSGO").addLore("").addLore("&7Used by crates: &f" + CrateType.INV_CSGO.getUses()));
		ib.setItem(12, new ItemBuilder(Material.PAPER, 1, 0).setName("&aRoulette Animation").setLore("&7Animation name: &fINV_ROULETTE").addLore("").addLore("&7Used by crates: &f" + CrateType.INV_ROULETTE.getUses()));
		ib.setItem(13, new ItemBuilder(Material.PAPER, 1, 0).setName("&aMenu Animation").setLore("&7Animation name: &fINV_MENU").addLore("").addLore("&7Used by crates: &f" + CrateType.INV_MENU.getUses()));
		ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&aEnclose Animation").setLore("&7Animation name: &fINV_ENCLOSE").addLore("").addLore("&7Used by crates: &f" + CrateType.INV_ENCLOSE.getUses()));
		ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDiscover Animation").setLore("&7Animation name: &fINV_DISCOVER").addLore("").addLore("&7Used by crates: &f" + CrateType.INV_DISCOVER.getUses()));
		ib.setItem(16, new ItemBuilder(Material.PAPER, 1, 0).setName("&aOpen Chest Animation").setLore("&7Animation name: &fBLOCK_CRATEOPEN").addLore("").addLore("&7Used by crates: &f" + CrateType.BLOCK_CRATEOPEN.getUses()));

		/*ib.setItem(27, IGCDefaultItems.EXIT_BUTTON.getIb());

		ItemBuilder nameDisplay = new ItemBuilder(Material.PAPER, 1, 0).setLore("").addLore("&7---->");
		ItemBuilder nameEditor = new ItemBuilder(Material.BOOK, 1, 0).setName("&aChange the inv-name");

		// Roulette
		ib.setItem(1, nameDisplay.setName("&aRoulette Animation"));
		ib.setItem(2, nameEditor.setLore("&7Current value:").addLore("&f" + getValue("Roulette", "inv-name")));
		ib.setItem(3, new ItemBuilder(Material.RECORD_3, 1, 0).setName("&aChange the tick-sound").setLore("&7Current value:").addLore("&f" + getValue("Roulette", "tick-sound")));
		ib.setItem(4, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the tick-speed-per-run").setLore("&7Current value:").addLore("&f" + getValue("Roulette", "tick-speed-per-run")));
		ib.setItem(5, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the final-crate-tick-length").setLore("&7Current value:").addLore("&f" + getValue("Roulette", "final-crate-tick-length")));

		ItemBuilder randomBlocks = new ItemBuilder(Material.STONE, 1, 0).setName("&aAdd to random-blocks").setLore("&7Current values: ");
		for(String s: fc.getStringList("CrateType.Inventory.Roulette.random-blocks"))
		{
			randomBlocks.addLore("&f" + s);
		}

		ib.setItem(6, randomBlocks);
		ib.setItem(7, randomBlocks.setName("&aRemove from random-blocks"));

		// CS:GO
		ib.setItem(19, nameDisplay.setName("&aCSGO Animation"));
		ib.setItem(20, nameEditor.setLore("&7Current value:").addLore("&f" + getValue("CSGO", "inv-name")));
		ib.setItem(21, new ItemBuilder(Material.RECORD_3, 1, 0).setName("&aChange the tick-sound").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "tick-sound")));
		ib.setItem(22, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the tick-speed-per-run").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "tick-speed-per-run")));
		ib.setItem(23, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aChange the final-crate-tick-length").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "final-crate-tick-length")));

		randomBlocks = new ItemBuilder(Material.STONE, 1, 0).setName("&aAdd to filler-blocks").setLore("&7Current values: ");
		for(String s: fc.getStringList("CrateType.Inventory.CSGO.filler-blocks"))
		{
			randomBlocks.addLore("&f" + s);
		}

		// Menu
		ib.setItem(24, randomBlocks);
		ib.setItem(25, randomBlocks.setName("&aRemove from filler-blocks"));
		ib.setItem(26, new ItemBuilder(Material.REDSTONE_TORCH_ON, 1, 0).setName("&aChange the identifier-block").setLore("&7Current value:").addLore("&f" + getValue("CSGO", "identifier-block")));

		ib.setItem(37, nameDisplay.setName("&aMenu Animation"));
		ib.setItem(38, nameEditor.setLore("&7Current value:").addLore("&f" + getValue("Menu", "inv-name")));
		ib.setItem(39, new ItemBuilder(Material.FENCE, 1, 0).setName("&aChange the inventory-rows").setLore("&7Current value:").addLore("&f" + getValue("Menu", "inventory-rows")));
		ib.setItem(40, new ItemBuilder(Material.WOOD_BUTTON, 1, 0).setName("&aChange the minimum-rewards").setLore("&7Current value:").addLore("&f" + getValue("Menu", "minimum-rewards")));
		ib.setItem(41, new ItemBuilder(Material.WOOD_BUTTON, 1, 0).setName("&aChange the maximum-rewards").setLore("&7Current value:").addLore("&f" + getValue("Menu", "maximum-rewards")));*/

		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		switch(slot)
		{
			case 18:
				up();
				break;
			case 0:
				cc.getCrateconfigFile().save();
				ChatUtils.msgSuccess(p, "CrateConfig.YML saved!");
				break;
			case 9:
				reload();
				break;
			case 11:
				new IGCAnimCSGO(cc, p, this).open();
				break;
			case 12:
				new IGCAnimRoulette(cc, p, this).open();
				break;
			case 13:
				new IGCAnimMenu(cc, p, this).open();
				break;
			case 14:
				new IGCAnimEnclose(cc, p, this).open();
				break;
			case 15:
				new IGCAnimDiscover(cc, p, this).open();
				break;
			case 16:
				new IGCAnimOpenChest(cc, p, this).open();
				break;
		}
		/*String val = "no current value";
		Object obj = Object.class;
		switch(slot)
		{
			case 0:
				cc.getCrateconfigFile().save();
				ChatUtils.msgSuccess(getP(), "Saved the Rewards.YML file.");
				return;
			case 9:
				getP().closeInventory();
				getCc().reload();
				return;
			case 27:
				up();
				return;
			case 2:
				val = "Roulette.inv-menu";
				obj = String.class;
				break;
			case 3:
				val = "Roulette.tick-sound";
				obj = String.class;
				break;
			case 4:
				val = "Roulette.tick-speed-per-run";
				obj = Double.class;
				break;
			case 5:
				val = "Roulette.final-crate-tick-length";
				obj = Double.class;
				break;
			case 6:
				new InputMenu(cc, p, "add Roulette.random-blocks", "Format: MATERIAL;ID", String.class, this);
				return;
			case 7:
				new InputMenu(cc, p, "remove Roulette.random-blocks", "Existing random-blocks: " + (cc.getCrateconfigFile().get().contains("CrateType.Inventory.Roulette.random-blocks") ? cc.getCrateconfigFile().get().getStringList("CrateType.Inventory.Roulette.random-blocks") : "none"), String.class, this);
				return;
			case 20:
				val = "CSGO.inv-name";
				obj = String.class;
				break;
			case 21:
				val = "CSGO.tick-sound";
				obj = String.class;
				break;
			case 22:
				val = "CSGO.tick-speed-per-run";
				obj = Double.class;
				break;
			case 23:
				val = "CSGO.final-crate-tick-length";
				obj = Double.class;
				break;
			case 24:
				new InputMenu(cc, p, "add CSGO.filler-blocks", "Format: MATERIAL;ID", String.class, this);
				return;
			case 25:
				new InputMenu(cc, p, "remove CSGO.filler-blocks", "Existing filler-blocks: " + (cc.getCrateconfigFile().get().contains(getPath("CSGO.filler-blocks")) ? cc.getCrateconfigFile().get().getStringList(getPath("CSGO.filler-blocks")) : "none"), String.class, this);
				return;
			case 26:
				val = "CSGO.identifier-block";
				obj = String.class;
				break;
			case 39:
				val = "Menu.inv-name";
				obj = String.class;
				break;
			case 40:
				val = "Menu.inventory-rows";
				obj = Integer.class;
				break;
			case 41:
				val = "Menu.minimum-rewards";
				obj = Integer.class;
				break;
			case 42:
				val = "Menu.maximum-rewards";
				obj = Integer.class;
				break;
		}

		if(!val.equalsIgnoreCase("no current value"))
		{
			String[] args = val.split("\\.");
			new InputMenu(cc, p, val, args[0], args[1], obj, this);
		}*/

	}

	@Override
	public boolean handleInput(String value, String input)
	{
		Object type = getInputMenu().getType();
		if(type == Double.class)
		{
			if(Utils.isDouble(input))
			{
				cc.getCrateconfigFile().get().set(getPath(value), Double.valueOf(input));
				ChatUtils.msgSuccess(p, "Set " + value + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(p, "This is not a valid decimal value, please try again.");
			}
		}
		else if(type == Integer.class)
		{
			if(Utils.isInt(input))
			{
				cc.getCrateconfigFile().get().set(getPath(value), Integer.parseInt(input));
				ChatUtils.msgSuccess(p, "Set " + value + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(p, "This is not a valid number, please try again.");
			}
		}
		else
		{
			if(value.equalsIgnoreCase("add Roulette.random-blocks") || value.equalsIgnoreCase("add CSGO.filler-blocks"))
			{
				try
				{
					String[] split = input.split(";");
					DynamicMaterial m = DynamicMaterial.fromString(input.toUpperCase());
					if(Utils.isInt(split[1]))
					{
						int id = Integer.parseInt(split[1]);
						List<String> currentList = cc.getCrateconfigFile().get().contains(getPath(value.substring(4))) ? cc.getCrateconfigFile().get().getStringList(getPath(value.substring(4))) : new ArrayList<String>();
						currentList.add(m.name() + ";" + id);
						cc.getCrateconfigFile().get().set(getPath(value.substring(4)), currentList);
						return true;
					}
					else
					{
						ChatUtils.msgError(getP(), split[1] + " is not a valid number.");
					}
				}
				catch(Exception exc)
				{
					ChatUtils.msgError(getP(), input + " does not have a valid material or is not formatted MATERIAL;DATA");
				}
			}
			else if(value.equalsIgnoreCase("remove Roulette.random-blocks") || value.equalsIgnoreCase("remove CSGO.filler-blocks"))
			{
				if(cc.getCrateconfigFile().get().contains(getPath(value.substring(7))))
				{
					boolean found = false;
					List<String> newList = new ArrayList<>();
					for(String s : cc.getCrateconfigFile().get().getStringList(getPath(value.substring(7))))
					{
						if(s.equalsIgnoreCase(input))
						{
							found = true;
						}
						else
						{
							newList.add(s);
						}
					}

					if(found)
					{
						ChatUtils.msgSuccess(getP(), "Removed the " + input + " value.");
						cc.getCrateconfigFile().get().set(getPath(value.substring(7)), newList);
						return true;
					}
					else
					{
						ChatUtils.msgError(getP(), input + " does not exist in the filler / random blocks: " + cc.getCrateconfigFile().get().getStringList(getPath(value.substring(7))));
					}
				}
				else
				{
					ChatUtils.msgError(getP(), "No filler blocks currently exist to remove.");
					return true;
				}
			}
			else
			{
				cc.getCrateconfigFile().get().set(getPath(value), input);
				ChatUtils.msgSuccess(p, "Set " + value + " to '" + input + "'");
				return true;
			}
		}
		return false;
	}

	public String getPath(String value)
	{
		return "CrateType.Inventory." + value;
	}

	public String getValue(String crateType, String value)
	{
		FileHandler fu = cc.getCrateconfigFile();
		FileConfiguration fc = fu.get();
		return fc.get("CrateType.Inventory." + crateType + "." + value).toString();
	}
}
