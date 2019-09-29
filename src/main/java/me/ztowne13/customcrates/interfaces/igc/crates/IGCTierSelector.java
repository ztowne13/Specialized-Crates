package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCTierSelector extends IGCMenuCrate
{
    static List<String> allowedTiers = Arrays.asList(new String[]{"PLAY", "OPEN", "DEFAULT"});

    Set<String> tiers;
    IGCTierMenu igcTierMenu;

    public IGCTierSelector(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, Set<String> tiers,
                           IGCTierMenu igcTierMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lTier Selector", crates);
        this.tiers = tiers;
        this.igcTierMenu = igcTierMenu;

    }

    @Override
    public void open()
    {
        if (tiers.size() == 1)
        {
            igcTierMenu.setTier(tiers.iterator().next());
            igcTierMenu.open();
            return;
        }

        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4, tiers.size()) + 9);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 2;
        for (String s : tiers)
        {
            if (allowedTiers.contains(s.toUpperCase()))
            {
                if (i % 9 == 7)
                {
                    i += 4;
                }

                ib.setItem(i, new ItemBuilder(DynamicMaterial.ACACIA_BUTTON, 1).setName("&a" + s)
                        .setLore("&7Click me to view values for this tier."));
                i++;
            }
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        if (slot == 9)
        {
            up();
        }
        else if (getIb().getInv().getItem(slot) != null &&
                getIb().getInv().getItem(slot).getType().equals(DynamicMaterial.ACACIA_BUTTON.parseMaterial()))
        {
            String tier = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            igcTierMenu.setTier(tier);
            igcTierMenu.open();
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return false;
    }
}
