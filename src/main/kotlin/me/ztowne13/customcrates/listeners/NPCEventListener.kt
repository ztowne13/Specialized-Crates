package me.ztowne13.customcrates.listeners

import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.crateaction.AttemptKeyUseAction
import me.ztowne13.customcrates.crates.crateaction.LeftClickAction
import net.citizensnpcs.api.event.NPCLeftClickEvent
import net.citizensnpcs.api.event.NPCRightClickEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Created by ztowne13 on 2/26/16.
 */
class NPCEventListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler
    fun onNPCClickRight(e: NPCRightClickEvent) {
        if (AttemptKeyUseAction(cc, e.clicker, e.npc.storedLocation).run()) {
            e.isCancelled = true
        }
    }

    @EventHandler
    fun onNPCClickLeft(e: NPCLeftClickEvent) {
        val p = e.clicker
        LeftClickAction(cc, p, e.npc.storedLocation).run()
    }
}