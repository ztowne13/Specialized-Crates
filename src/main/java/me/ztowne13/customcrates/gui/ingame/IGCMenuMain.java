package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.ingame.rewards.IGCMenuRewards;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuMain extends IGCMenu
{
	public IGCMenuMain(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lHome");
	}

	@Override
	public void open()
	{
		putInMenu();

		InventoryBuilder ib = createDefault(27);
		ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

		ItemBuilder paper = new ItemBuilder(Material.PAPER, 1, 0);

		paper.setName("&aConfig.YML").setLore("").addLore("&7Amount of values: &f" + cc.getSettings().getConfigValues().keySet().size()).addLore("").addLore("&7Config values from the Config.YML");
		ib.setItem(11, paper);

		paper.setName("&aCrateConfig.YML").setLore("").addLore("&7Amount of animations: &f" + CrateType.values().length).addLore("").addLore("&7Config values from the CrateConfig.YML");
		ib.setItem(12, paper);

		paper.setName("&aRewards.YML").setLore("").addLore("&7Amount of rewards: &f" + CRewards.allRewards.keySet().size()).addLore("").addLore("&7Config values from the Rewards.YML");
		ib.setItem(13, paper);

		paper.setName("&aMessages.YML").setLore("").addLore("&7Amount of messages: &f" + (Messages.values().length - 5)).addLore("").addLore("&7Config values from the Messages.YML");
		ib.setItem(14, paper);

		ib.setItem(16, new ItemBuilder(Material.CHEST, 1, 0).setName("&aCrates").setLore("").addLore("&7Amount: &f" + Crate.getLoadedCrates().keySet().size()).addLore("&7Amount Placed: &f" + PlacedCrate.getPlacedCrates().keySet().size()));
 		ib.open();
	}

	@Override
	public void manageClick(int slot)
	{
		switch(slot)
		{
			case 9:
				p.closeInventory();
				break;
			case 11:
				new IGCMenuConfig(cc, p, this).open();
				break;
			case 12:
				new IGCMenuCrateConfig(cc, p, this).open();
				break;
			case 13:
				new IGCMenuRewards(cc, p, this, 1).open();
				break;
			case 14:
				new IGCMenuMessages(cc, p, this).open();
				break;
			case 16:
				new IGCMenuCrates(cc, p, this).open();
				break;
		}
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		return false;
	}
}
