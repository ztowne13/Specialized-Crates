package me.ztowne13.customcrates.crates.options.actions.actionbar;

import org.bukkit.entity.Player;

public class ActionBar
{
    public void play(Player player, String msg)
    {
        player.spigot().sendMessage(net.md_5.bungee.api.ChatMessageType.ACTION_BAR,
                net.md_5.bungee.api.chat.TextComponent.fromLegacyText(msg));
    }
}
