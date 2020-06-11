package me.ztowne13.customcrates.interfaces.igc.buttons;

import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;

public class IGCButtonRewardFilter implements IGCButton
{
    CRewards.RewardSortType sortType = CRewards.RewardSortType.CREATED_ORDER;

    @Override
    public ItemBuilder getButtonItem()
    {
        ItemBuilder button = new ItemBuilder(DynamicMaterial.HOPPER);
        button.setDisplayName("&6Sorting the Rewards by");
        button.addLore("&e" + sortType.getNiceName());
        button.addLore("");
        for(String line : sortType.getNiceDescription())
        {
            button.addLore(line);
        }
        return button;
    }

    @Override
    public boolean handleClick(IGCMenu menu)
    {
        sortType = sortType.getNext();
        return true;
    }

    @Override
    public CRewards.RewardSortType getValue()
    {
        return sortType;
    }
}
