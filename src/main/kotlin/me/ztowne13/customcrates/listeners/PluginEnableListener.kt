package me.ztowne13.customcrates.listeners

import org.bukkit.event.server.PluginEnableEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.PlacedCrate
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PluginEnableListener(var sc: SpecialisedCrates) : Listener {
    var toListenHolograms = arrayOf(
        "CMI",
        "Holographic Displays",
        "Holograms"
    )

    @EventHandler
    fun onPluginEnable(pluginEnableEvent: PluginEnableEvent) {
        for (hologramName in toListenHolograms) {
            if (hologramName.equals(pluginEnableEvent.plugin.name, ignoreCase = true)) {
                for (crate in PlacedCrate.Companion.getPlacedCrates().values) {
                    crate.setupHolo(crate.crate, false)
                }
            }
        }
        if (pluginEnableEvent.plugin.name.equals("Multiverse-Core", ignoreCase = true)) {
            for (s in sc.settings!!.failedPlacedCrate) sc.settings!!.loadCrateFromFile(s)
        }
    }
}