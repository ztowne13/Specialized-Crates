package me.ztowne13.customcrates.gui.ingame.crateanimations;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
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
 * Created by ztowne13 on 7/6/16.
 *
 *	 inv-name: '&8&l> &6&l%crate%'
 *	 inventory-rows: 3
 *	 minimum-rewards: 1
 *	 maximum-rewards: 8
 */
public class IGCAnimMenu extends IGCAnimation
{
	public IGCAnimMenu(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lMenu Animation", CrateType.INV_MENU);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(27);


		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(11, new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").addLore(getcVal() + getString("inv-name")));
		ib.setItem(13, new ItemBuilder(Material.PAPER, 1, 0).setName("&ainventory-rows").addLore(getcVal() + getString("inventory-rows")));
		ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&aminimum-rewards").addLore(getcVal() + getString("minimum-rewards")));
		ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&amaximum-rewards").addLore(getcVal() + getString("maximum-rewards")));

		getIb().open();
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
				new InputMenu(getCc(), getP(), "inv-name", getString("inv-name"), "The name of the inventory", String.class, this);
				break;
			case 13:
				new InputMenu(getCc(), getP(), "inventory-rows", getString("inventory-rows"), "How many rows the menu crate has.", Integer.class, this);
				break;
			case 14:
				new InputMenu(getCc(), getP(), "minimum-rewards", getString("minimum-rewards"), "The low end of the random amount of rewards that will spawn.", Integer.class, this);
				break;
			case 15:
				new InputMenu(getCc(), getP(), "maximum-rewards", getString("maximum-rewards"), "The high end of the random amount of rewards that will spawn.", Integer.class, this);
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
		else
		{
			fc.set(getPath(value), input);
			ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
			return true;
		}
		return false;
	}
}
