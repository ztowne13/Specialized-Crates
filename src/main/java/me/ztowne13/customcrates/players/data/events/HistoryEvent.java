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
import java.util.List;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class HistoryEvent extends DataEvent {
    Crate crates;
    List<Reward> rewards;
    boolean success;
    String currentTime;

    public HistoryEvent(String currentTime, Crate crates, List<Reward> rewards, boolean success) {
        super(crates.getCc());
        this.crates = crates;
        this.rewards = rewards;
        this.success = success;
        this.currentTime = currentTime;
    }

    public static void listFor(SpecializedCrates cc, CommandSender sender, Player toDisplay, int amount) {
        ChatUtils.msg(sender, "&6TIME &7- &9CRATE &7- &cREWARD\n\n" +
                "&aOldest Entries\n" +
                "      to\n" +
                "&aNewest Entries\n\n");

        ArrayList<HistoryEvent> hevents = new ArrayList<>(PlayerManager.get(cc, toDisplay).getPdm().getHistoryEvents());

        Collections.reverse(hevents);

        amount = Math.min(amount, hevents.size());
        ChatUtils.msg(sender, "&eHistory for " + toDisplay.getName() + " &7(Showing " + amount + "/" + hevents.size() + ")");

        for (int i = amount - 1; i >= 0; i--) {
            HistoryEvent he = hevents.get(i);
            String[] split = he.getFormatted().split(";");
            String rewardName = split[2].replace("%newReward%", ",");
            ChatUtils
                    .msg(sender, "&6" + he.getCurrentTime() + " &7- &9" + he.getCrates().getName() + " &7- &c" + rewardName);
        }
    }

    @Override
    public void addTo(PlayerDataManager pdm) {
        pdm.addHistory(this, pdm.addStringToList(getFormatted(), pdm.getHistory()));
    }

    @Override
    public String getFormatted() {
        String rewardName = getRewards() == null || getRewards().isEmpty() ? "none" :
                getRewards().toString().replace(",", "%newReward%");
        return getCurrentTime() + ";" + getCrates().getName() + ";" + rewardName + ";" + isSuccess();
    }

    public boolean matches(HistoryEvent he) {
        boolean matches =
                getCrates().getName().equalsIgnoreCase(he.getCrates().getName()) && isSuccess() == he.isSuccess() &&
                        getCurrentTime().equals(he.getCurrentTime());
        for (int i = 0; i < getRewards().size(); i++) {
            if (!getRewards().get(i).getDisplayName(false).equalsIgnoreCase(he.getRewards().get(i).getDisplayName(false))) {
                matches = false;
                break;
            }
        }
        return matches;
    }

    public Crate getCrates() {
        return crates;
    }

    public void setCrates(Crate crates) {
        this.crates = crates;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(List<Reward> reward) {
        this.rewards = reward;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}
