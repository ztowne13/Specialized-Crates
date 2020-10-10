package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class Reload extends SubCommand {
    public Reload() {
        super("reload", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        long start = System.currentTimeMillis();
        cmds.msg("&6&lINFO! &eReloading...");
        cc.reload();
        cmds.msgSuccess("Reloaded the Specialized Crate plugin &7(" + (System.currentTimeMillis() - start) + "ms)&a.");
        return true;
    }
}
