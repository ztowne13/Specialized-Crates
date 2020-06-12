package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.crateaction.AttemptKeyUseAction;
import me.ztowne13.customcrates.crates.crateaction.CrateAction;
import me.ztowne13.customcrates.crates.crateaction.LeftClickAction;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener
{
    SpecializedCrates cc;

    public InteractListener(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e)
    {
        cc.getDu().log("onInteract - CALL", this.getClass());
        cc.getDu().log("onInteract - (cancelled: " + e.isCancelled() + ")", getClass());

        Player p = e.getPlayer();

        // Handle crate left or right click
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
        {
            cc.getDu().log("onInteract - Click block", this.getClass());

            CrateAction action;
            if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                action = new AttemptKeyUseAction(cc, p, e.getClickedBlock().getLocation());
                cc.getDu().log("onInteract - is right click block", getClass());
            }
            else
            {
                action = new LeftClickAction(cc, p, e.getClickedBlock().getLocation());
            }

            boolean result = action.run();
            if (result)
            {
                cc.getDu().log("onInteract - Cancelling", getClass());
                e.setCancelled(true);
            }
        }

        // Prevent crate keys from being used for anything else (enderpearls not being throw, etc.)
        if (e.getItem() != null)
        {
            for (Crate crate : Crate.getLoadedCrates().values())
            {
                if (crate.getSettings().getKeyItemHandler().keyMatchesToStack(e.getItem(), false))
                {
                    if(!((Boolean) SettingsValue.KEY_ALLOW_LEFT_CLICK_INTERACTION.getValue(cc) && e.getAction().equals(Action.LEFT_CLICK_BLOCK)))
                    {
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onKeyPreviewMenu(PlayerInteractEvent event)
    {
        Action action = event.getAction();
        if(action.equals(Action.LEFT_CLICK_BLOCK) || action.equals(Action.LEFT_CLICK_AIR))
        {
            if((Boolean) SettingsValue.LEFT_CLICK_KEY_PREVIEW.getValue(cc))
            {
                Crate crate = CrateUtils.searchByKey(event.getItem());
                if(crate != null)
                {
                    crate.getSettings().getDisplayer().openFor(event.getPlayer());
                }
            }
        }
    }
}
