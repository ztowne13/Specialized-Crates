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
import org.jetbrains.annotations.NotNull;

public class CommandRewards extends Commands implements CommandExecutor {
    private final SpecializedCrates instance;

    public CommandRewards(SpecializedCrates instance) {
        super("rewards");
        this.instance = instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, String[] args) {
        setCmdSender(commandSender);

        if (!canExecute(false, true, "customcrates.rewards", "specializedcrates.rewards")) {
            if (getCmdSender() instanceof ConsoleCommandSender)
                msg("This command can not be run from console.");
            else
                Messages.NO_PERMISSIONS.msgSpecified(instance, (Player) commandSender, new String[]{"%permission%"},
                        new String[]{"specializedcrates.rewards"});
            return false;
        }

        Player player = (Player) commandSender;
        if (args.length == 0)
            Messages.COMMAND_REWARDS_USAGE.msgSpecified(instance, player);
        else {
            String crateName = args[0];
            if (Crate.exists(crateName)) {
                Crate crate = Crate.getCrate(instance, crateName);
                if (CrateUtils.isCrateUsable(crate) && SettingsValue.REWARD_DISPLAY_ENABLED.getValue(instance).equals(Boolean.TRUE) && !crate.isMultiCrate()) {
                    crate.getSettings().getDisplayer().openFor(player);
                    Messages.COMMAND_REWARDS_OPENING.msgSpecified(instance, player, new String[]{"%crate%"}, new String[]{crateName});
                    return true;
                }
            }
            Messages.COMMAND_REWARDS_INVALID_CRATE.msgSpecified(instance, player, new String[]{"%crate%"}, new String[]{crateName});
        }
        return false;
    }

    @Override
    public void msgPage(int page) {
        // EMPTY
    }
}
