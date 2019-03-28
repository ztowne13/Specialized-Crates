package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class DeleteCrate extends SubCommand
{
    public DeleteCrate()
    {
        super("deletecrate", 1, "");
    }

    @Override
    public boolean run(CustomCrates cc, Commands cmds, String[] args)
    {
        if (cmds.canExecute(false, false, ""))
        {
            Player p = (Player) cmds.getCmdSender();
            PlayerManager pm = PlayerManager.get(cc, p);

            if (pm.isDeleteCrate())
            {
                pm.setDeleteCrate(false);
                ChatUtils.msgSuccess(p, "You will no longer delete a crate!");
            }
            else
            {
                pm.setDeleteCrate(true);
                ChatUtils.msg(p, "&ePlease LEFT-CLICK the crate you'd like to delete.");
                ChatUtils.msg(p, " &o&6Use /SCrates deletecrate to disable this mode.");
            }
        }
        return true;
    }
}
