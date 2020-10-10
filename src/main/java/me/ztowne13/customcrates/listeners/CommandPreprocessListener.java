package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Created by ztowne13 on 3/3/16.
 */
public class CommandPreprocessListener implements Listener {
    SpecializedCrates cc;

    public CommandPreprocessListener(SpecializedCrates cc) {
        this.cc = cc;
    }

    @EventHandler
    public void onCommandPP(PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        PlayerManager pm = PlayerManager.get(cc, p);

        long ct = System.currentTimeMillis();
        long diff = ct - pm.getCmdCooldown();

        if (diff < 1000 && !pm.getLastCooldown().equalsIgnoreCase("cmd")) {
            e.setCancelled(true);
            Messages.WAIT_ONE_SECOND.msgSpecified(cc, p);
        }

        pm.setLastCooldown("cmd");
        pm.setCmdCooldown(ct);
    }
}
