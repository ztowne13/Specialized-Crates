package me.ztowne13.customcrates.gui.ingame.rewards;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ztowne13 on 8/7/16.
 */
public class IGCDragAndDrop extends IGCMenu
{
	public IGCDragAndDrop(CustomCrates cc, Player p, IGCMenu lastMenu)
	{
		super(cc, p, lastMenu, "&7&l> &6&lClose to save");
	}

	@Override
	public void open()
	{
		getP().closeInventory();
		putInMenu();

		InventoryBuilder ib = createDefault(54);

		getIb().open();
	}

	@Override // For this menu it has been repurposed to be used as the "save" function
	public void manageClick(int slot)
	{
		getCc().getDu().log("IGCDragAndDrop.manageClick");
		for(ItemStack stack : getIb().getInv().getContents())
		{
			String rewardName = getNameFor(stack);
			getCc().getDu().log("stack type: " + stack.getType().name());
			getCc().getDu().log("rewardName: " + rewardName);
			Reward r = new Reward(getCc(), rewardName);
			String displayName = stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : stack.getType().name().toLowerCase().replaceAll("_", " ");
			getCc().getDu().log("displayName: " + displayName);
			r.setDisplayName(displayName);
			r.setChance(10);
			r.setDisplayItem(stack);
			r.setNeedsMoreConfig(false);
		}
		getIb().getInv().clear();
	}

	@Override
	public boolean handleInput(String value, String input)
	{
		return false;
	}

	public String getNameFor(ItemStack stack)
	{
		String baseName = ChatUtils.removeColor(stack.hasItemMeta() && stack.getItemMeta().hasDisplayName() ? stack.getItemMeta().getDisplayName() : stack.getType().name().toLowerCase().replaceAll("_", " "));
		int i = 0;
		while(true)
		{
			String name = baseName + (i == 0 ? "" : i);
			if (CRewards.rewardNameExists(getCc(), name))
			{
				return name;
			}
			i++;
		}
	}
}
