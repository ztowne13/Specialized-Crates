package me.ztowne13.customcrates.api;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class CrateOpenEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private final Crate crate;
    private final List<Reward> rewards;
    private final Player player;
    private final int openedCratesCount;

    public CrateOpenEvent(Player player, List<Reward> rewards, Crate crate, int openedCratesCount) {
        this.player = player;
        this.rewards = rewards;
        this.crate = crate;
        this.openedCratesCount = openedCratesCount;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public Crate getCrate() {
        return crate;
    }

    public List<Reward> getRewards() {
        return rewards;
    }

    public Player getPlayer() {
        return player;
    }

    public int getOpenedCratesCount() {
        return openedCratesCount;
    }
}
