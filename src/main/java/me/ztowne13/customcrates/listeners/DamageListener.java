package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageListener implements Listener {
    SpecializedCrates sc;

    public DamageListener(SpecializedCrates sc) {
        this.sc = sc;
    }

    @EventHandler
    public void onFireworkExplode(EntityDamageByEntityEvent event) {
        if (event.getDamager().getCustomName() != null) {
            if (event.getDamager().getCustomName().equalsIgnoreCase("scf")) {
                if (!event.getDamager().isCustomNameVisible()) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
