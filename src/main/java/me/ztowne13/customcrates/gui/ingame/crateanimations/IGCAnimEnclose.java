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
 *	inv-name: '&8&l> &6&l%crate%'
 *	inventory-rows: 2
 *	fill-block: STAINED_GLASS_PANE;1
 *	tick-sound: ENTITY_PLAYER_BIG_FALL, 5, 5
 *	update-speed: 5
*	reward-amount: 1
 */
public class IGCAnimEnclose extends IGCAnimation
{
	public IGCAnimEnclose(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lEnclose Animation", CrateType.INV_ENCLOSE);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(36);


		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(11, new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").addLore(getcVal() + getString("inv-name")));
		ib.setItem(13, new ItemBuilder(Material.PAPER, 1, 0).setName("&ainventory-rows").addLore(getcVal() + getString("inventory-rows")));
		ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&aupdate-speed").addLore(getcVal() + getString("update-speed")));
		ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&areward-amount").addLore(getcVal() + getString("reward-amount")));
		ib.setItem(20, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&atick-sound").addLore(getcVal() + getString("tick-sound")));
		ib.setItem(23, new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&afill-block").addLore(getcVal() + getString("fill-block")));

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
				new InputMenu(getCc(), getP(), "inventory-rows", getString("inventory-rows"), "How many rows the menu crate has (1 is 1 row up AND down).", Integer.class, this);
				break;
			case 14:
				new InputMenu(getCc(), getP(), "update-speed", getString("minimum-rewards"), "How fast each reward disappears (in ticks).", Integer.class, this);
				break;
			case 15:
				new InputMenu(getCc(), getP(), "reward-amount", getString("maximum-rewards"), "The amount of rewards that are left displayed and given to the player (must be an odd number).", Integer.class, this);
				break;
			case 20:
				new InputMenu(getCc(), getP(), "tick-sound", getString("tick-sound"), "Formatted: SOUND, PITCH, VOLUME. The sound played on every update.", String.class, this);
				break;
			case 23:
				new InputMenu(getCc(), getP(), "fill-block", getString("fill-block"), "Formatted: MATERIAL;DURABILITY. The block that fills the empty spots", String.class, this);
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
