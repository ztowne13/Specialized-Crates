package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class DelAllCrateType extends SubCommand
{
    public DelAllCrateType()
    {
        super("delallcratetype", 2, "Usage: /SCrates DelAllCrateType (Crate)");
    }

    @Override
    public boolean run(CustomCrates cc, Commands cmds, String[] args)
    {
        if (Crate.exists(args[1]))
        {
            Crate.getCrate(cc, args[1]).deleteAllPlaced();
            cmds.msgSuccess("Deleted all " + args[1] + " crates!");
            return true;
        }
        else
        {
            cmds.msgError("Crate " + args[1] + " doesn't exist.");
            return false;
        }
    }
}
