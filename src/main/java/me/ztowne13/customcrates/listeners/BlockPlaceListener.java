package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.crateaction.AttemptCrateUseAction;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class BlockPlaceListener implements Listener
{
    SpecializedCrates cc;

    public BlockPlaceListener(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void onPlaceCrateKey(BlockPlaceEvent e)
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
                if(new AttemptCrateUseAction(cc, p, l, true).run())
                {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e)
    {
        Block b = e.getBlock();
        b.setMetadata("PLACED", new FixedMetadataValue(cc, "something"));
    }
}
