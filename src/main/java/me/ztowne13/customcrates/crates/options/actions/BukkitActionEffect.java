package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import org.bukkit.entity.Player;

/**
 * Created by ztown on 2/14/2017.
 */
public class BukkitActionEffect extends ActionEffect
{
    String title, subtitle;
    int fadeIn, stay, fadeOut;

    public BukkitActionEffect(CustomCrates cc)
    {
        super(cc);
    }

    public void newTitle()
    {
        fadeIn = Integer.parseInt(SettingsValues.CA_FADE_IN.getValue(cc).toString());
        stay = Integer.parseInt(SettingsValues.CA_STAY.getValue(cc).toString());
        fadeOut = Integer.parseInt(SettingsValues.CA_FADE_OUT.getValue(cc).toString());
    }

    public void playTitle(Player p)
    {
        p.sendTitle(title, subtitle, fadeIn*20, stay*20, fadeOut*20);
        resetData();
    }

    public void setDisplayTitle(String title)
    {
        this.title = title;
    }

    public void setDisplaySubtitle(String subtitle)
    {
        this.subtitle = subtitle;
    }

    public void resetData()
    {
        title = "";
        subtitle = "";
    }
}
