package me.ztowne13.customcrates.interfaces.igc.crates.previeweditor;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCCrateRewards;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMenuCrate;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import org.bukkit.entity.Player;

public class IGCCratePreviewOrRewardMenu extends IGCMenuCrate {
    public IGCCratePreviewOrRewardMenu(SpecializedCrates specializedCrates, Player player, Crate crate, IGCMenu lastMenu) {
        super(specializedCrates, player, lastMenu, "&7&l> &6&lRewards", crate);
    }

    @Override
    public void openMenu() {
        InventoryBuilder ib = createDefault(9);

        // Reward Editor
        ItemBuilder rewardEditor = new ItemBuilder(XMaterial.LIGHT_BLUE_DYE, 1);
        rewardEditor.setDisplayName("&aReward Editor");
        rewardEditor.addAutomaticLore("&7", 30, "Add, remove, and edit the rewards for this crate.");

        // Reward Preview Editor
        ItemBuilder rewardPreview = new ItemBuilder(XMaterial.CHEST, 1);
        rewardPreview.setDisplayName("&aReward Preview Menu Editor");
        rewardPreview.addAutomaticLore("&7", 30, "Edit the reward preview menu and everything related to it.");

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(3, rewardEditor);
        ib.setItem(5, rewardPreview);

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 0:
                up();
                break;
            case 3:
                new IGCCrateRewards(getCc(), getP(), this, getCrates(), 1).open();
                break;
            case 5:
                new IGCCratePreviewMenu(getCc(), getP(), getCrates(), this).open();
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        return false;
    }
}
