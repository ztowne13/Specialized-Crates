package me.ztowne13.customcrates.commands;

import me.ztowne13.customcrates.Messages;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.sub.*;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * All commands for the plugin
 */
public class CommandCrate extends Commands implements CommandExecutor
{
    SpecializedCrates cc;
    VirtualCrates vcSubCommand;
    ArrayList<SubCommand> subCommands;

    public CommandCrate(SpecializedCrates cc)
    {
        super("scrates");
        this.cc = cc;

        vcSubCommand = new VirtualCrates();

        subCommands = new ArrayList<>(Arrays.asList(new SubCommand[]{
                new Config(),
                new DelAllCrateType(),
                new DeleteCrate(),
                new GiveCrate(),
                new GiveKey(),
                new Help(),
                new Info(),
                new LastConfigMenu(),
                new ListCrates(),
                new ListHistory(),
                new Reload(),
                new Errors(),
                new Edit(),
                vcSubCommand
        }));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args)
    {
        setCmdSender(sender);

        if (canExecute(true, true, "customcrates.admin"))
        {
            if (args.length > 0)
            {
                for (SubCommand subCommand : subCommands)
                {
                    if (subCommand.isCommand(args[0]))
                    {
                        if (subCommand.checkProperUsage(sender, args))
                        {
                            return subCommand.run(cc, this, args);
                        }
                        return false;
                    }
                }
            }
        }

        if (args.length >= 1 && args[0].equalsIgnoreCase("luckychest") &&
                canExecute(false, true, "customcrates.luckychestcommand"))
        {
            PlayerDataManager pdm = PlayerManager.get(cc, (Player) sender).getPdm();
            pdm.setActivatedLuckyChests(!pdm.isActivatedLuckyChests());
            Messages.TOGGLE_LUCKYCRATE.msgSpecified(cc, (Player) sender, new String[]{"%state%"},
                    new String[]{pdm.isActivatedLuckyChests() + ""});
            return true;
        }
        else if (!canExecute(false, true, "customcrates.admin"))
        {
            if (vcSubCommand.run(cc, this, args))
            {
                return true;
            }
            else if (args.length == 0)
            {
                msg("&7&l>> &3&m                    ");
                msg("&c" + cc.getDescription().getName() + " &fV" + cc.getDescription().getVersion());
                msg("&6By &e" + cc.getDescription().getAuthors().get(0));
                msg("&7&l>> &3&m                    ");
                return true;
            }

            msg(Messages.NO_PERMISSIONS.getFromConf(cc)
                    .replaceAll("%permission%", "customcrates.admin"));
        }
        else
        {
            msgPage(1);
        }


        return false;
    }

    public void msgPage(int page)
    {
        msg("  &7&l>  &6&lSC &e&lHelp Menu");
        msg("");
        msg("&a&oPage &c" + page + " / 3");
        msg("");
        if (page == 1)
        {
            msg("&7-  &bHelp &a&l> &fOpen the help menu.");
            msg("&7-  &bGiveKey &a&l> &fGet the get for the specified crate.");
            msg("&7-  &bGiveCrate &a&l> &fGet the item for a specific crate.");
            msg("&7-  &bDeleteCrate &a&l> &fDelete a crate from the world.");
            msg("&7-  &f&oShift + left click in CREATIVE will also delete the crate.");
            msg("&7-  &bConfig &a&l> &fConfigure the plugin in game.");
        }
        else if (page == 2)
        {
            msg("&7-  &bReload &a&l> &fReload all data from the config.yml.");
            msg("&7-  &bListHistory &a&l> &fList the crate history for an online player.");
            msg("&7-  &bListCrates &a&l> &fList all the valid crates.");
            msg("&7-  &bDelAllCrateType &a&l> &fDelete all existing crates of the type.");
            msg("&7-  &bInfo &a&l> &fGet all info for the plugin.");
            msg("&7-  &bluckychest &a&l> &fToggle whether or not you want luckychests to appear.");
        }
        else if (page == 3)
        {
            msg("&7-  &b! &a&l> &fOpen the last crate config menu you were in.");
            msg("&7-  &berrors &a&l> &fSee all crate errors in game!");
            msg("&7-  &bedit &a&l> &fOpen up the in game config directly for a crate.");
            msg("&7-  &bvirtualcrates &a&l> &fOpen the virtual crates menu.");
        }
        else
        {
            msgPage(1);
        }
    }

    public ArrayList<SubCommand> getSubCommands()
    {
        return subCommands;
    }
}
