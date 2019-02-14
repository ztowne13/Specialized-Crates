package me.ztowne13.customcrates.gui.ingame.crateanimations;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class IGCAnimDiscover extends IGCAnimation
{
	public IGCAnimDiscover(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lDiscover Animation", CrateType.INV_DISCOVER);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(36);

		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

		ib.setItem(11, new ItemBuilder(Material.BOOK , 1, 0).setName("&ainv-name").setLore(getcVal() + getString("inv-name")));
		ib.setItem(13, new ItemBuilder(Material.PAPER , 1, 0).setName("&ainventory-rows").setLore(getcVal() + getString("inventory-rows")));
		ib.setItem(14, new ItemBuilder(Material.PAPER , 1, 0).setName("&aminimum-rewards").setLore(getcVal() + getString("minimum-rewards")));
		ib.setItem(15, new ItemBuilder(Material.PAPER , 1, 0).setName("&amaximum-rewards").setLore(getcVal() + getString("maximum-rewards")));
		ib.setItem(16, new ItemBuilder(Material.PAPER , 1, 0).setName("&arandom-display-duration").setLore(getcVal() + getString("random-display-duration")));

		boolean b = true;
		try
		{
			b = Boolean.valueOf(getString("count"));
		}
		catch(Exception exc)
		{

		}
		ib.setItem(20, new ItemBuilder(b ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1).setName("&acount").setLore(getcVal() + b).addLore("").addLore("&7Do the 'cover-block's display numbers?"));
		ib.setItem(21, new ItemBuilder(Material.ENDER_CHEST , 1, 0).setName("&acover-block").setLore(getcVal() + getString("cover-block")));

		ib.setItem(23, new ItemBuilder(Material.NOTE_BLOCK , 1, 0).setName("&atick-sound").setLore(getcVal() + getString("tick-sound")));
		ib.setItem(24, new ItemBuilder(Material.NOTE_BLOCK , 1, 0).setName("&aclick-sound").setLore(getcVal() + getString("click-sound")));
		ib.setItem(25, new ItemBuilder(Material.NOTE_BLOCK , 1, 0).setName("&auncover-sound").setLore(getcVal() + getString("uncover-sound")));

		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		switch(slot)
		{
			case 0:
				up();
				break;
			case 11:
				new InputMenu(getCc(), getP(), "inv-name", getString("inv-name"), String.class, this);
				break;
			case 13:
				new InputMenu(getCc(), getP(), "inventory-rows", getString("inventory-rows"), Integer.class, this);
				break;
			case 14:
				new InputMenu(getCc(), getP(), "minimum-rewards", getString("minimum-rewards"), Integer.class, this);
				break;
			case 15:
				new InputMenu(getCc(), getP(), "maximum-rewards", getString("maximum-rewards"), Integer.class, this);
				break;
			case 16:
				new InputMenu(getCc(), getP(), "random-display-duration", getString("random-display-duration"), "How many ticks the random display of green grass plane will run for.", Integer.class, this);
				break;
			case 20:
				boolean b = !Boolean.valueOf(getString("count"));
				fc.set(getPath("count"), b);
				getIb().setItem(20, new ItemBuilder(b ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1).setName("&acount").setLore(getcVal() + b).addLore("").addLore("&7Do the 'cover-block's display numbers?"));
				break;
			case 21:
				new InputMenu(getCc(), getP(), "cover-block", getString("cover-block"), "Formatted: MATERIAL;DURABILITY", String.class, this);
				break;
			case 23:
				new InputMenu(getCc(), getP(), "tick-sound", getString("ticks-sound"), "Formatted: SOUND, VOLUME, PITCH", String.class, this);
				break;
			case 24:
				new InputMenu(getCc(), getP(), "click-sound", getString("click-sound"), "Formatted: SOUND, VOLUME, PITCH", String.class, this);
				break;
			case 25:
				new InputMenu(getCc(), getP(), "uncover-sound", getString("uncover-sound"), "Formatted: SOUND, VOLUME, PITCH", String.class, this);
				break;
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		Object type = getInputMenu().getType();
		if(type == Integer.class)
		{
			if(Utils.isInt(input))
			{
				fc.set(getPath(value), Integer.parseInt(input));
				ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
			}
		}
		else if(type == Boolean.class)
		{
			if(Utils.isBoolean(input))
			{
				fc.set(getPath(value), Boolean.parseBoolean(input));
				ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), "This is not a valid true / false value, please try again.");
			}
		}
		else
		{
			fc.set(getPath(value), input);
			ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
			return true;
		}
		return false;
	}
}
