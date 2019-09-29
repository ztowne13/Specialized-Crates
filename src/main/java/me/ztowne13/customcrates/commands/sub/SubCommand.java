package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.command.CommandSender;

/**
 * Created by ztowne13 on 6/23/16.
 */
public abstract class SubCommand
{
    int minimumArgs;
    String commandTitle, usageMessage;

    public SubCommand(String commandTitle, int minimumArgs, String usageMessage)
    {
        this.minimumArgs = minimumArgs;
        this.commandTitle = commandTitle;
        this.usageMessage = usageMessage;
    }

    public abstract boolean run(SpecializedCrates cc, Commands cmds, String[] args);

    public boolean checkProperUsage(CommandSender sender, String[] args)
    {
        boolean enoughArgs = args.length >= minimumArgs;
        if (!enoughArgs)
        {
            sender.sendMessage(ChatUtils.toChatColor("&4&lERROR! &c" + ChatUtils.toChatColor(usageMessage)));
        }

        return enoughArgs;
    }

    public boolean isCommand(String s)
    {
        return commandTitle.equalsIgnoreCase(s);
    }
}
