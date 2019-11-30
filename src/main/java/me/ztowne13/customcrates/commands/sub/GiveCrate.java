package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.DataHandler;
import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

/**
 * Created by ztowne13 on 6/23/16.
 */
public class GiveCrate extends SubCommand
{
    public GiveCrate()
    {
        super("givecrate", 2, "Usage: /SCrates GiveCrate (Crate) (Player/ALL) [Amount] [-v : for a virtual crate]");
    }

    @Override
    public boolean run(SpecializedCrates cc, Commands cmds, String[] args)
    {
        if (Crate.exists(args[1]))
        {
            int amount = 1;
            if (args.length >= 4)
            {
                if (Utils.isInt(args[3]))
                {
                    amount = Integer.parseInt(args[3]);
                }
                else
                {
                    cmds.msgError(args[3] + " is not a valid number.");
                    return true;
                }
            }

            Crate crate = Crate.getCrate(cc, args[1]);
            ItemStack toAdd = crate.getCs().getCrate(amount);

            if (args.length < 3)
            {
                if (cmds.getCmdSender() instanceof Player)
                {
                    Player p = (Player) cmds.getCmdSender();
                    cmds.msgSuccess("Given crate for crate: " + args[1]);
                    Utils.addItemAndDropRest(p, toAdd);
                    Messages.DEMO.msgSpecified(cc, p);
                }
                return true;
            }

            String end = args[args.length - 1];
            boolean isVirtual = end.toLowerCase().startsWith("-v");

            if (args[2].equalsIgnoreCase("ALL"))
            {
                if(isVirtual)
                {
                    for(Player p : Bukkit.getOnlinePlayers())
                    {
                        PlayerDataManager pdm = PlayerManager.get(cc, p).getPdm();
                        pdm.setVirtualCrateCrates(crate, pdm.getVCCrateData(crate).getCrates() + amount);
                        Messages.DEMO.msgSpecified(cc, p);
                    }
                    cmds.msgSuccess("Given a virtual crate for " + args[1] + " to every online player.");
                    return true;
                }
                cmds.msgSuccess("Given a physical crate for " + args[1] + " to every online player.");
                Utils.giveAllItem(toAdd);
                return true;
            }

            Player op = Bukkit.getPlayer(args[2]);
            Player op2 = null;
            try
            {
                op2 = Bukkit.getPlayer(UUID.fromString(args[2]));
            }
            catch (Exception exc)
            {
                //exc.printStackTrace();
            }

            boolean foundPlayer = true;

            if (op == null && op2 == null)
            {
                if (!args[2].equalsIgnoreCase("ALL"))
                {
                    foundPlayer = false;
                }
            }

            if (!foundPlayer)
            {
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[2]);

                cmds.msgError(args[2] +
                        " is not an online player / online player's UUID. Adding the commands to the queue for when they rejoin.");
                DataHandler dataHandler = cc.getDataHandler();
                try
                {
                    DataHandler.QueuedGiveCommand queuedGiveCommand = dataHandler.new QueuedGiveCommand(
                            offlinePlayer == null ? UUID.fromString(args[2]) : offlinePlayer.getUniqueId(), false, isVirtual,
                            amount, crate);

                    dataHandler.addQueuedGiveCommand(queuedGiveCommand);
                }
                catch(Exception exc)
                {
                    exc.printStackTrace();
                    cmds.msgError("FAILED to add the give command! The player and/or UUID do not exist.");
                }
                return false;
            }

            Player toGive = op == null ? op2 : op;
            PlayerDataManager pdm = PlayerManager.get(cc, toGive).getPdm();
            Messages.DEMO.msgSpecified(cc, toGive);

            if (isVirtual)
            {
                pdm.setVirtualCrateCrates(crate, pdm.getVCCrateData(crate).getCrates() + amount);
                cmds.msgSuccess("Given virtual crate for crate: " + args[1]);
            }
            else
            {
                Utils.addItemAndDropRest(toGive, toAdd);
                cmds.msgSuccess("Given physical crate for crate: " + args[1]);
            }
            return true;
        }
        cmds.msgError(args[1] + " crate does NOT exist.");
        return false;
    }
}
