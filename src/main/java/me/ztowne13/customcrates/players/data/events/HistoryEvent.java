package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.players.PlayerDataManager;
import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class HistoryEvent extends DataEvent
{
    Crate crates;
    ArrayList<Reward> rewards;
    boolean success;
    String currentTime;

    public HistoryEvent(String currentTime, Crate crates, ArrayList<Reward> rewards, boolean success)
    {
        super(crates.getCc());
        this.crates = crates;
        this.rewards = rewards;
        this.success = success;
        this.currentTime = currentTime;
    }

    @Override
    public void addTo(PlayerDataManager pdm)
    {
        pdm.addHistory(this, pdm.addStringToList(getFormatted(), pdm.getHistory()));
    }

    @Override
    public String getFormatted()
    {
        String rewardName = getRewards() == null || getRewards().isEmpty() ? "none" :
                getRewards().toString().replace(",", "%newReward%");
        return getCurrentTime() + ";" + getCrates().getName() + ";" + rewardName + ";" + isSuccess();
    }

    public boolean matches(HistoryEvent he)
    {
        boolean matches =
                getCrates().getName().equalsIgnoreCase(he.getCrates().getName()) && isSuccess() == he.isSuccess() &&
                        getCurrentTime() == he.getCurrentTime();
        for (int i = 0; i < getRewards().size(); i++)
        {
            if (!getRewards().get(i).getDisplayName(false).equalsIgnoreCase(he.getRewards().get(i).getDisplayName(false)))
            {
                matches = false;
                break;
            }
        }
        return matches;
    }

    public static void listFor(SpecializedCrates cc, CommandSender sender, Player toDisplay, int amount)
    {
        ChatUtils.msg(sender, "&6TIME &7- &9CRATE &7- &cREWARD\n\n" +
                "&aOldest Entries\n" +
                "      to\n" +
                "&aNewest Entries\n\n");
        ChatUtils.msg(sender, "&eHistory for " + toDisplay.getName());


        ArrayList<HistoryEvent> hevents =
                (ArrayList<HistoryEvent>) PlayerManager.get(cc, toDisplay).getPdm().getHistoryEvents().clone();
        Collections.reverse(hevents);

        amount = amount >= hevents.size() ? hevents.size() - 1 : amount;

        for (int i = amount - 1; i >= 0; i--)
        {
            HistoryEvent he = hevents.get(i);
            String[] split = he.getFormatted().split(";");
            String rewardName = split[2].replace("%newReward%", ",");
            ChatUtils
                    .msg(sender, "&6" + he.getCurrentTime() + " &7- &9" + he.getCrates().getName() + " &7- &c" + rewardName);
        }
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public ArrayList<Reward> getRewards()
    {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> reward)
    {
        this.rewards = reward;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getCurrentTime()
    {
        return currentTime;
    }

    public void setCurrentTime(String currentTime)
    {
        this.currentTime = currentTime;
    }
}
