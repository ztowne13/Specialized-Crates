package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CActions extends CSetting
{
    HashMap<String, HashMap<String, ArrayList<String>>> actions = new HashMap<String, HashMap<String, ArrayList<String>>>();

    public CActions(Crate crates)
    {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cs)
    {
        if (csb.hasV("open.actions"))
        {
            List<String> list = getCrates().getCs().getFc().getStringList("open.actions");
            for (String s : list)
            {
                addEntryByString("DEFAULT", s);
            }
        }

        if (csb.hasV("open.crate-tiers"))
        {
            for (String tier : getCrates().getCs().getFc().getConfigurationSection("open.crate-tiers").getKeys(false))
            {
                if (csb.hasV("open.crate-tiers." + tier + ".actions"))
                {
                    List<String> list = getCrates().getCs().getFc().getStringList("open.crate-tiers." + tier + ".actions");
                    for (String s : list)
                    {
                        addEntryByString(tier, s);
                    }
                }
            }
        }
    }

    public void saveToFile()
    {
        if (!actions.isEmpty())
        {
            for (String tier : actions.keySet())
            {
                ArrayList<String> toSetList = new ArrayList<>();
                String path = "open." + (tier.equalsIgnoreCase("DEFAULT") ? "" : "crate-tiers." + tier + ".") + "actions";

                for (String actionType : actions.get(tier).keySet())
                {
                    for (String action : actions.get(tier).get(actionType))
                    {
                        toSetList.add(ChatUtils.fromChatColor(actionType + ", " + action));
                    }
                }

                getFu().get().set(path, toSetList);
            }
        }
    }

    public void addEntry(String type, String action, String tier)
    {
        HashMap<String, ArrayList<String>> map =
                getActions().containsKey(tier) ? getActions().get(tier) : new HashMap<String, ArrayList<String>>();

        ArrayList<String> list = map.containsKey(type) ? map.get(type) : new ArrayList<String>();
        list.add(action);
        map.put(type, list);

        StatusLoggerEvent.ACTION_ADD.log(getCrates(), new String[]{action, tier});
        getActions().put(tier, map);
    }

    public void removeEntry(String type, String action, String tier)
    {
        getActions().get(tier).get(type).remove(action);
    }

    public void addEntryByString(String crateTier, String toAdd)
    {
        String[] split = toAdd.split(",");
        String type = split[0].replace(" ", "").replace(",", "");
        String action = "";

        boolean b = false;
        for (String words : split)
        {
            if (b)
            {
                action = words + " ";
            }
            b = true;
        }

        if (action.startsWith(" "))
        {
            action = action.substring(1);
        }

        action = ChatUtils.toChatColor(action);

        addEntry(type, action, crateTier);
    }

    public void playAll(Player p, boolean pre)
    {
        playAll(p, new ArrayList<Reward>(), pre);
    }

    public void playAll(Player p, PlacedCrate placedCrate, boolean pre)
    {
        playAll(p, placedCrate, new ArrayList<Reward>(), pre);
    }

    public void playAll(Player p, ArrayList<Reward> rewards, boolean pre)
    {
        playAll(p, null, rewards, pre);
    }

    public void playAll(Player p, PlacedCrate placedCrate, ArrayList<Reward> rewards, boolean pre)
    {
        cc.getDu().log("playAll() - CALL (pre: " + pre + ")", getClass());

        if (rewards.isEmpty() && !pre)
            return;

        boolean toRunTitle = false;

        ArrayList<String> rewardsAsDisplayname = new ArrayList<>();
        for (Reward r : rewards)
        {
            rewardsAsDisplayname.add(r.getDisplayName());
        }

        for (String tier : getActions().keySet())
        {
            if (pre || (tier.equalsIgnoreCase("DEFAULT") &&
                    !getActions().keySet().contains(rewards.get(0).getRarity().toLowerCase())) ||
                    rewards.get(0).getRarity().equalsIgnoreCase(tier))
            {
                for (String s : getActions().get(tier).keySet())
                {
                    s = s.toUpperCase();
                    for (String msg : getActions().get(tier).get(s))
                    {
                        if (pre)
                        {
                            if (s.startsWith("PRE_"))
                            {
                                s = s.substring(4);
                            }
                            else
                            {
                                continue;
                            }
                        }
                        else
                        {
                            if (s.startsWith("PRE_"))
                            {
                                continue;
                            }
                        }

                        String rewardsAsString = rewardsAsDisplayname.toString();
                        rewardsAsString = rewardsAsString.substring(1, rewardsAsString.length() - 1);

                        msg = ChatUtils.toChatColor(
                                msg.replace("%player%", p.getName()).replace("%crate%", getCrates().getName())
                                        .replace("%reward%", rewardsAsString));
                        if (s.equalsIgnoreCase("MESSAGE"))
                        {
                            p.sendMessage(msg);
                        }
                        else if (s.equalsIgnoreCase("BROADCAST"))
                        {
//                            for (Player onlinePlayer : Bukkit.getOnlinePlayers())
//                            {
//                                onlinePla\yer.sendMessage(msg);
//                            }

                            Bukkit.broadcastMessage(msg);
                        }
                        else
                        {
                            ChatUtils.log(new String[]{
                                    "Please note that an action type called " + s + " was attempted to be run",
                                    "    However, this action type does not exist. Valid types are:",
                                    "      MESSAGE, BROADCAST, ACTIONBAR, TITLE, and SUBTITLE"});
                        }
                    }
                }
            }
        }
    }

    public void attachTo(Item item, String rewardName)
    {
        Entity real = null;

        for (Entity entity : item.getLocation().getChunk().getEntities())
        {
//            if (item.getLocation().distance(entity.getLocation()) < 2 &&
//                    ChatUtils.removeColor(rewardName).equalsIgnoreCase(ChatUtils.removeColor(entity.getName())) &&
//                    !entity.equals(item))
//            {
//                real = entity;
//                break;
//            }
        }

        try
        {
//            if (real != null)
//            {
//                if (NMSUtils.Version.v1_13.isServerVersionOrLater())
//                    item.addPassenger(real);
//                else
//                    item.setPassenger(real);
//            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }

    }

    public HashMap<String, HashMap<String, ArrayList<String>>> getActions()
    {
        return actions;
    }

    public void setActions(HashMap<String, HashMap<String, ArrayList<String>>> actions)
    {
        this.actions = actions;
    }
}
