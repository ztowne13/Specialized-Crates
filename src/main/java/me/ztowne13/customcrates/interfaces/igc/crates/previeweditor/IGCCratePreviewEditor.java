package me.ztowne13.customcrates.interfaces.igc.crates.previeweditor;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.DisplayPage;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMenuCrate;
import me.ztowne13.customcrates.interfaces.items.SaveableItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class IGCCratePreviewEditor extends IGCMenuCrate {
    public static String INV_NAME = "&c&lClose to Save";

    CustomRewardDisplayer customRewardDisplayer;
    DisplayPage page;

    public IGCCratePreviewEditor(SpecializedCrates specializedCrates, Player player, Crate crate, IGCMenu lastMenu, DisplayPage page) {
        super(specializedCrates, player, lastMenu, INV_NAME, crate);

        this.customRewardDisplayer = (CustomRewardDisplayer) getCrates().getSettings().getDisplayer();
        this.page = page;
    }

    @Override
    public void openMenu() {
        InventoryBuilder ib = page.buildInventoryBuilder(getP(), true, "&c&lClose to leave and Save", null, false);

        setIb(ib);
        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        manageClick(slot, false, null);
    }

    public void manageClick(int slot, boolean isDrag, ItemStack dragMaterial) {
        int x = slot / 9;
        int y = slot % 9;

        if (page.getRewards()[x][y] == null && page.getBuilders()[x][y] == null) {
            if ((getP().getItemOnCursor() == null || getP().getItemOnCursor().getType().equals(Material.AIR)) && !isDrag) {
                new IGCCratePreviewRewards(getCc(), getP(), this, getCrates(), 1, slot, customRewardDisplayer).open();
            } else {
                SaveableItemBuilder builder = new SaveableItemBuilder(isDrag ? dragMaterial : getP().getItemOnCursor());

                page.getBuilders()[x][y] = builder;
                msg("ITEM added.");

                msgBreak();
                getIb().clear();
                page.buildInventoryBuilder(getP(), true, INV_NAME, getIb(), false);
            }
        } else {
            boolean isReward = page.getRewards()[x][y] != null;

            if (isReward) {
                page.getRewards()[x][y] = null;
                msg("REWARD removed.");
            } else {
                getP().getInventory().addItem(page.getBuilders()[x][y].getStack());
                page.getBuilders()[x][y] = null;
                msg("ITEM returned.");
            }

            if (!isDrag && (getP().getItemOnCursor() != null && !getP().getItemOnCursor().getType().equals(Material.AIR))) {
                SaveableItemBuilder builder = new SaveableItemBuilder(isDrag ? dragMaterial : getP().getItemOnCursor());

                page.getBuilders()[x][y] = builder;
                msg("ITEM added.");
            }

            msgBreak();
            getIb().clear();
            page.buildInventoryBuilder(getP(), true, INV_NAME, getIb(), false);
        }
    }

    public void close() {

    }

    @Override
    public boolean handleInput(String value, String input) {
        String[] args = value.split(" ");
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set") && args[1].equalsIgnoreCase("reward")) {
                Integer slot = Integer.parseInt(args[2]);

                Reward reward = getCs().getRewards().getByName(input);

                int x = slot / 9;
                int y = slot % 9;

                page.getRewards()[x][y] = reward;
                msg("REWARD added.");
                open();
            }
        }
        return false;
    }

    public void msg(String s) {
        ChatUtils.msg(getP(), "&2&l!! &a" + s);
    }

    public void msgBreak() {
        ChatUtils.msg(getP(), "&2*");
    }

    public CustomRewardDisplayer getCustomRewardDisplayer() {
        return customRewardDisplayer;
    }
}
