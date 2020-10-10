package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.actions.actionbar.ActionBar;
import org.bukkit.entity.Player;

/**
 * Created by ztown on 2/14/2017.
 */
public abstract class ActionEffect {
    SpecializedCrates cc;

    public ActionEffect(SpecializedCrates cc) {
        this.cc = cc;
    }

    public abstract ActionBar getActionBarExecutor();

    public abstract void newTitle();

    public abstract void playTitle(Player p);

    public abstract void setDisplayTitle(String title);

    public abstract void setDisplaySubtitle(String subtitle);
}
