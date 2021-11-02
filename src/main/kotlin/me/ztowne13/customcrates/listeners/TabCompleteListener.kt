package me.ztowne13.customcrates.listeners

import org.bukkit.entity.Player
import org.bukkit.Bukkit
import me.ztowne13.customcrates.SpecialisedCrates
import me.ztowne13.customcrates.commands.sub.SubCommand
import me.ztowne13.customcrates.crates.Crate
import org.bukkit.command.*
import java.util.ArrayList

/**
 * Created by ztowne13 on 2/19/16.
 */
class TabCompleteListener(var cc: SpecialisedCrates) : TabCompleter {
    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String>? {
        var list: MutableList<String>? = ArrayList()
        if (sender is Player) {
            val player = sender
            val cN = cmd.name
            if (cN.equals("crates", ignoreCase = true) ||
                cN.equals("specializedcrates", ignoreCase = true) ||
                cN.equals("sc", ignoreCase = true) ||
                cN.equals("scrates", ignoreCase = true) ||
                cN.equals("ccrates", ignoreCase = true)
            ) {
                if (args.size == 1) {
                    if (player.hasPermission("me.ztowne13.customcrates.admin") || player.hasPermission("specializedcratest.admin")) {
                        list!!.add("config")
                        list.add("virtualcrates")
                        list.add("givecrate")
                        list.add("givekey")
                        list.add("edit")
                        list.add("errors")
                        list.add("deletecrate")
                        list.add("listcrates")
                        list.add("listhistory")
                        list.add("delallcratetype")
                        list.add("reload")
                        list.add("info")
                        list.add("!")
                        list.add("luckychest")
                        list.add("forceopen")
                        list.add("toggleparticles")
                        list.add("spawncrate")
                        list.add("claim")
                    } else {
                        list!!.add("luckychest")
                        list.add("claim")
                    }
                    list = Utils.onlyLeaveEntriesWithPref(list, args[0])
                } else {
                    if (SubCommand.Companion.getMappedAliases().containsKey(args[0])) {
                        args[0] = SubCommand.Companion.getMappedAliases().get(args[0])!!
                    }
                    if (args[0].equals("givekey", ignoreCase = true) || args[0].equals(
                            "givecrate",
                            ignoreCase = true
                        ) ||
                        args[0].equals("delallcratetype", ignoreCase = true)
                    ) {
                        if (args.size == 2) {
                            for (crates in Crate.Companion.getLoadedCrates().values) {
                                list!!.add(crates.name!!)
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1])
                        }
                        if (!args[0].equals("delallcratetype", ignoreCase = true)) {
                            if (args.size == 3) {
                                list!!.add("all")
                                for (p in Bukkit.getOnlinePlayers()) {
                                    list.add(p.name)
                                }
                                list = Utils.onlyLeaveEntriesWithPref(list, args[2])
                            } else if (args.size == 4) {
                                list!!.add("1")
                                list = Utils.onlyLeaveEntriesWithPref(list, "")
                            } else if (args.size == 5) {
                                list!!.add("-v")
                            }
                        }
                    } else if (args[0].equals("edit", ignoreCase = true)) {
                        for (crates in Crate.Companion.getLoadedCrates().values) {
                            list!!.add(crates.name!!)
                        }
                        list = Utils.onlyLeaveEntriesWithPref(list, args[1])
                    } else if (args[0].equals("forceopen", ignoreCase = true)) {
                        if (args.size == 2) {
                            for (crates in Crate.Companion.getLoadedCrates().values) {
                                list!!.add(crates.name!!)
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1])
                        }
                        if (args.size == 3) {
                            list!!.add("all")
                            for (p in Bukkit.getOnlinePlayers()) {
                                list.add(p.name)
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[2])
                        }
                    } else if (args[0].equals("listhistory", ignoreCase = true)) {
                        if (args.size == 2) {
                            for (p in Bukkit.getOnlinePlayers()) {
                                list!!.add(p.name)
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1])
                        } else if (args.size == 3) {
                            list!!.add("10")
                            list = Utils.onlyLeaveEntriesWithPref(list, "")
                        }
                    } else if (args[0].equals("errors", ignoreCase = true)) {
                        for (crates in Crate.Companion.getLoadedCrates().values) {
                            list!!.add(crates.name!!)
                        }
                        list = Utils.onlyLeaveEntriesWithPref(list, args[1])
                    } else if (args[0].equals("spawncrate", ignoreCase = true)) {
                        if (args.size == 2) {
                            for (crates in Crate.Companion.getLoadedCrates().values) {
                                list!!.add(crates.name!!)
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[1])
                        } else if (args.size == 3) {
                            for (world in Bukkit.getWorlds()) {
                                list!!.add(world.name)
                            }
                            list = Utils.onlyLeaveEntriesWithPref(list, args[2])
                        } else if (args.size == 4) {
                            list!!.add("x")
                        } else if (args.size == 5) {
                            list!!.add("y")
                        } else if (args.size == 6) {
                            list!!.add("z")
                        }
                    }
                }
            } else if (cN.equals("rewards", ignoreCase = true)) {
                if (args.size >= 1) {
                    for (crates in Crate.Companion.getLoadedCrates().values) {
                        val cs = crates.settings
                        if (player.hasPermission(cs!!.permission!!) || cs!!.permission.equals(
                                "no permission",
                                ignoreCase = true
                            )
                        ) if (!crates.isMultiCrate) list!!.add(
                            crates.name!!
                        )
                    }
                    list = Utils.onlyLeaveEntriesWithPref(list, args[0])
                }
            }
        }
        return list
    }
}