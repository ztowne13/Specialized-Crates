package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Created by ztowne13 on 3/15/16.
 */
public class ChatListener implements Listener
{
    SpecializedCrates cc;

    public ChatListener(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(final AsyncPlayerChatEvent e)
    {
        final Player p = e.getPlayer();
        if(p.hasPermission("customcrates.admin"))
        {
            PlayerManager pm = PlayerManager.get(cc, p);
            if (pm.isInOpenMenu())
            {
                final IGCMenu menu = pm.getOpenMenu();
                if (menu.isInInputMenu())
                {
                    Bukkit.getScheduler().runTaskLater(cc, new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            InputMenu im = menu.getInputMenu();
                            im.runFor(menu, e.getMessage());
                            p.sendMessage(ChatUtils.toChatColor(" &7&l> &f" + e.getMessage()));
                        }
                    }, 1);

                    e.getRecipients().clear();
                    e.setCancelled(true);
                }
            }
        }
    }
}
