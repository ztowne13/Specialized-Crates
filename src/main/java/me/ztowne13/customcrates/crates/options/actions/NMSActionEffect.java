package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.actions.actionbar.ActionBar;
import me.ztowne13.customcrates.crates.options.actions.actionbar.ActionBarV1_7_8;
import me.ztowne13.customcrates.crates.options.actions.actionbar.ActionBarV1_9_10_11;
import me.ztowne13.customcrates.crates.options.actions.title.Title;
import me.ztowne13.customcrates.crates.options.actions.title.TitleV1_11;
import me.ztowne13.customcrates.crates.options.actions.title.TitleV1_7_8_9_10;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 2/14/2017.
 */
public class NMSActionEffect extends ActionEffect
{
    Title title;

    public NMSActionEffect(SpecializedCrates cc)
    {
        super(cc);
    }

    public ActionBar getActionBarExecutor()
    {
        if (VersionUtils.Version.v1_8.isServerVersionOrEarlier())
            return new ActionBarV1_7_8();
        else
            return new ActionBarV1_9_10_11();
    }

    public void newTitle()
    {
        if (VersionUtils.Version.v1_10.isServerVersionOrEarlier())
        {
            title = new TitleV1_7_8_9_10("", "",
                    (Integer.parseInt(SettingsValue.CA_FADE_IN.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValue.CA_STAY.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValue.CA_FADE_OUT.getValue(cc).toString())));
        }
        else
        {
            title = new TitleV1_11("", "",
                    (Integer.parseInt(SettingsValue.CA_FADE_IN.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValue.CA_STAY.getValue(cc).toString())),
                    (Integer.parseInt(SettingsValue.CA_FADE_OUT.getValue(cc).toString())));
        }
    }

    public void playTitle(Player p)
    {
        if (title != null)
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
