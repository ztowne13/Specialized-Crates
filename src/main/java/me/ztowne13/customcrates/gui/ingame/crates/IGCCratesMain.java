package me.ztowne13.customcrates.gui.ingame.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ztowne13 on 3/26/16.
 */
public class IGCCratesMain extends IGCMenuCrate
{
	public IGCCratesMain(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates)
	{
		super(cc, p, lastMenu, "&7&l> &6&lCrate Main", crates);
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(crates.isMultiCrate() ? 27 : 45);

		ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
		ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
		ib.setItem(crates.isMultiCrate() ? 18 : 36, IGCDefaultItems.EXIT_BUTTON.getIb());
		ib.setItem(3, new ItemBuilder(Material.INK_SACK, 1, 1).setName("&4&lDelete this crate").setLore("&cThis action CANNOT be undone.").addLore("&e&oNote: This does not delete rewards").addLore("").addLore("&7This will delete the entire").addLore("&7file for this crate and will").addLore("&7erase all data for it."));
		//ib.setItem(5, new ItemBuilder(Material.BOOK_AND_QUILL, 1, 0).setName("&aRename this crate").setLore("&cNOTE: &7This will remove all").addLore("&7placed crates of this type."));

		ib.setItem(8, new ItemBuilder(Material.CARPET, 1, 14).setName("&aDelete all placed instances").setLore("&7Click this runs the command:").addLore("&7/ccrates delallcratetype " + crates.getName()).addLore("").addLore("&7It deletes all crates").addLore("&7of this type that have been").addLore("&7placed."));
		ib.setItem(4, new ItemBuilder(Material.CHEST, 1, 0).setName("&a" + crates.getName()));

		ib.setItem(10, new ItemBuilder(Material.STONE_BUTTON, 1, 0).setName("&aThe Defaults").setLore("&7This includes things such as").addLore("&7crate / key material,").addLore("&7crate-animation, obtain methods").addLore("&7and more"));
		ib.setItem(16, new ItemBuilder(Material.NETHER_STAR, 1, 0).setName("&aParticles").setLore("&7Modify particles for play").addLore("&7and open use."));
		ib.setItem(21, new ItemBuilder(Material.BOOK, 1, 0).setName("&aHolograms").setLore("&7Modify the holograms."));

		if(!crates.isMultiCrate())
		{
			ib.setItem(23, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&aSounds").setLore("&7Modify the sounds for when").addLore("&7the crate is opened and").addLore("&7reward is given."));
			ib.setItem(28, new ItemBuilder(Material.FIREWORK, 1, 0).setName("&aFireworks").setLore("&7Modify the fireworks."));
			ib.setItem(34, new ItemBuilder(Material.PAPER, 1, 0).setName("&aActions").setLore("&7Modify messages, broadcasts,").addLore("&7titles, subtitles, and").addLore("&7actionbars."));
			ib.setItem(40, new ItemBuilder(Material.INK_SACK, 1, 12).setName("&aRewards").setLore("&7Add and remove rewards that").addLore("&7players will receive from").addLore("&7this crate."));
		}
		else
		{
			ib.setItem(23, new ItemBuilder(Material.LADDER, 1, 0).setName("&aMultiCrate Values").setLore("&7Modify the multicrate inventory and").addLore("&7other values."));
		}

		if(cs.getOt().equals(ObtainType.LUCKYCHEST))
		{
			ib.setItem(crates.isMultiCrate() ? 35 : 44, new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&aMine Chest").setLore("&7Config all values for the").addLore("&7mine chest."));
		}

		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		if(crates.isMultiCrate() && (slot == 28 || slot == 34 || slot == 40))
		{
			return;
		}

		switch(slot)
		{
			case 0:
				if(!crates.isMultiCrate() && (cs.getCr().getCrateRewards() == null || cs.getCr().getCrateRewards().length == 0))
				{
					getIb().setItem(slot, new ItemBuilder(getIb().getInv().getItem(slot)).setName("&cPlease add some rewards").setLore("&cbefore saving!"));
				}
				else
				{
					try
					{
						crates.getCs().saveAll();
						ChatUtils.msgSuccess(getP(), "Saved the file!");
					}
					catch(Exception exc)
					{
						exc.printStackTrace();
						ChatUtils.msgError(getP(), "Failed to save the file!");
					}
				}
				break;
			case 3:
				if(ChatUtils.removeColor(getIb().getInv().getItem(3).getItemMeta().getDisplayName()).equalsIgnoreCase("Delete this crate"))
				{
					getIb().setItem(3, new ItemBuilder(Material.INK_SACK, 1, 1).setName("&6CONFIRM DELETE").setLore("&4&lTHIS CANNOT BE UNDONE!"));
				}
				else
				{
					getP().closeInventory();
					ChatUtils.msg(getP(), "&6&lNote: &eDeleting...");
					String path = crates.getCs().deleteCrate();
					ChatUtils.msgSuccess(getP(), "Successfully deleted file on path &e" + path);
				}
				break;
			case 5:
				new InputMenu(getCc(), getP(), "Crate name", crates.getName(), "Do not use ANY spaces or special characters", String.class, this);
				break;
			case 9:
				reload();
				break;
			case 18:
				if(!crates.isMultiCrate())
				{
					return;
				}
			case 36:
				if(getLastMenu() == null)
				{
					getP().closeInventory();
					break;
				}
				up();
				break;
			case 8:
				getP().chat("/ccrates delallcratetype " + crates.getName());
				break;
			case 10:
				getP().closeInventory();
				new IGCCratesBase(getCc(), getP(), this, crates).open();
				break;
			case 16:
				getP().closeInventory();
				Set<String> blankParticles = new HashSet<String>();
				blankParticles.add("PLAY");
				if(!crates.isMultiCrate())
				{
					blankParticles.add("OPEN");
				}
				new IGCTierSelector(getCc(), getP(), this, crates, blankParticles, new IGCCrateParticles(getCc(), getP(), this, crates, "")).open();
				break;
			case 21:
				getP().closeInventory();
				new IGCCrateHolograms(getCc(), getP(), this, crates).open();
				break;
			case 23:
				if(crates.isMultiCrate())
				{
					getP().closeInventory();
					new IGCMultiCrateMain(getCc(), getP(), this, crates).open();
				}
				else
				{
					getP().closeInventory();
					Set<String> blankSounds = new HashSet<String>();
					blankSounds.add("OPEN");
					new IGCTierSelector(getCc(), getP(), this, crates, blankSounds, new IGCCrateSounds(getCc(), getP(), this, crates, "")).open();
				}
				break;
			case 28:
				getP().closeInventory();
				Set<String> blankFireworks = new HashSet<String>();
				blankFireworks.add("OPEN");
				new IGCTierSelector(getCc(), getP(), this, crates, blankFireworks, new IGCCrateFireworks(getCc(), getP(), this, crates, "")).open();
				break;
			case 34:
				Set<String> blankActions = new HashSet<String>();
				blankActions.add("DEFAULT");
				new IGCTierSelector(getCc(), getP(), this, crates, blankActions, new IGCCrateActions(getCc(), getP(), this, crates, "")).open();
				break;
			case 35:
				if(!crates.isMultiCrate())
				{
					return;
				}
			case 40:
				new IGCCrateRewards(getCc(), getP(), this, crates, 1).open();
				break;
			case 44:
				if(cs.getOt().equals(ObtainType.LUCKYCHEST))
				{
					new IGCCrateLuckyChest(getCc(), getP(), this, crates).open();
				}
				break;
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		/*if(value.equalsIgnoreCase("crate name"))
		{
			if(StringUtils.isAlphanumeric(input))
			{
				//crates.getCs().rename(getP(), input, crates.isMultiCrate() ? ".multicrate" : ".crate");
				return false;
			}
			ChatUtils.msgError(getP(), input + " is not alphanumeric (no spaces and only letters)");
		}*/
		return false;
	}
}
