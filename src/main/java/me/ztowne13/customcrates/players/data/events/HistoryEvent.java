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
    private final Crate crate;
    private final List<Reward> rewards;
    private boolean success;
    private String currentTime;

    public HistoryEvent(String currentTime, Crate crate, List<Reward> rewards, boolean success) {
        super(crate.getInstance());
        this.crate = crate;
        this.rewards = rewards;
        this.success = success;
        this.currentTime = currentTime;
    }

    public static void listFor(SpecializedCrates instance, CommandSender sender, Player toDisplay, int amount) {
        ChatUtils.msg(sender, "&6TIME &7- &9CRATE &7- &cREWARD\n\n" +
                "&aOldest Entries\n" +
                "      to\n" +
                "&aNewest Entries\n\n");

        ArrayList<HistoryEvent> hevents = new ArrayList<>(PlayerManager.get(instance, toDisplay).getPlayerDataManager().getHistoryEvents());

        Collections.reverse(hevents);

        amount = Math.min(amount, hevents.size());
        ChatUtils.msg(sender, "&eHistory for " + toDisplay.getName() + " &7(Showing " + amount + "/" + hevents.size() + ")");

        for (int i = amount - 1; i >= 0; i--) {
            HistoryEvent he = hevents.get(i);
            String[] split = he.getFormatted().split(";");
            String rewardName = split[2].replace("%newReward%", ",");
            ChatUtils
                    .msg(sender, "&6" + he.getCurrentTime() + " &7- &9" + he.getCrate().getName() + " &7- &c" + rewardName);
        }
    }

    @Override
    public void addTo(PlayerDataManager playerDataManager) {
        playerDataManager.addHistory(this, playerDataManager.addStringToList(getFormatted(), playerDataManager.getHistory()));
    }

    @Override
    public String getFormatted() {
        String rewardName = getRewards() == null || getRewards().isEmpty() ? "none" :
                getRewards().toString().replace(",", "%newReward%");
        return getCurrentTime() + ";" + getCrate().getName() + ";" + rewardName + ";" + isSuccess();
    }

    public boolean matches(HistoryEvent historyEvent) {
        boolean matches =
                getCrate().getName().equalsIgnoreCase(historyEvent.getCrate().getName()) && isSuccess() == historyEvent.isSuccess() &&
                        getCurrentTime().equals(historyEvent.getCurrentTime());
        for (int i = 0; i < getRewards().size(); i++) {
            if (!getRewards().get(i).getDisplayName(false).equalsIgnoreCase(historyEvent.getRewards().get(i).getDisplayName(false))) {
                matches = false;
                break;
            }
        }
        return matches;
    }

    public Crate getCrate() {
        return crate;
    }

    public List<Reward> getRewards() {
        return rewards;
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
