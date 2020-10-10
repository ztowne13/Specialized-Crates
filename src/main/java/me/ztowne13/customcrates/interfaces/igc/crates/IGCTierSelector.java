package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by ztowne13 on 4/2/16.
 */
public class IGCTierSelector extends IGCMenuCrate {
    static List<String> allowedTiers = Arrays.asList("PLAY", "OPEN", "DEFAULT");

    Set<String> tiers;
    IGCTierMenu igcTierMenu;

    public IGCTierSelector(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, Set<String> tiers,
                           IGCTierMenu igcTierMenu) {
        super(cc, p, lastMenu, "&7&l> &6&lTier Selector", crates);
        this.tiers = tiers;
        this.igcTierMenu = igcTierMenu;

    }

    @Override
    public void openMenu() {
        if (tiers.size() == 1) {
            igcTierMenu.setTier(tiers.iterator().next());
            igcTierMenu.open();
            return;
        }

        igcTierMenu.setLastMenu(this);

        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4, tiers.size()) + 9);

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 2;

        ArrayList<String> allTiers = new ArrayList<>(tiers);
        for (Reward reward : crates.getSettings().getRewards().getCrateRewards()) {
            String tier = reward.getRarity().toUpperCase();
            if (!tier.equalsIgnoreCase("DEFAULT") && !allTiers.contains(tier)) {
                allTiers.add(tier);
            }
        }

        for (String tier : allTiers) {
            if (i % 9 == 7) {
                i += 4;
            }

            ItemBuilder button = new ItemBuilder(DynamicMaterial.STONE_BUTTON);
            button.setDisplayName("&a" + tier);
            if (tier.equalsIgnoreCase("PLAY")) {
                ItemBuilder play = button.clone();
                play.getStack().setType(Material.EMERALD);
                play.addAutomaticLore("&7", 30, "These are the values run while the crate is sitting, idle.");
                play.addLore("");
                play.addAutomaticLore("&e", 30,
                        "To add a new 'tier,' change the 'rarity' of a reward in this crate to a new value.");
                ib.setItem(i, play);
            } else {
                button.addLore("&7Rewards in this tier:");
                int listedRewards = 0;
                int remaining = 0;
                for (Reward reward : crates.getSettings().getRewards().getCrateRewards()) {
                    if (!reward.getRarity().equalsIgnoreCase(tier) &&
                            !(reward.getRarity().equalsIgnoreCase("DEFAULT") && tier.equalsIgnoreCase("OPEN"))) {
                        continue;
                    }
                    if (listedRewards >= 5) {
                        remaining++;
                    } else {
                        button.addLore("&7- &f" + reward.getRewardName());
                        listedRewards++;
                    }
                }

                if (remaining > 0) {
                    button.addLore("&7(and &f" + remaining + "&7 more)");
                }

                button.addLore("");
                button.addAutomaticLore("&e", 30,
                        "To add a new 'tier' to this crate, change the 'rarity' of any reward in this crate to a new tier value.");
                ib.setItem(i, button);
            }

            i++;
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        if (slot == 9) {
            up();
        } else if (getIb().getInv().getItem(slot) != null && !getIb().getInv().getItem(slot).getType().equals(Material.AIR)) {
            String tier = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            igcTierMenu.setTier(tier);
            igcTierMenu.open();
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        return false;
    }
}
