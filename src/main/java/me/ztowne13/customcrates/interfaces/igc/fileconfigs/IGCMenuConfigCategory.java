package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.Settings;
import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuConfigCategory extends IGCMenu {
    ArrayList<Integer> slotsWithBoolean = new ArrayList<Integer>();

    ItemBuilder red;
    ItemBuilder green;
    SettingsValue.Category category;

    public IGCMenuConfigCategory(SpecializedCrates cc, Player p, IGCMenu lastMenu, SettingsValue.Category category) {
        super(cc, p, lastMenu, "&7&l> &6&l" + category.getShortTitle());

        this.category = category;
        red = new ItemBuilder(XMaterial.RED_WOOL, 1);
        green = new ItemBuilder(XMaterial.LIME_WOOL, 1);
    }

    @Override
    public void openMenu() {

        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(2, category.getAssociatedValues().size()));

        ib.setItem(ib.getInv().getSize() - 9, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 2;

        SettingsValue[] sortedObj = new SettingsValue[category.getAssociatedValues().size()];

        int back = 0;
        int forward = 0;
        for (SettingsValue settingsValue : category.getAssociatedValues()) {
            Object val = settingsValue.getValue(getCc());
            if (val instanceof Boolean) {
                sortedObj[forward] = settingsValue;
                forward++;
            } else {
                back++;
                sortedObj[sortedObj.length - back] = settingsValue;
            }
        }

        for (SettingsValue settingsValue : sortedObj) {
            if (i % 9 == 0) {
                i += 2;
            }

            Object val = settingsValue.getValue(getCc());
            String combinedDescriptor = "";
            String combinedDescriptorExtra = "";

            boolean isOntoExtra = false;

            for (String lore : settingsValue.getDescriptor()) {
                if (lore.startsWith("THE")) {
                    isOntoExtra = true;
                }

                if (isOntoExtra) {
                    if (combinedDescriptorExtra.equalsIgnoreCase("")) {
                        combinedDescriptorExtra = lore;
                    } else {
                        combinedDescriptorExtra += " " + lore;
                    }
                } else {
                    if (combinedDescriptor.equalsIgnoreCase("")) {
                        combinedDescriptor = lore;
                    } else {
                        combinedDescriptor += " " + lore;
                    }
                }
            }

            if (val instanceof Boolean) {
                ItemBuilder newBuilder = new ItemBuilder(((boolean) val ? green : red));
                newBuilder.setDisplayName("&a" + settingsValue.getEasyName());
                newBuilder.addLore("").addLore("&e" + settingsValue.getPath());
                newBuilder.addLore("&f&oCurrent value: &f" + val);
                newBuilder.addLore("");

                newBuilder.addAutomaticLore("&7", 35, combinedDescriptor);
                if (!combinedDescriptorExtra.equalsIgnoreCase("")) {
                    newBuilder.addLore("");
                    newBuilder.addAutomaticLore("&b", 35, combinedDescriptorExtra);
                }
                ib.setItem(i, newBuilder);
                slotsWithBoolean.add(i);
            } else {
                boolean isCollection = val instanceof Collection;
                XMaterial material;
                if (val instanceof Collection) {
                    material = XMaterial.LIGHT_BLUE_WOOL;
                } else if (settingsValue.getListValues() != null) {
                    material = XMaterial.MAGENTA_WOOL;
                } else {
                    material = XMaterial.ORANGE_WOOL;
                }

                ItemBuilder newBuilder = new ItemBuilder(material);
                newBuilder.setDisplayName("&a" + settingsValue.getEasyName());
                newBuilder.addLore("").addLore("&e" + settingsValue.getPath());
                if (isCollection) {
                    newBuilder.addLore("&f&oCurrent value: &f");
                    List<String> objects = (List<String>) val;
                    for (String obj : objects)
                        newBuilder.addLore(obj);
                } else {
                    newBuilder.addLore("&f&oCurrent value: &f" + val);
                }

                newBuilder.addLore("");
                newBuilder.addAutomaticLore("&7", 35, combinedDescriptor);
                if (!combinedDescriptorExtra.equalsIgnoreCase("")) {
                    newBuilder.addLore("");
                    newBuilder.addAutomaticLore("&b", 35, combinedDescriptorExtra);
                }

                ib.setItem(i, newBuilder);
            }
            i++;
        }


        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        Inventory inv = getIb().getInv();

        if (inv.getItem(slot) == null) {
            return;
        }

        ItemBuilder item = new ItemBuilder(inv.getItem(slot));
        if (slotsWithBoolean.contains(slot)) {
            SettingsValue sv = SettingsValue.getByPath(ChatUtils.removeColorFrom(item.getLore()).get(1));

            if (VersionUtils.Version.v1_12.isServerVersionOrEarlier()) {
                item.getStack().setDurability((byte) (item.getStack().getDurability() == 5 ? 14 : 5));
            } else {
                if (XMaterial.RED_WOOL.isSimilar(item.getStack()))
                    item.getStack().setType(XMaterial.LIME_WOOL.parseMaterial());
                else
                    item.getStack().setType(XMaterial.RED_WOOL.parseMaterial());
            }

            getIb().setItem(slot, item);

            sv.setValue(getCc(), item.getStack().getDurability() == 5 || XMaterial.LIME_WOOL.isSimilar(item.getStack()));

            open();
        } else if (XMaterial.ORANGE_WOOL.isSimilar(inv.getItem(slot))) {
            SettingsValue sv = SettingsValue.getByPath(ChatUtils.removeColorFrom(item.getLore()).get(1));

            new InputMenu(getCc(), getP(), sv.getPath(), sv.getValue(getCc()).toString(), sv.getObj(), this, !sv.isWithColor());
        } else if (XMaterial.LIGHT_BLUE_WOOL.isSimilar(inv.getItem(slot))) {
            SettingsValue sv = SettingsValue.getByPath(ChatUtils.removeColorFrom(item.getLore()).get(1));
            new IGCListEditor(getCc(), getP(), this, "inv-reward-item-lore", "Line", (List<String>) sv.getValue(getCc()),
                    XMaterial.BOOK, 1).open();
        } else if (XMaterial.MAGENTA_WOOL.isSimilar(inv.getItem(slot))) {
            SettingsValue sv = SettingsValue.getByPath(ChatUtils.removeColorFrom(item.getLore()).get(1));

            List<String> values;
            List<String> descriptors;

            if (sv.getListValues().length != 0) {
                values = Arrays.asList(sv.getListValues());
                descriptors = Arrays.asList(sv.getListValueDescriptors());
            } else {
                values = new ArrayList<>();
                descriptors = new ArrayList<>();
                for (Crate crate : Crate.getLoadedCrates().values()) {
                    if (crate.isMultiCrate()) {
                        values.add(crate.getName());
                        descriptors.add("Set the " + crate.getName() + " crate to be the multicrate that opens when a player types /crates.");
                    }
                }
            }

            new IGCListSelector(getCc(), getP(), this, sv.getPath(), values,
                    XMaterial.BOOK, 1, descriptors).open();
        } else {
            if (slot == getIb().getInv().getSize() - 9) {
                up();
            }
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        SettingsValue sv = SettingsValue.getByPath(value);
        Settings settings = getCc().getSettings();
        String path = sv.getPath();

        if (sv.getObj() == String.class) {
            sv.setValue(getCc(), input);
            ChatUtils.msgSuccess(getP(), "Set " + path + " to '" + input + "'");
            return true;
        } else if (sv.getObj() == Integer.class) {
            if (Utils.isInt(input)) {
                sv.setValue(getCc(), Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + path + " to '" + input + "'");
                return true;
            } else {
                ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
            }
        } else if (sv.getObj() == Double.class) {
            if (Utils.isDouble(input)) {
                sv.setValue(getCc(), input);
                ChatUtils.msgSuccess(getP(), "Set " + path + " to '" + input + "'");
                return true;
            } else {
                ChatUtils.msgError(getP(), "This is not a valid decimal value, please try again.");
            }
        }

        return false;
    }
}
