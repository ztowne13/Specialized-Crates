package me.ztowne13.customcrates.listeners

import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.metadata.FixedMetadataValue
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.crateaction.AttemptCrateUseAction
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BlockPlaceListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlaceCrateKey(e: BlockPlaceEvent) {
        val p = e.player
        val l = e.block.location
        if (!e.isCancelled) {
            if (CrateUtils.searchByKey(e.itemInHand) != null) {
                Messages.DENY_PLACE_KEY.msgSpecified(cc, p)
                e.isCancelled = true
            } else {
                if (AttemptCrateUseAction(cc, p, l, true).run()) {
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPlace(e: BlockPlaceEvent) {
        val b = e.block
        b.setMetadata("PLACED", FixedMetadataValue(cc, "something"))
    }
}