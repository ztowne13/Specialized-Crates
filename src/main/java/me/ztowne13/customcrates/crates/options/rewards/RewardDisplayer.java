package me.ztowne13.customcrates.crates.options.rewards;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public class RewardDisplayer 
{
	Crate crates;
	
	public RewardDisplayer(Crate crates)
	{
		this.crates = crates;
	}
	
	public void openFor(Player p)
	{
		p.openInventory(createInventory(p).getInv());
		PlayerManager.get(getCrates().getCc(), p).setInRewardMenu(true);
	}
	
	public InventoryBuilder createInventory(Player p)
	{
		CRewards cr = getCrates().getCs().getCr();
		int amount = cr.getCrateRewards().length;
		int rows = amount % 9 == 0 ? amount/9 : (amount/9)+1;
		InventoryBuilder ib = new InventoryBuilder(p, rows*9, getInvName());
		Reward[] crewards = cr.getCrateRewards();
		int i = 0;
		for(Reward r: crewards)
		{
			ib.setItem(i, r.getDisplayItem());
			i++;
		}
		return ib;
	}
		
	public String getInvName()
	{
		return ChatUtils.toChatColor(getCrates().getCc().getSettings().getConfigValues().get("inv-reward-display-name").toString().replace("%crate%", getCrates().getName()));
	}

	public Crate getCrates()
	{
		return crates;
	}

	public void setCrates(Crate crates)
	{
		this.crates = crates;
	}
	
}
