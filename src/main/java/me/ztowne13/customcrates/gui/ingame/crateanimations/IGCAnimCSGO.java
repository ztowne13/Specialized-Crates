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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 7/6/16.
 *
 *	inv-name: '&8&l> &6&l%crate%'
 *	tick-sound: ENTITY_PLAYER_BIG_FALL, 5, 5
 *	identifier-block: REDSTONE_TORCH_ON;0
 *	tick-speed-per-run: 3
 *	final-crate-tick-length: 11
 *	tile-update-ticks: 2
 *	close-speed: 3
 *	filler-blocks:
 *	  - STAINED_GLASS_PANE;1
 *	  - STAINED_GLASS_PANE;2
 *	  - STAINED_GLASS_PANE;3
 *	  - STAINED_GLASS_PANE;4
 *	  - STAINED_GLASS_PANE;5
 *	  - STAINED_GLASS_PANE;6
 *	  - STAINED_GLASS_PANE;7
 *	  - STAINED_GLASS_PANE;8
 *	  - STAINED_GLASS_PANE;9
 *
 */
public class IGCAnimCSGO extends IGCAnimation
{
	public IGCAnimCSGO(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lCSGO Animation", CrateType.INV_CSGO);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(36);


		ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(11, new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").addLore(getcVal() + getString("inv-name")));
		ib.setItem(13, new ItemBuilder(Material.PAPER, 1, 0).setName("&atick-speed-per-run").addLore(getcVal() + getString("tick-speed-per-run")));
		ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&afinal-crate-tick-length").addLore(getcVal() + getString("final-crate-tick-length")));
		ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&atile-update-ticks").addLore(getcVal() + getString("tile-update-ticks")));
		ib.setItem(16, new ItemBuilder(Material.PAPER, 1, 0).setName("&aclose-speed").addLore(getcVal() + getString("close-speed")));
		ib.setItem(20, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&atick-sound").addLore(getcVal() + getString("tick-sound")));
		ib.setItem(21, new ItemBuilder(DynamicMaterial.REDSTONE_TORCH, 1).setName("&aidentifier-block").addLore(getcVal() + getString("identifier-block")));

		ItemBuilder fillerBlocks = new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&aAdd new filler-blocks").setLore("&7Current values: ");
		for(String s: fc.getStringList(getPath("filler-blocks")))
		{
			fillerBlocks.addLore("&7- &f" + s);
		}

		ib.setItem(23, fillerBlocks);
		ib.setItem(24, fillerBlocks.setName("&aRemove existing filler-blocks"));

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
				new InputMenu(getCc(), getP(), "tick-speed-per-run", getString("tick-speed-per-run"), "How fast the animation updates the rewards.", Double.class, this);
				break;
			case 14:
				new InputMenu(getCc(), getP(), "final-crate-tick-length", getString("final-crate-tick-length"), "How long the animation will display.", Double.class, this);
				break;
			case 15:
				new InputMenu(getCc(), getP(), "tile-update-ticks", getString("tile-update-ticks"), "How fast, in ticks, the filler-blocks will update", Integer.class, this);
				break;
			case 16:
				new InputMenu(getCc(), getP(), "close-speed", getString("close-speed"), "How fast the, neat, 'close' animation will display. Set to -1 to not have a 'close' animation.", Integer.class, this);
				break;
			case 20:
				new InputMenu(getCc(), getP(), "tick-sound", getString("tick-sound"), "Formatted SOUND, PITCH, VOLUME. Click for a list of sounds -> https://www.spigotmc.org/wiki/cc-sounds-list/", String.class, this);
				break;
			case 21:
				new InputMenu(getCc(), getP(), "identifier-block", getString("identifier-block"), "The block, formatted MATERIAL;ID, that identifies the middle block.", String.class, this);
				break;
			case 23:
				new InputMenu(getCc(), getP(), "add filler-blocks", "Formated: MATERIAL;ID", String.class, this);
				break;
			case 24:
				new InputMenu(getCc(), getP(), "remove filler-blocks", "Existing filler-blocks: " + (fc.contains(crateType.getPrefix() + ".filler-blocks") ? fc.getStringList(crateType.getPrefix() + ".filler-blocks") : "none"), String.class, this);
				break;
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		Object type = getInputMenu().getType();
		if(type == Double.class)
		{
			if(Utils.isDouble(input))
			{
				fc.set(getPath(value), Double.valueOf(input));
				ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
				return true;
			}
			else
			{
				ChatUtils.msgError(getP(), "This is not a valid decimal value, please try again.");
			}
		}
		else if(type == Integer.class)
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
			if(value.equalsIgnoreCase("add filler-blocks"))
			{
				try
				{
					String[] split = input.split(";");
					Material m = Material.valueOf(split[0].toUpperCase());
					if(Utils.isInt(split[1]))
					{
						int id = Integer.parseInt(split[1]);
						List<String> currentList = fc.contains(getPath(value.substring(4))) ? fc.getStringList(getPath(value.substring(4))) : new ArrayList<String>();
						currentList.add(m.name() + ";" + id);
						fc.set(getPath(value.substring(4)), currentList);
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
			else if(value.equalsIgnoreCase("remove filler-blocks"))
			{
				if(fc.contains(getPath(value.substring(7))))
				{
					boolean found = false;
					List<String> newList = new ArrayList<>();
					for(String s : fc.getStringList(getPath(value.substring(7))))
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
						fc.set(getPath(value.substring(7)), newList);
						return true;
					}
					else
					{
						ChatUtils.msgError(getP(), input + " does not exist in the filler / random blocks: " + fc.getStringList(getPath(value.substring(7))));
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
				fc.set(getPath(value), input);
				ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
				return true;
			}
		}
		return false;
	}
}
