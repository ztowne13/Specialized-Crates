package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.utils.NMSUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 2/14/2017.
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
        if(NMSUtils.Version.v1_10.isServerVersionEarlier())
        {
            title = new TitleV1_7_8_9_10("", "",
                    (Integer.parseInt(SettingsValues.CA_FADE_IN.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValues.CA_STAY.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValues.CA_FADE_OUT.getValue(cc).toString())));
        }
        else
        {
            title = new TitleV1_11("", "",
                    (Integer.parseInt(SettingsValues.CA_FADE_IN.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValues.CA_STAY.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValues.CA_FADE_OUT.getValue(cc).toString())));
        }
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
