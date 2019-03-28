package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener
{
    CustomCrates cc;

    public BlockBreakListener(CustomCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e)
    {
        Player p = e.getPlayer();
        Location l = e.getBlock().getLocation();

        if (PlacedCrate.crateExistsAt(cc, l))
        {
            PlacedCrate cm = PlacedCrate.get(cc, l);
            Crate crates = cm.getCrates();

            if (crates.getCs().getOt().isStatic())
            {
                if (!p.hasPermission("customcrates.deletestatic"))
                {
                    e.setCancelled(true);
                    Messages.FAILED_BREAK_CRATE.msgSpecified(cc, p, new String[]{"%crate%", "%reason%"},
                            new String[]{crates.getName(), "static"});
                }
                return;
            }

            cm.delete();
            Messages.BROKEN_CRATE.msgSpecified(cc, p, new String[]{"%crate%"}, new String[]{crates.getName()});
        }
        else
        {
            if (!e.isCancelled())
            {
                if (!p.getGameMode().equals(GameMode.CREATIVE) || (Boolean) SettingsValues.LUCKYCHEST_CREATIVE.getValue(cc))
                {
                    if (PlayerManager.get(cc, p).getPdm().isActivatedLuckyChests())
                    {
                        for (Crate crates : Crate.getLoadedCrates().values())
                        {
                            if (CrateUtils.isCrateUsable(crates) && crates.getCs().clcExists() &&
                                    crates.getCs().getOt().equals(ObtainType.LUCKYCHEST))
                            {
                                if (crates.getCs().getClc().checkRun(e.getBlock()))
                                {
                                    PlacedCrate cm = PlacedCrate.get(cc, e.getBlock().getLocation());
                                    cm.setup(crates, true);
                                    Messages.FOUND_LUCKY_CHEST.msgSpecified(cc, p);
                                    e.setCancelled(true);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
