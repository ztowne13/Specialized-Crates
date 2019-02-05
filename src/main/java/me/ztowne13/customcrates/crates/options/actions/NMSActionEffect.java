package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ztown on 2/14/2017.
 */
public class NMSActionEffect extends ActionEffect
{
    Title title;

    public NMSActionEffect(CustomCrates cc)
    {
        super(cc);
    }

    public void newTitle()
    {
        title = new Title("", "", (Integer.parseInt(SettingsValues.CA_FADE_IN.getValue(cc).toString())), (Integer.parseInt(SettingsValues.CA_STAY.getValue(cc).toString())), (Integer.parseInt(SettingsValues.CA_FADE_OUT.getValue(cc).toString())));
    }

    public void playTitle(Player p)
    {
        if(title != null)
        {
            title.send(p);
            title = null;
        }
    }

    public void setDisplayTitle(String titleMsg)
    {
        title.setTitle(titleMsg);
    }

    public void setDisplaySubtitle(String subtitleMsg)
    {
        title.setSubtitle(subtitleMsg);
    }
}
