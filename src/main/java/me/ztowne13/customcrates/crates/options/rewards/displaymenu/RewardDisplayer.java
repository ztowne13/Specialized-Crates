package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public abstract class RewardDisplayer
{
    Crate crates;

    public RewardDisplayer(Crate crates)
    {
        this.crates = crates;
    }

    public abstract void openFor(Player p);

    public abstract InventoryBuilder createInventory(Player p);

    public String getInvName()
    {
        return ChatUtils.toChatColor(
                getCrates().getCc().getSettings().getConfigValues().get("inv-reward-display-name").toString()
                        .replace("%crate%", getCrates().getName()));
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

}
