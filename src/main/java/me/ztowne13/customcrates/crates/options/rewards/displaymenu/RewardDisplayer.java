package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

public abstract class RewardDisplayer
{
    Crate crate;

    public RewardDisplayer(Crate crates)
    {
        this.crate = crates;
    }

    abstract void openFor(Player p);

    abstract InventoryBuilder createInventory(Player p);

    public String getInvName()
    {
        return ChatUtils.toChatColor(
                getCrate().getCc().getSettings().getConfigValues().get("inv-reward-display-name").toString()
                        .replace("%crate%", getCrate().getName()));
    }

    public Crate getCrate()
    {
        return crate;
    }

    public void setCrate(Crate crate)
    {
        this.crate = crate;
    }
}
