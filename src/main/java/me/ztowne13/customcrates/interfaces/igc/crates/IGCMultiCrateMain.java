package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/29/16.
 */
public class IGCMultiCrateMain extends IGCMenuCrate {
    public IGCMultiCrateMain(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates) {
        super(cc, p, lastMenu, "&7&l> &6&lMultiCrates Main", crates);
    }

    @Override
    public void openMenu() {

        InventoryBuilder ib = createDefault(9);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(2, new ItemBuilder(Material.PAPER, 1, 0).setName("&aEdit the current GUI").addLore("")
                .addAutomaticLore("&f", 30,
                        "Design the multicrate inventory in the inventory that opens. Put the actual crates in for the crates, and normal blocks for filler blocks."));
        ib.setItem(4,
                new ItemBuilder(Material.BOOK, 1, 0).setName("&aEdit the amount of rows").setLore("&7Current value:")
                        .addLore("&7" + (crates.getSettings().getMultiCrateSettings().getInventory(getP(), "", false).getInv().getSize() / 9))
                        .addLore("")
                        .addAutomaticLore("&f", 30, "Set the size (in amount of rows) of the multicrate's inventory."));
        ib.setItem(6, new ItemBuilder(DynamicMaterial.RED_WOOL, 1).setName("&aClear the inventory")
                .setLore("&4&lWARNING: &cThis clears the entire").addLore("&cinventory."));

        getIb().open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 0:
                up();
                break;
            case 2:
                crates.getSettings().getMultiCrateSettings().getInventory(getP(), "&c&lClose to save", true).open();
                ChatUtils.msgSuccess(getP(),
                        "Close the inventory to save, if you don't want to save, type /scrates reload and all changes will be lost.");
                break;
            case 4:
                new InputMenu(getCc(), getP(), "set rows",
                        (crates.getSettings().getMultiCrateSettings().getInventory(getP(), "", false).getInv().getSize() / 9) + "",
                        "Please use a number between 1 and 6", Integer.class, this);
                break;
            case 6:
                crates.getSettings().getMultiCrateSettings().getInventory(getP(), "", false).getInv().clear();
                ChatUtils.msgSuccess(getP(), "You have cleared the inventory.");
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        if (value.equalsIgnoreCase("set rows")) {
            if (Utils.isInt(input)) {
                InventoryBuilder oldIb = crates.getSettings().getMultiCrateSettings().getInventory(getP(), "", false);
                InventoryBuilder newIb = new InventoryBuilder(getP(), Integer.parseInt(input) * 9, oldIb.getName());

                for (int i = 0; i < (oldIb.getInv().getSize() < newIb.getInv().getSize() ? oldIb.getInv().getSize() :
                        newIb.getInv().getSize()); i++) {
                    if (!(oldIb.getInv().getItem(i) == null)) {
                        newIb.setItem(i, oldIb.getInv().getItem(i));
                    }
                }
                crates.getSettings().getMultiCrateSettings().setIb(newIb);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to " + input);
                return true;
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid number.");
            }
        }
        return false;
    }
}
