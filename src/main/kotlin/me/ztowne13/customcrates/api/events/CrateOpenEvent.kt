package me.ztowne13.customcrates.api.events

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import me.ztowne13.customcrates.crates.Crate
import me.ztowne13.customcrates.crates.options.rewards.Reward
import org.bukkit.event.Event

class CrateOpenEvent(val player: Player, val rewards: List<Reward?>?, val crate: Crate?, val openedCratesCount: Int) :
    Event() {
    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {
        val handlerList = HandlerList()
    }
}