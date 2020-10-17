package me.ztowne13.customcrates.commands.sub;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.Commands;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ztowne13 on 6/23/16.
 */
public abstract class SubCommand {
    private static final HashMap<String, String> mappedAliases = new HashMap<>();
    private final String[] aliases;
    private final int minimumArgs;
    private final String commandTitle;
    private final String usageMessage;

    public SubCommand(String commandTitle, int minimumArgs, String usageMessage) {
        this(commandTitle, minimumArgs, usageMessage, new String[]{});
    }

    public SubCommand(String commandTitle, int minimumArgs, String usageMessage, String[] aliases) {
        this.minimumArgs = minimumArgs;
        this.commandTitle = commandTitle;
        this.usageMessage = usageMessage;
        this.aliases = aliases;
        for (String alias : aliases) {
            mappedAliases.put(alias, commandTitle);
        }
    }

    public static Map<String, String> getMappedAliases() {
        return mappedAliases;
    }

    public abstract boolean run(SpecializedCrates cc, Commands cmds, String[] args);

    public boolean checkProperUsage(CommandSender sender, String[] args) {
        boolean enoughArgs = args.length >= minimumArgs;
        if (!enoughArgs) {
            sender.sendMessage(ChatUtils.toChatColor("&4&lERROR! &c" + ChatUtils.toChatColor(usageMessage)));
        }

        return enoughArgs;
    }

    public boolean isCommand(String s) {
        if (commandTitle.equalsIgnoreCase(s)) {
            return true;
        }
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }
}
