package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class ListCrates extends SubCommand {
    public ListCrates() {
        super("listcrates", 1, "", new String[]{"crates", "lcrates", "listcrate"});
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        for (Crate crates : Crate.getLoadedCrates().values()) {
            cmds.msg("&6- &f" + crates.getName());
        }
        return true;
    }
}
