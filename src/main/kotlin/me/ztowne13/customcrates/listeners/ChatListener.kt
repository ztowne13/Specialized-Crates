package me.ztowne13.customcrates.listeners

import org.bukkit.Bukkit
import java.lang.Runnable
import org.bukkit.event.EventPriority
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.players.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

/**
 * Created by ztowne13 on 3/15/16.
 */
class ChatListener(var cc: SpecialisedCrates) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(e: AsyncPlayerChatEvent) {
        val p = e.player
        if (p.hasPermission("me.ztowne13.customcrates.admin") || p.hasPermission("specializedcrates.admin")) {
            val pm: PlayerManager = PlayerManager.Companion.get(cc, p)
            if (pm.isInOpenMenu) {
                val menu = pm.openMenu
                if (menu!!.isInInputMenu) {
                    val msg = e.message
                    val im = menu.inputMenu
                    Bukkit.getScheduler().runTaskLater(cc, Runnable {
                        im!!.runFor(menu, msg)
                        p.sendMessage(ChatUtils.toChatColor(" &7&l> &f$msg"))
                    }, 1)
                    e.recipients.clear()
                    e.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onCmiReloadListener(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        if (player.hasPermission("me.ztowne13.customcrates.admin") || player.hasPermission("specializedcrates.admin")) {
            if (cc.settings!!.infoToLog.containsKey("Hologram Plugin") &&
                cc.settings!!.infoToLog["Hologram Plugin"].equals("CMI", ignoreCase = true)
            ) {
                val split = event.message.split(" ").toTypedArray()
                if (split.size >= 2) {
                    if (split[0].equals("/cmi", ignoreCase = true) || split[0].equals("/cmi:cmi", ignoreCase = true)) {
                        if (split[1].equals("reload", ignoreCase = true)) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(cc, {
                                val start = System.currentTimeMillis()
                                ChatUtils.msgInfo(
                                    player,
                                    "You have executed &c/cmi reload&e. In order to keep holograms on the &6Specialized&7Crates &ecrates, &6Specialized&7Crates &eis also reloading."
                                )
                                cc.reload()
                                ChatUtils.msgInfo(
                                    player,
                                    "Reloaded the Specialized Crate plugin &7(" + (System.currentTimeMillis() - start) +
                                            "ms)&a."
                                )
                            }, 10)
                        }
                    }
                }
            }
        }
    }
}