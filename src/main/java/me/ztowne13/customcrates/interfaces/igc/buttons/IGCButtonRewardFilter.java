package me.ztowne13.customcrates.interfaces.igc.buttons;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.crates.options.CReward;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;

public class IGCButtonRewardFilter implements IGCButton {
    CReward.RewardSortType sortType = CReward.RewardSortType.CREATED_ORDER;

    @Override
    public ItemBuilder getButtonItem() {
        ItemBuilder button = new ItemBuilder(XMaterial.HOPPER);
        button.setDisplayName("&6Sorting the Rewards by");
        button.addLore("&e" + sortType.getNiceName());
        button.addLore("");
        for (String line : sortType.getNiceDescription()) {
            button.addLore(line);
        }
        return button;
    }

    @Override
    public boolean handleClick(IGCMenu menu) {
        sortType = sortType.getNext();
        return true;
    }

    @Override
    public CReward.RewardSortType getValue() {
        return sortType;
    }
}
