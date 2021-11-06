package me.ztowne13.customcrates.listeners

import org.bukkit.event.player.PlayerQuitEvent
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.players.PlayerManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

class PlayerConnectionListener(var cc: SpecialisedCrates) : Listener {
    //    @EventHandler
    //    public void adminJoin(PlayerJoinEvent e)
    //    {
    //        final Player p = e.getPlayer();
    //        PlayerManager.get(cc, p);
    //        if (p.hasPermission("me.ztowne13.customcrates.admin") && cc.getUpdateChecker().needsUpdate())
    //        {
    //            Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
    //            {
    //                @Override
    //                public void run()
    //                {
    //                    Messages.NEEDS_UPDATE.msgSpecified(cc, p, new String[]{"%version%"},
    //                            new String[]{cc.getUpdateChecker().getLatestVersion()});
    //                }
    //            }, 1);
    //        }
    //    }
    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        cc.debugUtils!!.log("onQuit() - CALL (" + e.player.name + ")", javaClass)
        val p = e.player
        PlayerManager.Companion.get(cc, p)!!.remove(20)
    }
}