package me.ztowne13.customcrates.listeners

import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockExplodeEvent
import org.bukkit.event.block.BlockPistonExtendEvent
import org.bukkit.event.entity.EntityExplodeEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.PlacedCrate
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BlockRemoveListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler
    fun onBlockChange(e: BlockExplodeEvent) {
        if (cc.isAllowTick) if ((SettingsValue.EXPLODE_DYNAMIC.getValue(cc) as Boolean)) e.blockList()
            .removeIf { b: Block -> PlacedCrate.Companion.crateExistsAt(b.location) }
    }

    @EventHandler
    fun onPistonPush(e: BlockPistonExtendEvent) {
        if (cc.isAllowTick && shouldCancel(e.blocks, e.direction)) e.isCancelled = true
    }

    @EventHandler
    fun onEntityExplode(e: EntityExplodeEvent) {
        if (cc.isAllowTick) if ((SettingsValue.EXPLODE_DYNAMIC.getValue(cc) as Boolean)) e.blockList()
            .removeIf { b: Block -> PlacedCrate.Companion.crateExistsAt(b.location) }
    }

    fun shouldCancel(blocks: List<Block>, bf: BlockFace?): Boolean {
        var shouldCancel = false
        if ((SettingsValue.EXPLODE_DYNAMIC.getValue(cc) as Boolean)) {
            for (b in blocks) {
                if (PlacedCrate.Companion.crateExistsAt(b.location) ||
                    PlacedCrate.Companion.crateExistsAt(b.getRelative(bf!!).location)
                ) {
                    shouldCancel = true
                    break
                }
            }
        }
        return shouldCancel
    }
}