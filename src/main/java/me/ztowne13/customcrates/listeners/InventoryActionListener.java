package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.types.CrateHead;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.crates.types.animations.InvDiscover;
import me.ztowne13.customcrates.crates.types.animations.InvMenu;
import me.ztowne13.customcrates.crates.types.animations.dataholders.DiscoverDataHolder;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.nbt_utils.NBTTagManager;
import net.citizensnpcs.api.jnbt.NBTUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryActionListener implements Listener
{
	CustomCrates cc;
	
	public InventoryActionListener(CustomCrates cc)
	{
		this.cc = cc;
	}

	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{

		Bukkit.broadcastMessage("NBT Tags: " + NBTTagManager.getFrom(e.getCurrentItem()).toString());

		Player p = (Player) e.getWhoClicked();
		PlayerManager pm = PlayerManager.get(cc, p);
				
		if(pm.isInCrate() || pm.isInRewardMenu())
		{
			if(!(e.getClickedInventory() == null || e.getWhoClicked().getInventory() == null))
				if(!e.getClickedInventory().equals(e.getWhoClicked().getInventory()))
				{
					e.setCancelled(true);
				}

			if(pm.isInCrate() && pm.getOpenCrate().isMultiCrate())
			{
				Crate crate = pm.getOpenCrate();
				int slot = e.getSlot();

				crate.getCs().getCmci().checkClick(pm, slot, e.getClick());
			}
			else if(pm.isInCrate())
			{
				if(pm.getOpenCrate().getCs().getCt().equals(CrateType.INV_DISCOVER) && DiscoverDataHolder.getHolders().containsKey(p))
				{
					((InvDiscover)pm.getOpenCrate().getCs().getCh()).handleClick(DiscoverDataHolder.getHolders().get(p), e.getSlot());
				}
			}
		}
		
		if(pm.isWaitingForClose())
		{
			e.setCancelled(true);
			pm.closeCrate();
			for(Reward r: pm.getWaitingForClose())
			{
				r.runCommands(p);
			}
			pm.setWaitingForClose(null);
			p.closeInventory();
		}

		if(pm.isInOpenMenu())
		{
			if(!(e.getClickedInventory() == null || e.getView().getTopInventory() == null))
			{
				if (e.getClickedInventory().equals(e.getView().getTopInventory()))
				{
					e.setCancelled(true);
					try
					{
						pm.getOpenMenu().manageClick(e.getSlot());
					} catch (Exception exc)
					{
						exc.printStackTrace();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e)
	{
		final Player p = (Player) e.getPlayer();
		final PlayerManager pm = PlayerManager.get(cc, p);

		pm.setInRewardMenu(false);

		if(pm.isInOpenMenu() && !pm.getOpenMenu().isInInputMenu())
		{

			if(e.getInventory().getName().equalsIgnoreCase(ChatUtils.toChatColor("&7&l> &6&lClose to save")))
			{
				pm.getOpenMenu().manageClick(-1);
				ChatUtils.msgSuccess(p, "Successfully saved all rewards. Please go through and update all of their commands as well as their chance values.");
				Bukkit.getScheduler().runTaskLater(cc, new Runnable()
				{
					@Override
					public void run()
					{
						pm.getOpenMenu().up();
					}
				}, 1);
			}
			else
			{
				pm.setOpenMenu(null);
				Bukkit.getScheduler().runTaskLater(cc, new Runnable()
				{
					@Override
					public void run()
					{
						if (!pm.isInOpenMenu())
						{
							ChatUtils.msg(p, "&9&lNOTE: &bType &f'/sc !' &bto reopen to your last open config menu!");
						}
					}
				}, 1);
			}
		}

		if(pm.isWaitingForClose())
		{
			pm.closeCrate();
			for(Reward r: pm.getWaitingForClose())
			{
				r.runCommands(p);
			}
			pm.setWaitingForClose(null);
		}


		if(pm.isInCrate() || pm.isInRewardMenu())
		{
			CrateHead ch = pm.getOpenCrate().getCs().getCh();
			if(ch instanceof InvMenu)
			{
				ch.completeCrateRun(p);
				return;
			}
			else if(pm.getOpenCrate().isMultiCrate())
			{
				pm.closeCrate();
			}
		}

		// Prevents the player from opening the inventory
		if(!pm.isCanClose())
		{
			//e.getPlayer().openInventory(e.getView().getTopInventory());
			pm.setCanClose(true);

			try
			{
				e.getPlayer().openInventory(e.getView().getTopInventory());
			}
			catch(Exception exc)
			{

			}

			pm.setCanClose(false);
		}
	}
}
