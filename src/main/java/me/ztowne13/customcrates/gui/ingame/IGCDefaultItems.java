package me.ztowne13.customcrates.gui.ingame;

import me.ztowne13.customcrates.gui.ItemBuilder;
import org.bukkit.Material;

/**
 * Created by ztowne13 on 3/11/16.
 */
public enum IGCDefaultItems
{
	EXIT_BUTTON(new ItemBuilder(Material.INK_SACK, 1, 8).setName("&cExit").setLore("&4&oNOTE: THIS DOES NOT SAVE CHANGES").addLore("").addLore("&7Exit or return to the previous menu.")),

	SAVE_BUTTON(new ItemBuilder(Material.INK_SACK, 1, 10).setName("&aSave and reload").addLore("").addLore("&7Save your current changes and reload the plugin.")),

	SAVE_ONLY_BUTTON(new ItemBuilder(Material.INK_SACK, 1, 10).setName("&aSave it").setLore("&7Note, click reload to").addLore("&7see the changes.")),

	RELOAD_BUTTON(new ItemBuilder(Material.INK_SACK, 1, 9).setName("&aReload").setLore("&7This reloads the plugin").addLore("&4&lMAKE SURE YOU SAVE FIRST!"));

	ItemBuilder ib;

	IGCDefaultItems(ItemBuilder ib)
	{
		this.ib = ib;
	}

	public ItemBuilder getIb()
	{
		return ib;
	}

	public void setIb(ItemBuilder ib)
	{
		this.ib = ib;
	}
}
