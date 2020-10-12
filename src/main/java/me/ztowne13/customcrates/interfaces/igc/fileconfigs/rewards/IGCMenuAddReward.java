package me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.buttons.IGCButtonType;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class IGCMenuAddReward extends IGCMenu {

    int page;
    Crate crate;

    public IGCMenuAddReward(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crate, int page) {
        super(cc, p, lastMenu, "&7&l> &6&lRewards PG" + page,
                new IGCButtonType[]{
                        IGCButtonType.REWARD_FILTER
                },
                new int[]{
                        8
                });
        this.page = page;
        this.crate = crate;
    }

    @Override
    public void openMenu() {
        int slots;

        CRewards.loadAll(getCc(), getP());

        HashMap<String, Reward> rewards = getUnusedRewards((CRewards.RewardSortType) getButtons()[0].getValue());

        slots = Math.min(rewards.size() - ((page - 1) * 28), 28);

        slots = InventoryUtils.getRowsFor(2, slots) + 9;


        setInventoryName("&7&l> &6&lRewards PG" + page);
        InventoryBuilder ib = createDefault(slots, 27);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 10;
        int toSkip = ((page - 1) * 28);
        int skipped = 0;
        int displayedRewards = 0;

        for (String rName : rewards.keySet()) {
            if (toSkip > skipped || displayedRewards >= 28) {
                skipped++;
                continue;
            }

            if (i % 9 == 8) {
                i += 2;
            }

            Reward r;

            r = rewards.get(rName);

            r.checkIsNeedMoreConfig();
            ItemBuilder newR;

            if (r.isNeedsMoreConfig())
                newR = new ItemBuilder(XMaterial.BARRIER).setDisplayName("&4&l" + rName)
                        .setLore("&cThis reward isn't fully configured,").addLore("&cplease fix it and reload the plugin.");
            else
                newR = new ItemBuilder(r.getDisplayBuilder().getStack()).setDisplayName("&a" + rName);

            newR.setLore("").addLore("&7- Name: &f" + r.getDisplayBuilder().getDisplayName(true));
            newR.addLore("&7- Chance: &f" + r.getChance());
            newR.addLore("&7- Give display item: &f" + r.isGiveDisplayItem());
            newR.addLore("&7- Give display item WITH lore: &f" + r.isGiveDisplayItemLore());
            newR.addLore("&7- Commands to run: &f" + r.getCommands().size());
            newR.addLore("&7- Rarity: &f" + r.getRarity());

            newR.addLore("").addLore("&eClick to add to the crate.");

            ib.setItem(i, newR);
            i++;
            displayedRewards++;
        }

        if (page != 1) {
            ib.setItem(2, new ItemBuilder(XMaterial.ARROW).setDisplayName("&aGo back a page"));
        }

        if ((CRewards.getAllRewards().size() / 28) + (CRewards.getAllRewards().size() % 28 == 0 ? 0 : 1) != page) {
            ib.setItem(6, new ItemBuilder(XMaterial.ARROW).setDisplayName("&aGo forward a page"));
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        if (slot == 2 && XMaterial.ARROW.isSimilar(getIb().getInv().getItem(slot))) {
            page--;
            open();
        } else if (slot == 6 && XMaterial.ARROW.isSimilar(getIb().getInv().getItem(slot))) {
            page++;
            open();
        } else if (slot == 0) {
            up();
        } else if (getIb().getInv().getItem(slot) != null) {
            String rName = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            if (crate.getSettings().getRewards().addReward(rName)) {
                ChatUtils.msgSuccess(getP(), "Added " + rName);
                open();
            } else {
                ChatUtils.msgError(getP(), "Failed to add the reward, maybe it's not completely configured?");
            }
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        return false;
    }

    public HashMap<String, Reward> getUnusedRewards(CRewards.RewardSortType sortType) {
        CRewards cRewards = crate.getSettings().getRewards();

        HashMap<String, Reward> rewards = (HashMap<String, Reward>) CRewards.getAllRewardsSorted(getCc(), sortType);

        for (Reward reward : cRewards.getCrateRewards()) {
            rewards.remove(reward.getRewardName());
        }

        return rewards;
    }

}
