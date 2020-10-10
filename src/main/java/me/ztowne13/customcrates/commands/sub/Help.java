package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.utils.Utils;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class Help extends SubCommand {
    public Help() {
        super("help", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args) {
        if (args.length == 2) {
            if (Utils.isInt(args[1])) {

                cmds.msgPage(Integer.parseInt(args[1]));
                return true;
            } else {
                cmds.msgError(args[1] + " is not a valid page number.");
            }
            return true;
        }
        cmds.msgPage(1);
        return true;
    }
}
