package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.utils.ChatUtils;

/**
 * Created by ztowne13 on 6/30/16.
 */
public class Errors extends SubCommand
{
    public Errors()
    {
        super("errors", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args)
    {
        if (args.length == 1)
        {
            for (Crate crate : Crate.getLoadedCrates().values())
            {
                cmds.getCmdSender().sendMessage(ChatUtils.toChatColor("&4&l----------"));
                cmds.getCmdSender().sendMessage(ChatUtils.toChatColor("&c" + crate.getName()));
                crate.getCs().getSl().logAll(cmds.getCmdSender(), false);
            }
        }
        else if (args.length > 1)
        {
            if (Crate.crateAlreadyExist(args[1]))
            {
                cmds.msg("&4&lErrors:");
                Crate.getCrate(cc, args[1]).getCs().getSl().logAll(cmds.getCmdSender(), true);
            }
            else
            {
                cmds.msgError(args[1] + " is not a valid crate name to identify loading failures for.");
            }
        }
        return false;
    }
}
