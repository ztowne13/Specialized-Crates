package me.ztowne13.customcrates.external.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

public class HologramInteractListener implements Listener
{
    SpecializedCrates cc;

    public HologramInteractListener(SpecializedCrates cc)
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
