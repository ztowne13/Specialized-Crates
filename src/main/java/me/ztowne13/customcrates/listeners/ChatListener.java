package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by ztowne13 on 3/15/16.
 */
public class ChatListener implements Listener
{
    CustomCrates cc;

    public ChatListener(CustomCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e)
    {
        Player p = e.getPlayer();
        if(p.hasPermission("customcrates.admin"))
        {
            PlayerManager pm = PlayerManager.get(cc, p);
            if (pm.isInOpenMenu())
            {
                IGCMenu menu = pm.getOpenMenu();
                if (menu.isInInputMenu())
                {
                    InputMenu im = menu.getInputMenu();
                    im.runFor(menu, e.getMessage());
                    p.sendMessage(ChatUtils.toChatColor(" &7&l> &f" + e.getMessage()));
                    e.setCancelled(true);
                }
            }
        }
    }
}
