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

public class SortedRewardDisplayer extends RewardDisplayer {
    private final boolean lowToHigh;

    public SortedRewardDisplayer(Crate crate, boolean lowToHigh) {
        super(crate);
        this.lowToHigh = lowToHigh;
    }

    @Override
    public void open(Player player) {
        player.openInventory(createInventory(player).getInv());
        PlayerManager.get(getCrate().getCc(), player).setInRewardMenu(true);
    }

    @Override
    public InventoryBuilder createInventory(Player player) {
        CRewards rewards = getCrate().getSettings().getRewards();
        int amount = rewards.getCrateRewards().length;
        int rows = amount % 9 == 0 ? amount / 9 : (amount / 9) + 1;
        InventoryBuilder inventoryBuilder = new InventoryBuilder(player, rows * 9, getInvName());
        Reward[] crateRewards = rewards.getCrateRewards();
        int i = 0;
        for (Reward reward : sortedRewards(crateRewards)) {
            if (i >= 54) {
                break;
            }
            inventoryBuilder.setItem(i, reward.getDisplayBuilder());
            i++;
        }
        return inventoryBuilder;
    }

    public List<Reward> sortedRewards(Reward[] rewards) {
        List<Reward> sortedRewards = Arrays.asList(rewards);

        if (lowToHigh)
            Collections.sort(sortedRewards);
        else
            sortedRewards.sort(Collections.reverseOrder());

        return sortedRewards;
    }

    @Override
    public void load() {
        loadDefaults();
    }
}
