package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.crateaction.AttemptKeyUseAction;
import me.ztowne13.customcrates.crates.crateaction.CrateAction;
import me.ztowne13.customcrates.crates.crateaction.LeftClickAction;
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent e)
    {
        cc.getDu().log("onInteract - CALL", this.getClass());

        try
        {
            if (!e.getHand().equals(EquipmentSlot.HAND) && !(e.getPlayer().isSneaking() && e.getHand().equals(EquipmentSlot.OFF_HAND)))
            {
                cc.getDu().log("onInteract - Equipment slot isn't hand. It's " + e.getHand().name(), this.getClass());
                return;
            }
        }
        catch (Throwable exc)
        {
            cc.getDu().log("onInteract - Failed to handle equipment slot check", getClass());
        }

        cc.getDu().log("onInteract - (cancelled: " + e.isCancelled() + ")");

        if (!e.isCancelled())
        {
            cc.getDu().log("onInteract - Isn't cancelled", this.getClass());

            Player p = e.getPlayer();

            if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))
            {
                cc.getDu().log("onInteract - Click block", this.getClass());

                CrateAction action;
                if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    action = new AttemptKeyUseAction(cc, p, e.getClickedBlock().getLocation());
                else
                    action = new LeftClickAction(cc, p, e.getClickedBlock().getLocation());

                if (action.run())
                {
                    e.setCancelled(true);
                }
            }
        }

        // Prevent crate keys from being used for anything else (enderpearls not being throw, etc.)
        if (e.getItem() != null)
        {
            for (Crate crate : Crate.getLoadedCrates().values())
            {
                if (crate.keyMatchesToStack(e.getItem()))
                {
                    e.setCancelled(true);
                }
            }
        }
    }


}
