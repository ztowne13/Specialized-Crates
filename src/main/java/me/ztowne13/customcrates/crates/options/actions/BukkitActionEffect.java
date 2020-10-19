package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.actions.actionbar.ActionBar;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 2/14/2017.
 */
public class BukkitActionEffect extends ActionEffect {
    private String title;
    private String subtitle;
    private int fadeIn;
    private int stay;
    private int fadeOut;

    public BukkitActionEffect(SpecializedCrates instance) {
        super(instance);
    }

    public ActionBar getActionBarExecutor() {
        return new ActionBar();
    }

    public void newTitle() {
        fadeIn = Integer.parseInt(SettingsValue.CA_FADE_IN.getValue(instance).toString());
        stay = Integer.parseInt(SettingsValue.CA_STAY.getValue(instance).toString());
        fadeOut = Integer.parseInt(SettingsValue.CA_FADE_OUT.getValue(instance).toString());
    }

    public void playTitle(Player p) {
        p.sendTitle(title, subtitle, fadeIn * 20, stay * 20, fadeOut * 20);
        resetData();
    }

    public void setDisplayTitle(String title) {
        this.title = title;
    }

    public void setDisplaySubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void resetData() {
        title = "";
        subtitle = "";
    }
}
