package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.crates.CrateAction;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener
{
    CustomCrates cc;

    public BlockPlaceListener(CustomCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        Location l = e.getBlock().getLocation();

        if (!e.isCancelled())
        {
            if (CrateUtils.searchByKey(e.getItemInHand()) != null)
            {
                Messages.DENY_PLACE_KEY.msgSpecified(cc, p);
                e.setCancelled(true);
            }
            else
            {
                new CrateAction(cc, CrateAction.Types.USE_CRATE).completeAction(p, l);
            }
        }
    }
}
