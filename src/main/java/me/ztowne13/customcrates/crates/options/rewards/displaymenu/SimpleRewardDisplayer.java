package me.ztowne13.customcrates.crates.options.rewards.displaymenu;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.entity.Player;

public class SimpleRewardDisplayer extends RewardDisplayer {
    public SimpleRewardDisplayer(Crate crate) {
        super(crate);
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
        for (Reward reward : crateRewards) {
            if (i >= 54) {
                break;
            }

            inventoryBuilder.setItem(i, reward.getDisplayBuilder());
            i++;
        }
        return inventoryBuilder;
    }

    @Override
    public void load() {
        loadDefaults();
    }
}
