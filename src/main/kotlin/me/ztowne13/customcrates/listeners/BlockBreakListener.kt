package me.ztowne13.customcrates.listeners

import org.bukkit.GameMode
import org.bukkit.event.block.BlockBreakEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.crates.Crate
import me.ztowne13.customcrates.crates.PlacedCrate
import me.ztowne13.customcrates.crates.options.ObtainType
import me.ztowne13.customcrates.players.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class BlockBreakListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler
    fun onBreakPlacedCrate(e: BlockBreakEvent) {
        val p = e.player
        val l = e.block.location
        if (PlacedCrate.Companion.crateExistsAt(l)) {
            val cm: PlacedCrate = PlacedCrate.Companion.get(cc, l)
            val crates = cm.crate
            if (crates!!.settings.obtainType!!.isStatic) {
                if (!p.hasPermission("me.ztowne13.customcrates.admin") && !p.hasPermission("specializedcrates.admin")) {
                    e.isCancelled = true
                    Messages.FAILED_BREAK_CRATE.msgSpecified(
                        cc, p, arrayOf("%crate%", "%reason%"), arrayOf(
                            crates.displayName, "static"
                        )
                    )
                }
                return
            }
            cm.delete()
            Messages.BROKEN_CRATE.msgSpecified(
                cc, p, arrayOf("%crate%"), arrayOf(
                    crates.displayName
                )
            )
        } else {
            // Event isn't already cancelled
            if (!e.isCancelled) {
                // Not in creative mode or creative mode is allowed
                if (p.gameMode != GameMode.CREATIVE || (SettingsValue.LUCKYCHEST_CREATIVE.getValue(cc) as Boolean)) {
                    // Luckychests enabled
                    if (PlayerManager.Companion.get(cc, p)!!.getPlayerDataManager().isActivatedLuckyChests()) {
                        // Cycle through all potential crates
                        for (crates in Crate.Companion.getLoadedCrates().values) {
                            // Check if the crate is a lucky chest and if it is enabled
                            if (CrateUtils.isCrateUsable(crates) && crates.settings.luckyChestSettingsExists() && crates.settings.obtainType == ObtainType.LUCKYCHEST) {
                                // Check if the lucky chesty should be placed at the location
                                if (crates.settings.luckyChestSettings!!.checkRun(e.block)) {
                                    // Check if this block is a placed block or not and whether or not that's okay
                                    if (!e.block.hasMetadata("PLACED") ||
                                        e.block.getMetadata("PLACED") == null ||
                                        (SettingsValue.LUCKYCHEST_ALLOW_PLACED_BLOCKS.getValue(cc) as Boolean)
                                    ) {
                                        // Check to make sure the player has the permission or doesn't need the permission
                                        if (!crates.settings.luckyChestSettings!!.isRequirePermission ||
                                            e.player.hasPermission(crates.settings.permission!!)
                                        ) {
                                            val cm: PlacedCrate = PlacedCrate.Companion.get(
                                                cc, e.block.location
                                            )
                                            cm.setup(crates, true)
                                            Messages.FOUND_LUCKY_CHEST.msgSpecified(cc, p)
                                            e.isCancelled = true
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}