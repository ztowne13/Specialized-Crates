package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
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
    CustomCrates cc;

    public NPCEventListener(CustomCrates cc)
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

    @EventHandler
    public void onNPCClickLeft(NPCLeftClickEvent e)
    {
        Player p = e.getClicker();
        new LeftClickAction(cc, p, e.getNPC().getStoredLocation());

    }

    /**if (PlacedCrate.crateExistsAt(cc, e.getNPC().getStoredLocation()))
     {
     PlacedCrate cm = PlacedCrate.get(cc, e.getNPC().getStoredLocation());
     if (CrateUtils.isCrateUsable(cm))
     {
     if (pm.isDeleteCrate())
     {
     cm.getCrates().getCs().getDcp().remove(cm);
     cm.delete();
     pm.setDeleteCrate(false);
     Messages.SUCCESS_DELETE.msgSpecified(cc, p, new String[]{"%crate%"}, new String[]{cm.getCrates().getName()});
     }
     else if ((Boolean) SettingsValues.REWARD_DISPLAY_ENABLED.getValue(cc))
     {
     new RewardDisplayer(cm.getCrates()).openFor(p);
     e.setCancelled(true);
     }
     }
     else
     {
     Messages.CRATE_DISABLED.msgSpecified(cc, p);
     e.setCancelled(true);
     }
     }**/

}
