package me.ztowne13.customcrates.listeners

import org.bukkit.event.entity.EntityDamageByEntityEvent
import me.ztowne13.customcrates.SpecialisedCrates
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class DamageListener(var sc: SpecialisedCrates) : Listener {
    @EventHandler
    fun onFireworkExplode(event: EntityDamageByEntityEvent) {
        if (event.damager.customName != null) {
            if (event.damager.customName.equals("scf", ignoreCase = true)) {
                if (!event.damager.isCustomNameVisible) {
                    event.isCancelled = true
                }
            }
        }
    }
}