package me.ztowne13.customcrates.interfaces.igc.crates.previeweditor;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.CReward;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.DisplayPage;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMenuCrate;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IGCCratePreviewRewards extends IGCMenuCrate {
    int page;
    Crate crate;
    int slot;
    CustomRewardDisplayer customRewardDisplayer;

    public IGCCratePreviewRewards(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crate, int page, int slot,
                                  CustomRewardDisplayer customRewardDisplayer) {
        super(cc, p, lastMenu, "&7&l> &6&lRewards PG" + page, crate);
        this.page = page;
        this.crate = crate;
        this.slot = slot;
        this.customRewardDisplayer = customRewardDisplayer;
    }

    @Override
    public void openMenu() {

        int slots;

        CReward.loadAll(getCc(), getP());

        List<Reward> rewards = getUnusedRewards();

        if (rewards.size() - ((page - 1) * 28) >= 28) {
            slots = 28;
        } else {
            slots = rewards.size() - ((page - 1) * 28);
        }

        slots = InventoryUtils.getRowsFor(2, slots) + 9;


        setInventoryName("&7&l> &6&lRewards PG" + page);
        InventoryBuilder ib = createDefault(slots, 27);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 10;
        int toSkip = ((page - 1) * 28);
        int skipped = 0;
        int displayedRewards = 0;

        for (Reward r : rewards) {
            if (toSkip > skipped || displayedRewards >= 28) {
                skipped++;
                continue;
            }

            if (i % 9 == 8) {
                i += 2;
            }


            r.checkIsNeedMoreConfig();
            ItemBuilder newR;

            if (!r.isNeedsMoreConfig())
                newR = new ItemBuilder(r.getDisplayBuilder().getStack()).setDisplayName("&a" + r.getRewardName());
            else
                continue;

            newR.addLore("").addLore("&eClick to add to the crate.");

            ib.setItem(i, newR);
            i++;
            displayedRewards++;
        }

        if (page != 1) {
            ib.setItem(2, new ItemBuilder(XMaterial.ARROW).setDisplayName("&aGo back a page"));
        }

        if ((CReward.getAllRewards().size() / 28) + (CReward.getAllRewards().size() % 28 == 0 ? 0 : 1) != page) {
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

            getLastMenu().handleInput("set reward " + this.slot, rName);
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        return false;
    }

    public List<Reward> getUnusedRewards() {
        CReward cReward = crate.getSettings().getReward();

        List<Reward> rewardsL = Arrays.asList(cReward.getCrateRewards());
        ArrayList<Reward> rewards = new ArrayList<>(rewardsL);

        ArrayList<Reward> toCompareRewards = new ArrayList<>();
        for (DisplayPage page : customRewardDisplayer.getPages().values()) {
            toCompareRewards.addAll(page.rewardsAsList());
        }

        for (Reward reward : toCompareRewards) {
            rewards.remove(reward);
        }

        return rewards;
    }
}
