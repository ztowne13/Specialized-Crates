package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class Info extends SubCommand
{
    public Info()
    {
        super("info", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args)
    {
        cc.getSettings().loadInfo();
        cmds.msg("&6&lCurrent &e&lS&7&lC &6&lInformation >");

        for (String s : cc.getSettings().getInfoToLog().keySet())
        {
            cmds.msg(" &b" + s + " &3&l- &a" + cc.getSettings().getInfoToLog().get(s));
        }
        return true;
    }
}
