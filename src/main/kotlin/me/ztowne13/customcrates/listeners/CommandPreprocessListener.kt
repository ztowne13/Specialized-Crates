package me.ztowne13.customcrates.listeners

import org.bukkit.event.player.PlayerCommandPreprocessEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.players.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Created by ztowne13 on 3/3/16.
 */
class CommandPreprocessListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler
    fun onCommandPP(e: PlayerCommandPreprocessEvent) {
        val p = e.player
        val pm: PlayerManager = PlayerManager.Companion.get(cc, p)
        val ct = System.currentTimeMillis()
        val diff = ct - pm.cmdCooldown
        if (diff < 1000 && !pm.lastCooldown.equals("cmd", ignoreCase = true)) {
            e.isCancelled = true
            Messages.WAIT_ONE_SECOND.msgSpecified(cc, p)
        }
        pm.lastCooldown = "cmd"
        pm.cmdCooldown = ct
    }
}