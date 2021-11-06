package me.ztowne13.customcrates.listeners

import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.Crate
import me.ztowne13.customcrates.crates.crateaction.AttemptKeyUseAction
import me.ztowne13.customcrates.crates.crateaction.CrateAction
import me.ztowne13.customcrates.crates.crateaction.LeftClickAction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*

class InteractListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onInteract(e: PlayerInteractEvent) {
        cc.debugUtils!!.log("onInteract - CALL", this.javaClass)
        cc.debugUtils!!.log("onInteract - (cancelled: " + e.isCancelled + ")", javaClass)
        val p = e.player

        // Handle crate left or right click
        if (e.action == Action.RIGHT_CLICK_BLOCK || e.action == Action.LEFT_CLICK_BLOCK) {
            cc.debugUtils!!.log("onInteract - Click block", this.javaClass)
            val action: CrateAction
            if (e.action == Action.RIGHT_CLICK_BLOCK) {
                action = AttemptKeyUseAction(cc, p, e.clickedBlock!!.location)
                cc.debugUtils!!.log("onInteract - is right click block", javaClass)
            } else {
                action = LeftClickAction(cc, p, e.clickedBlock!!.location)
            }
            val result = action.run()
            if (result) {
                cc.debugUtils!!.log("onInteract - Cancelling", javaClass)
                e.isCancelled = true
            }
        }

        // Prevent crate keys from being used for anything else (enderpearls not being throw, etc.)
        if (e.item != null) {
            for (crate in Crate.Companion.getLoadedCrates().values) {
                if (crate.settings.keyItemHandler!!.keyMatchesToStack(e.item, false)) {
                    if (!((SettingsValue.KEY_ALLOW_LEFT_CLICK_INTERACTION.getValue(cc) as Boolean) && e.action == Action.LEFT_CLICK_BLOCK)) {
                        e.isCancelled = true
                    }
                }
            }
        }
    }

    @EventHandler
    fun onKeyPreviewMenu(event: PlayerInteractEvent) {
        val action = event.action
        if (action == Action.LEFT_CLICK_BLOCK || action == Action.LEFT_CLICK_AIR) {
            if ((SettingsValue.LEFT_CLICK_KEY_PREVIEW.getValue(cc) as Boolean)) {
                val crate = CrateUtils.searchByKey(event.item)
                if (crate != null) {
                    crate.settings.displayer!!.openFor(event.player)
                }
            }
        }
    }
}