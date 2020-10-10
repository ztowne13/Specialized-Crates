package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.crateanimations.*;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 3/20/16.
 */
public class IGCMenuCrateConfig extends IGCMenu {
    public IGCMenuCrateConfig(SpecializedCrates cc, Player p, IGCMenu lastMenu) {
        super(cc, p, lastMenu, "&7&l> &6&lCrateConfig.YML");
    }

    @Override
    public void openMenu() {

        FileHandler fu = getCc().getCrateConfigFile();
        FileConfiguration fc = fu.get();

        InventoryBuilder ib = createDefault(27);

        ib.setItem(18, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());

        ib.setItem(11,
                new ItemBuilder(Material.PAPER, 1, 0).setName("&aCSGO Animation").setLore("&7Animation name: &fINV_CSGO")
                        .addLore("").addLore("&7Used by crates: &f" + CrateAnimationType.INV_CSGO.getUses()));
        ib.setItem(12, new ItemBuilder(Material.PAPER, 1, 0).setName("&aRoulette Animation")
                .setLore("&7Animation name: &fINV_ROULETTE").addLore("")
                .addLore("&7Used by crates: &f" + CrateAnimationType.INV_ROULETTE.getUses()));
        ib.setItem(13,
                new ItemBuilder(Material.PAPER, 1, 0).setName("&aMenu Animation").setLore("&7Animation name: &fINV_MENU")
                        .addLore("").addLore("&7Used by crates: &f" + CrateAnimationType.INV_MENU.getUses()));
        ib.setItem(14, new ItemBuilder(Material.PAPER, 1, 0).setName("&aEnclose Animation")
                .setLore("&7Animation name: &fINV_ENCLOSE").addLore("")
                .addLore("&7Used by crates: &f" + CrateAnimationType.INV_ENCLOSE.getUses()));
        ib.setItem(15, new ItemBuilder(Material.PAPER, 1, 0).setName("&aDiscover Animation")
                .setLore("&7Animation name: &fINV_DISCOVER").addLore("")
                .addLore("&7Used by crates: &f" + CrateAnimationType.INV_DISCOVER.getUses()));
        ib.setItem(16, new ItemBuilder(Material.PAPER, 1, 0).setName("&aOpen Chest Animation")
                .setLore("&7Animation name: &fBLOCK_CRATEOPEN").addLore("")
                .addLore("&7Used by crates: &f" + CrateAnimationType.BLOCK_CRATEOPEN.getUses()));

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 18:
                up();
                break;
            case 0:
                getCc().getCrateConfigFile().save();
                ChatUtils.msgSuccess(getP(), "CrateConfig.YML saved!");
                break;
            case 9:
                reload();
                break;
            case 11:
                new IGCAnimCSGO(getCc(), getP(), this).open();
                break;
            case 12:
                new IGCAnimRoulette(getCc(), getP(), this).open();
                break;
            case 13:
                new IGCAnimMenu(getCc(), getP(), this).open();
                break;
            case 14:
                new IGCAnimEnclose(getCc(), getP(), this).open();
                break;
            case 15:
                new IGCAnimDiscover(getCc(), getP(), this).open();
                break;
            case 16:
                new IGCAnimOpenChest(getCc(), getP(), this).open();
                break;
        }

    }

    @Override
    public boolean handleInput(String value, String input) {
        Object type = getInputMenu().getType();
        if (type == Double.class) {
            if (Utils.isDouble(input)) {
                getCc().getCrateConfigFile().get().set(getPath(value), Double.valueOf(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            } else {
                ChatUtils.msgError(getP(), "This is not a valid decimal value, please try again.");
            }
        } else if (type == Integer.class) {
            if (Utils.isInt(input)) {
                getCc().getCrateConfigFile().get().set(getPath(value), Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            } else {
                ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
            }
        } else {
            if (value.equalsIgnoreCase("add Roulette.random-blocks") || value.equalsIgnoreCase("add CSGO.filler-blocks")) {
                try {
                    String[] split = input.split(";");
                    DynamicMaterial m = DynamicMaterial.fromString(input.toUpperCase());
                    if (Utils.isInt(split[1])) {
                        int id = Integer.parseInt(split[1]);
                        List<String> currentList = getCc().getCrateConfigFile().get().contains(getPath(value.substring(4))) ?
                                getCc().getCrateConfigFile().get().getStringList(getPath(value.substring(4))) :
                                new ArrayList<String>();
                        currentList.add(m.name() + ";" + id);
                        getCc().getCrateConfigFile().get().set(getPath(value.substring(4)), currentList);
                        return true;
                    } else {
                        ChatUtils.msgError(getP(), split[1] + " is not a valid number.");
                    }
                } catch (Exception exc) {
                    ChatUtils.msgError(getP(), input + " does not have a valid material or is not formatted MATERIAL;DATA");
                }
            } else if (value.equalsIgnoreCase("remove Roulette.random-blocks") ||
                    value.equalsIgnoreCase("remove CSGO.filler-blocks")) {
                if (getCc().getCrateConfigFile().get().contains(getPath(value.substring(7)))) {
                    boolean found = false;
                    List<String> newList = new ArrayList<>();
                    for (String s : getCc().getCrateConfigFile().get().getStringList(getPath(value.substring(7)))) {
                        if (s.equalsIgnoreCase(input)) {
                            found = true;
                        } else {
                            newList.add(s);
                        }
                    }

                    if (found) {
                        ChatUtils.msgSuccess(getP(), "Removed the " + input + " value.");
                        getCc().getCrateConfigFile().get().set(getPath(value.substring(7)), newList);
                        return true;
                    } else {
                        ChatUtils.msgError(getP(), input + " does not exist in the filler / random blocks: " +
                                getCc().getCrateConfigFile().get().getStringList(getPath(value.substring(7))));
                    }
                } else {
                    ChatUtils.msgError(getP(), "No filler blocks currently exist to remove.");
                    return true;
                }
            } else {
                getCc().getCrateConfigFile().get().set(getPath(value), input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
        }
        return false;
    }

    public String getPath(String value) {
        return "CrateType.Inventory." + value;
    }

    public String getValue(String crateType, String value) {
        FileHandler fu = getCc().getCrateConfigFile();
        FileConfiguration fc = fu.get();
        return fc.get("CrateType.Inventory." + crateType + "." + value).toString();
    }
}
