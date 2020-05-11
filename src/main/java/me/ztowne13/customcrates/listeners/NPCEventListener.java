package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.crateaction.AttemptKeyUseAction;
import me.ztowne13.customcrates.crates.crateaction.LeftClickAction;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by ztowne13 on 2/26/16.
 */
public class NPCEventListener implements Listener
{
    SpecializedCrates cc;

    public NPCEventListener(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void onNPCClickRight(NPCRightClickEvent e)
    {
        if (new AttemptKeyUseAction(cc, e.getClicker(), e.getNPC().getStoredLocation()).run())
        {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onNPCClickLeft(NPCLeftClickEvent e)
    {
        Player p = e.getClicker();
        new LeftClickAction(cc, p, e.getNPC().getStoredLocation()).run();
    }
}
