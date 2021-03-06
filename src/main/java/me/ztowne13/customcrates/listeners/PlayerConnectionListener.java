package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {
    SpecializedCrates cc;

    public PlayerConnectionListener(SpecializedCrates cc) {
        this.cc = cc;
    }

//    @EventHandler
//    public void adminJoin(PlayerJoinEvent e)
//    {
//        final Player p = e.getPlayer();
//        PlayerManager.get(cc, p);
//        if (p.hasPermission("customcrates.admin") && cc.getUpdateChecker().needsUpdate())
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
    public void onQuit(PlayerQuitEvent e) {
        cc.getDebugUtils().log("onQuit() - CALL (" + e.getPlayer().getName() + ")", getClass());
        Player p = e.getPlayer();
        PlayerManager.get(cc, p).remove(20);
    }

}
