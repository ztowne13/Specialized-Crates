package me.ztowne13.customcrates.listeners;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.commands.sub.SubCommand;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 2/19/16.
 */
public class TabCompleteListener implements TabCompleter
{
    SpecializedCrates cc;

    public TabCompleteListener(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        List<String> list = new ArrayList<String>();
        if (sender instanceof Player)
        {
            String cN = cmd.getName();
            if (cN.equalsIgnoreCase("crates") ||
                    cN.equalsIgnoreCase("specializedcrates") ||
                    cN.equalsIgnoreCase("sc") ||
                    cN.equalsIgnoreCase("scrates") ||
                    cN.equalsIgnoreCase("ccrates"))
            {
                if (args.length == 1)
                {
                    list.add("config");
                    list.add("virtualcrates");
                    list.add("givecrate");
                    list.add("givekey");
                    list.add("edit");
                    list.add("errors");
                    list.add("deletecrate");
                    list.add("listcrates");
                    list.add("listhistory");
                    list.add("delallcratetype");
                    list.add("reload");
                    list.add("info");
                    list.add("!");
                    list.add("luckychest");
                    list.add("forceopen");
                    list.add("toggleparticles");
                    list.add("spawncrate");
                    list = Utils.onlyLeaveEntriesWithPref(list, args[0]);
                }
                else
                {
                    if(SubCommand.getMappedAliases().containsKey(args[0]))
                    {
                        args[0] = SubCommand.getMappedAliases().get(args[0]);
                    }

                    if (args[0].equalsIgnoreCase("givekey") || args[0].equalsIgnoreCase("givecrate") ||
                            args[0].equalsIgnoreCase("delallcratetype"))
                    {
                        if (args.length == 2)
                        {
                            for (Crate crates : Crate.getLoadedCrates().values())
                            {
                                list.add(crates.getName());
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1]);
                        }
                        if (!args[0].equalsIgnoreCase("delallcratetype"))
                        {
                            if (args.length == 3)
                            {
                                list.add("all");
                                for (Player p : Bukkit.getOnlinePlayers())
                                {
                                    list.add(p.getName());
                                }
                                list = Utils.onlyLeaveEntriesWithPref(list, args[2]);
                            }
                            else if (args.length == 4)
                            {
                                list.add("1");
                                list = Utils.onlyLeaveEntriesWithPref(list, "");
                            }
                            else if (args.length == 5)
                            {
                                list.add("-v");
                            }
                        }
                    }
                    else if (args[0].equalsIgnoreCase("edit"))
                    {
                        for (Crate crates : Crate.getLoadedCrates().values())
                        {
                            list.add(crates.getName());
                        }
                        list = Utils.onlyLeaveEntriesWithPref(list, args[1]);
                    }
                    else if (args[0].equalsIgnoreCase("forceopen"))
                    {
                        if(args.length == 2)
                        {
                            for (Crate crates : Crate.getLoadedCrates().values())
                            {
                                list.add(crates.getName());
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1]);
                        }
                        if(args.length == 3)
                        {
                            list.add("all");
                            for (Player p : Bukkit.getOnlinePlayers())
                            {
                                list.add(p.getName());
                            }

                            list = Utils.onlyLeaveEntriesWithPref(list, args[2]);
                        }
                    }
                    else if (args[0].equalsIgnoreCase(("listhistory")))
                    {
                        if (args.length == 2)
                        {
                            for (Player p : Bukkit.getOnlinePlayers())
                            {
                                list.add(p.getName());
                            }

                            list = Utils.onlyLeaveEntriesWithPref(list, args[1]);
                        }
                        else if (args.length == 3)
                        {
                            list.add("10");
                            list = Utils.onlyLeaveEntriesWithPref(list, "");
                        }
                    }
                    else if (args[0].equalsIgnoreCase("errors"))
                    {
                        for (Crate crates : Crate.getLoadedCrates().values())
                        {
                            list.add(crates.getName());
                        }
                        list = Utils.onlyLeaveEntriesWithPref(list, args[1]);
                    }
                    else if(args[0].equalsIgnoreCase("spawncrate"))
                    {
                        if(args.length == 2)
                        {
                            for (Crate crates : Crate.getLoadedCrates().values())
                            {
                                list.add(crates.getName());
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1]);
                        } else if(args.length == 3) {
                            for(World world : Bukkit.getWorlds())
                            {
                                list.add(world.getName());
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[2]);
                        } else if(args.length == 4) {
                            list.add("x");
                        } else if(args.length == 5) {
                            list.add("y");
                        } else if(args.length == 6) {
                            list.add("z");
                        }
                    }
                }

            }
            else if(cN.equalsIgnoreCase("rewards"))
            {
                if(args.length >= 1)
                {
                    for (Crate crates : Crate.getLoadedCrates().values())
                    {
                        Player player = (Player) sender;
                        CrateSettings cs = crates.getSettings();

                        if (player.hasPermission(cs.getPermission()) || cs.getPermission().equalsIgnoreCase("no permission"))
                            if (!crates.isMultiCrate())
                                list.add(crates.getName());
                    }

                    list = Utils.onlyLeaveEntriesWithPref(list, args[0]);
                }
            }
        }



        return list;
    }
}
