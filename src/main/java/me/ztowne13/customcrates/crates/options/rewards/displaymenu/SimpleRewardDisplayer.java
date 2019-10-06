package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;

public class SimpleRewardDisplayer extends RewardDisplayer
{
    public SimpleRewardDisplayer(Crate crate)
    {
        super(crate);
    }

    public void openFor(Player p)
    {
        p.openInventory(createInventory(p).getInv());
        PlayerManager.get(getCrate().getCc(), p).setInRewardMenu(true);
    }

    public InventoryBuilder createInventory(Player p)
    {
        CRewards cr = getCrate().getCs().getCr();
        int amount = cr.getCrateRewards().length;
        int rows = amount % 9 == 0 ? amount / 9 : (amount / 9) + 1;
        InventoryBuilder ib = new InventoryBuilder(p, rows * 9, getInvName());
        Reward[] crewards = cr.getCrateRewards();
        int i = 0;
        for (Reward r : crewards)
        {
            ib.setItem(i, r.getDisplayBuilder());
            i++;
        }
        return ib;
    }
}
