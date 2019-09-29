package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 8/3/16.
 */
public class VirtualCrates extends SubCommand
{
    boolean tryLoad = false;
    boolean successfulLoad = false;
    Crate crate;

    public VirtualCrates()
    {
        super("virtualcrates", 1, "");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args)
    {
        if (!tryLoad)
        {
            tryLoad = true;

            String crateName = cc.getSettings().getConfigValues().get("crates-command-multicrate").toString();
            if (crate.exists(crateName))
            {
                crate = Crate.getCrate(cc, crateName);
                successfulLoad = crate.isMultiCrate();
            }
        }

        if (cmds.canExecute(false, true, "customcrates.crates") || cmds.canExecute(false, true, "customcrates.admin"))
        {
            if (successfulLoad)
            {
                Player p = (Player) cmds.getCmdSender();
                PlayerManager pm = PlayerManager.get(cc, p);
                Messages.OPENING_VIRTUALCRATES.msgSpecified(cc, p);
                crate.getCs().getCmci()
                        .getInventory(p, cc.getSettings().getConfigValues().get("crates-command-name").toString(), true)
                        .open();
                // FIX THIS BECAUSE I THINK THE CODE CHECKS IF THE LAST OPENED CRATE WAS A MULTICRATE BY CHECKING ITS LOCATION
                pm.setLastOpenCrate(p.getLocation());
                pm.openCrate(crate);
                pm.setUseVirtualCrate(true);
                return true;
            }
            cmds.msgError(
                    "The crate name specified for this command is either not a valid crate name or the crate is not a mutlicrate.");
            return true;
        }
        return false;
    }
}
