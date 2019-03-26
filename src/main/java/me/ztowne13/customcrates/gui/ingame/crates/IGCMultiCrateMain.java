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
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/29/16.
 */
public class IGCMultiCrateMain extends IGCMenuCrate
{
	public IGCMultiCrateMain(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates)
	{
		super(cc, p, lastMenu, "&7&l> &6&lMultiCrates Main", crates);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(9);

		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(2, new ItemBuilder(Material.PAPER, 1, 0).setName("&aEdit the current GUI").setLore("&7Put the actual crate items in").addLore("&7the inventory where you want them to").addLore("&7display and put normal blocks in the ").addLore("&7inventory where you want them to").addLore("&7also display."));
		ib.setItem(4, new ItemBuilder(Material.BOOK, 1, 0).setName("&aEdit the amount of rows").setLore("&7Set the amount of rows").addLore("&7the inventory will have."));
		ib.setItem(6, new ItemBuilder(DynamicMaterial.RED_WOOL, 1).setName("&aClear the inventory").setLore("&4&lWARNING: &cThis clears the entire").addLore("&cinventory."));

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
			case 2:
				crates.getCs().getCmci().getInventory(getP(), "&c&lClose to save", true).open();
				ChatUtils.msgSuccess(getP(), "Close the inventory to save, if you don't want to save, type /scrates reload and all changes will be lost.");
				break;
			case 4:
				new InputMenu(getCc(), getP(), "set rows", (crates.getCs().getCmci().getInventory(getP(), "", false).getInv().getSize() / 9) + "", "Please use a number between 1 and 6", Integer.class, this);
				break;
			case 6:
				crates.getCs().getCmci().getInventory(getP(), "", false).getInv().clear();
				ChatUtils.msgSuccess(getP(), "You have cleared the inventory.");
				break;
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		if(value.equalsIgnoreCase("set rows"))
		{
			if(Utils.isInt(input))
			{
				InventoryBuilder oldIb = crates.getCs().getCmci().getInventory(getP(), "", false);
				InventoryBuilder newIb = new InventoryBuilder(getP(), Integer.parseInt(input)*9, oldIb.getInv().getName());

				for(int i = 0; i < (oldIb.getInv().getSize() < newIb.getInv().getSize() ? oldIb.getInv().getSize() : newIb.getInv().getSize()); i++)
				{
					if(!(oldIb.getInv().getItem(i) == null))
					{
						newIb.setItem(i, oldIb.getInv().getItem(i));
					}
				}
				crates.getCs().getCmci().setIb(newIb);
				ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), input + " is not a valid number.");
			}
		}
		return false;
	}
}
