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
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 4/2/16.
 *
 * 4:32
 */
public class IGCCrateHolograms extends IGCMenuCrate
{
	public IGCCrateHolograms(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates)
	{
		super(cc, p, lastMenu, "&7&l> &6&lHolograms", crates);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(18);

		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(8, new ItemBuilder(Material.PAPER, 1, 0).setName("&aAdd a line to the hologram"));
		ib.setItem(9, new ItemBuilder(DynamicMaterial.WRITABLE_BOOK, 1).setName("&aSet reward-hologram").setLore("&7Current value:").addLore("&f" + cs.getCholoCopy().getRewardHologram()).addLore("").addLore("&7This is the hologram that").addLore("&7appears when someone wins").addLore("&7a reward."));
		ib.setItem(17, new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&aTo remove a line, edit a").setLore("&aline and set it to 'delete'").addLore("&7&owithout the quotes"));

		if(cs.getCholoCopy().getLines() != null)
		{
			int lineNum = 0;
			for (int i = 2; lineNum < cs.getCholoCopy().getLines().length; i++)
			{
				if (i % 9 == 7)
				{
					i += 4;
				}

				if (!(cs.getCholoCopy().getLines()[lineNum] == null))
				{
					String holo = cs.getCholoCopy().getLines()[lineNum];
					ItemBuilder itemBuilder = new ItemBuilder(Material.BOOK, 1, 0);
					itemBuilder.setName("&aLine " + (lineNum + 1)).setLore("&7Line value: ").addLore("&f" + holo);
					ib.setItem(i, itemBuilder);

					lineNum++;
				}
				else
				{
					break;
				}
			}
		}
		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		if(slot == 0)
		{
			up();
		}
		else if(slot == 9)
		{
			new InputMenu(getCc(), getP(), "reward-hologram", crates.getCs().getCholoCopy().getRewardHologram(), "Type 'none' to remove the reward hologram. Use %reward% as a placeholder for the reward.", String.class, this);
		}
		else if(slot == 8)
		{
			if(!(cs.getCholoCopy().getLineCount() >= 10))
			{
				new InputMenu(getCc(), getP(), "new line", "null", String.class, this);
			}
			else
			{
				getIb().setItem(slot, new ItemBuilder(getIb().getInv().getItem(slot)).setName("&cDenied").setLore("&7You can't have more than").addLore("&710 hologram lines, try").addLore("&7deleting one."));
			}
		}
		else if(getIb().getInv().getItem(slot) != null && getIb().getInv().getItem(slot).getType().equals(Material.BOOK))
		{
			int lineNum = Integer.parseInt(ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName()).split(" ")[1]);
			new InputMenu(getCc(), getP(), "edit line " + lineNum, cs.getCholoCopy().getLines()[lineNum-1], "Type 'delete' without the quotes to delete the line.", String.class, this);
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		if(value.equalsIgnoreCase("new line"))
		{
			cs.getCholoCopy().addLine(input);
			ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
			return true;
		}
		else if(value.startsWith("edit line"))
		{
			int lineNum = Integer.parseInt(value.split(" ")[2])-1;
			if(input.equalsIgnoreCase("delete"))
			{
				cs.getCholoCopy().removeLine(lineNum);
				ChatUtils.msgSuccess(getP(), "Deleted line " + lineNum);
			}
			else
			{
				cs.getCholoCopy().getLines()[lineNum] = input;
				ChatUtils.msgSuccess(getP(), "Set " + value.substring(4) + " to " + input);
			}
			return true;
		}
		else if(value.equalsIgnoreCase("reward-hologram"))
		{
			if(input.equalsIgnoreCase("none"))
			{
				cs.getCholoCopy().setRewardHologram("");
				return true;
			}
			cs.getCholoCopy().setRewardHologram(input);
			ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
			return true;
		}
		return false;
	}
}
