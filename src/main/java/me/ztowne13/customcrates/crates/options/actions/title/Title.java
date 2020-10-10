package me.ztowne13.customcrates.crates.options.actions.title;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public interface Title {
    String getTitle();

    void setTitle(String title);

    String getSubtitle();

    void setSubtitle(String subtitle);

    void setTimingsToTicks();

    void setTimingsToSeconds();

    void send(Player player);

    void updateTimes(Player player);

    void updateTitle(Player player);

    void updateSubtitle(Player player);

    void broadcast();

    void clearTitle(Player player);

    ChatColor getTitleColor();

    void setTitleColor(ChatColor color);

    ChatColor getSubtitleColor();

    void setSubtitleColor(ChatColor color);

    int getFadeInTime();

    void setFadeInTime(int time);

    int getFadeOutTime();

    void setFadeOutTime(int time);

    int getStayTime();

    void setStayTime(int time);

    boolean isTicks();
}
