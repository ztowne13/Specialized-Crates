package me.ztowne13.customcrates.crates.options.actions.title;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public abstract class Title
{
    public abstract void setTitle(String title);

    public abstract String getTitle();

    public abstract void setSubtitle(String subtitle);

    public abstract String getSubtitle();

    abstract void setTitleColor(ChatColor color);

    abstract void setSubtitleColor(ChatColor color);

    abstract void setFadeInTime(int time);

    abstract void setFadeOutTime(int time);

    abstract void setStayTime(int time);

    abstract void setTimingsToTicks();

    abstract void setTimingsToSeconds();

    public abstract void send(Player player);

    abstract void updateTimes(Player player);

    abstract void updateTitle(Player player);

    abstract void updateSubtitle(Player player);

    abstract void broadcast();

    abstract void clearTitle(Player player);

    abstract ChatColor getTitleColor();

    abstract ChatColor getSubtitleColor();

    abstract int getFadeInTime();

    abstract int getFadeOutTime();

    abstract int getStayTime();

    abstract boolean isTicks();
}
