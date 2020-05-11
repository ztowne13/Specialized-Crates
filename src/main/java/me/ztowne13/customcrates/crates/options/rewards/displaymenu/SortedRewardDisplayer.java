package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortedRewardDisplayer extends RewardDisplayer
{
    boolean lowToHigh;

    public SortedRewardDisplayer(Crate crate, boolean lowToHigh)
    {
        super(crate);
        this.lowToHigh = lowToHigh;
    }

    @Override
    public void open(Player p)
    {
        p.openInventory(createInventory(p).getInv());
        PlayerManager.get(getCrates().getCc(), p).setInRewardMenu(true);
    }

    @Override
    public InventoryBuilder createInventory(Player p)
    {
        CRewards cr = getCrates().getSettings().getRewards();
        int amount = cr.getCrateRewards().length;
        int rows = amount % 9 == 0 ? amount / 9 : (amount / 9) + 1;
        InventoryBuilder ib = new InventoryBuilder(p, rows * 9, getInvName());
        Reward[] crewards = cr.getCrateRewards();
        int i = 0;
        for (Reward r : sortedRewards(crewards))
        {
            ib.setItem(i, r.getDisplayBuilder());
            i++;
        }
        return ib;
    }

    public List<Reward> sortedRewards(Reward[] rewards)
    {
        List<Reward> sortedRewards = Arrays.asList(rewards);

        if(lowToHigh)
            Collections.sort(sortedRewards);
        else
            Collections.sort(sortedRewards, Collections.<Reward>reverseOrder());

        return sortedRewards;
    }

    @Override
    public void load()
    {
        loadDefaults();
    }
}
