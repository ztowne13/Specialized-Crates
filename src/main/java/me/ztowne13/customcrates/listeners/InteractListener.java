package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class InteractListener implements Listener
{
	CustomCrates cc;

	public InteractListener(CustomCrates cc)
	{
		this.cc = cc;
	}

	@EventHandler (priority = EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e)
	{
		try
		{
			if (!e.getHand().equals(EquipmentSlot.HAND))
				return;
		} catch(Throwable exc) {}

		if(!e.isCancelled())
		{
			Player p = e.getPlayer();

			if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
			{
				if (new CrateAction(cc, e.getAction().equals(Action.RIGHT_CLICK_BLOCK) ? CrateAction.Types.USE_KEY : CrateAction.Types.LEFT_CLICK).completeAction(p, e.getClickedBlock().getLocation()))
				{
					e.setCancelled(true);
				}
			}
		}

		// Prevent crate keys from being used for anything else (enderpearls not being throw, etc.)
		if(e.getItem() != null)
		{
			for(Crate crate : Crate.getLoadedCrates().values())
			{
				if(crate.keyMatchesToStack(e.getItem()))
				{
					e.setCancelled(true);
				}
			}
		}
	}


}
