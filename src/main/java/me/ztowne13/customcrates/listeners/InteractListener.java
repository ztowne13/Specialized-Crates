package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.CrateAction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
	}


}
