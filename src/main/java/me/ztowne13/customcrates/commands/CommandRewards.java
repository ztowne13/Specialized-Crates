package me.ztowne13.customcrates.commands;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandRewards extends Commands implements CommandExecutor
{
    SpecializedCrates sc;

    public CommandRewards(SpecializedCrates sc)
    {
        super("rewards");
        this.sc = sc;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args)
    {
        setCmdSender(commandSender);

        if (canExecute(false, true, "customcrates.rewards", "specializedcrates.rewards"))
        {
            Player player = (Player)commandSender;
            if(args.length == 0)
                Messages.COMMAND_REWARDS_USAGE.msgSpecified(sc, player);
            else
            {
                String crateName = args[0];
                if(Crate.exists(crateName))
                {
                    Crate crate = Crate.getCrate(sc, crateName);
                    if (CrateUtils.isCrateUsable(crate))
                    {
                        if ((Boolean) SettingsValue.REWARD_DISPLAY_ENABLED.getValue(sc))
                        {
                            if (!crate.isMultiCrate())
                            {
                                crate.getSettings().getDisplayer().openFor(player);
                                Messages.COMMAND_REWARDS_OPENING.msgSpecified(sc, player, new String[]{"%crate%"}, new String[]{crateName});
                                return true;
                            }
                        }
                    }
                }
                Messages.COMMAND_REWARDS_INVALID_CRATE.msgSpecified(sc, player, new String[]{"%crate%"}, new String[]{crateName});
            }
        }
        else
        {
            if (getCmdSender() instanceof ConsoleCommandSender)
                msg("This command can not be run from console.");
            else
                Messages.NO_PERMISSIONS.msgSpecified(sc, (Player) commandSender, new String[]{"%permission%"},
                        new String[]{"specializedcrates.rewards"});
        }
        return false;
    }

    @Override
    public void msgPage(int page) { }
}
