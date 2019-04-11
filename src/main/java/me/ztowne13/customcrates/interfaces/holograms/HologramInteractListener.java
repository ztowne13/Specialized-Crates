package me.ztowne13.customcrates.interfaces.holograms;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class HologramInteractListener implements Listener
{
    CustomCrates cc;

    public HologramInteractListener(CustomCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void armourStandManipulate(PlayerArmorStandManipulateEvent e)
    {
        if(!e.getRightClicked().isVisible())
            if(cc.getHologramManager().isHologramEntity(e.getRightClicked()))
                e.setCancelled(true);
    }
}
