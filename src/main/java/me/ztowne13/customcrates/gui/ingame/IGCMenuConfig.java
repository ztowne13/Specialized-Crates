package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.Settings;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.InventoryUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuConfig extends IGCMenu
{
	ArrayList<Integer> slotsWithBoolean = new ArrayList<Integer>();

	ItemBuilder red;
	ItemBuilder green;

	public IGCMenuConfig(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lConfig.YML");

		red = new ItemBuilder(DynamicMaterial.WOOL.toMaterial(), 1, 14);
		green = new ItemBuilder(DynamicMaterial.WOOL.toMaterial(), 1, 5);
	}

	@Override
	public void open()
	{
		p.closeInventory();
		putInMenu();

		HashMap<String, Object> map = getCc().getSettings().getConfigValues();
		InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(2, getCc().getSettings().getConfigValues().keySet().size()));

		ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
		ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
		ib.setItem(ib.getInv().getSize()-9, IGCDefaultItems.EXIT_BUTTON.getIb());

		int i = 2;

		String[] sortedObj = new String[map.keySet().size()];

		int back = 0;
		int forward = 0;
		for(String s: map.keySet())
		{
			Object val = map.get(s);
			if(val instanceof Boolean)
			{
				sortedObj[forward] = s;
				forward++;
			}
			else
			{
				back++;
				sortedObj[sortedObj.length-back] = s;
			}
		}

		for(String sv : sortedObj)
		{
			if(i % 9 == 0)
			{
				i += 2;
			}


			if(map.get(sv) instanceof Boolean)
			{
				ItemBuilder newBuilder = ((boolean) map.get(sv) ? green : red).setName("&a" + sv).setLore("&e&oCurrent value: " + map.get(sv)).addLore("");
				for(String lore : SettingsValues.getByPath(sv).getDescriptor())
				{
					newBuilder.addLore("&7" + lore);
				}
				ib.setItem(i, newBuilder);
				slotsWithBoolean.add(i);
			}
			else
			{
				boolean canBeUsed = !(map.get(sv) instanceof Collection);
				ItemBuilder newBuilder = new ItemBuilder(DynamicMaterial.WOOL.toMaterial(), 1, canBeUsed ? 1 : 8).setName((canBeUsed ? "&a" : "&c") + sv).setLore(canBeUsed ? "&e&oCurrent value: " + map.get(sv) : "&cThis value cannot currently be edited in game").addLore("");
				for(String lore : SettingsValues.getByPath(sv).getDescriptor())
				{
					newBuilder.addLore("&7" + lore);
				}
				ib.setItem(i, newBuilder);
			}
			i++;
		}


		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		Inventory inv = ib.getInv();
		if(slotsWithBoolean.contains(slot))
		{
			ItemBuilder newBuilder = new ItemBuilder(inv.getItem(slot));
			SettingsValues sv = SettingsValues.getByPath(newBuilder.getName(true));

			newBuilder.getStack().setDurability((byte) (newBuilder.getStack().getDurability() == 5 ? 14 : 5));

			getIb().setItem(slot, newBuilder);

			getCc().getSettings().getConfigValues().put(sv.getPath(), newBuilder.getStack().getDurability() == 5);
			open();
		}
		else if(inv.getItem(slot).getType().equals(DynamicMaterial.WOOL.toMaterial()))
		{
			ItemStack item = inv.getItem(slot);
			SettingsValues sv = SettingsValues.getByPath(new ItemBuilder(item).getName(true));

			if(item.getDurability() == 8)
			{
				ChatUtils.msgError(p, "This value cannot be configured in game.");
			}
			else
			{
				new InputMenu(cc, getP(), sv.getPath(), sv.getValue(cc).toString(), sv.getObj(), this);
			}
		}
		else
		{
			if(slot == 0)
			{
				cc.getSettings().writeSettingsValues();
				ChatUtils.msgSuccess(p, "Config.YML saved!");
			}
			else if(slot == 9)
			{
				reload();
			}
			else if(slot == ib.getInv().getSize()-9)
			{
				up();
			}
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		SettingsValues sv = SettingsValues.getByPath(value);
		Settings settings = cc.getSettings();
		String path = sv.getPath();

		if(sv.getObj() == String.class)
		{
			settings.getConfigValues().put(sv.getPath(), input);
			ChatUtils.msgSuccess(p, "Set " + path + " to '" + input + "'");
			return true;
		}
		else if(sv.getObj() == Integer.class)
		{
			if(Utils.isInt(input))
			{
				settings.getConfigValues().put(path, Integer.parseInt(input));
				ChatUtils.msgSuccess(p, "Set " + path + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(p, "This is not a valid number, please try again.");
			}
		}
		else if(sv.getObj() == Double.class)
		{
			if(Utils.isDouble(input))
			{
				settings.getConfigValues().put(path, input);
				ChatUtils.msgSuccess(p, "Set " + path + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(p, "This is not a valid decimal value, please try again.");
			}
		}

		return false;
	}
}
