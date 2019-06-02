package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.Settings;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuConfig extends IGCMenu
{
    ArrayList<Integer> slotsWithBoolean = new ArrayList<Integer>();

    ItemBuilder red;
    ItemBuilder green;

    public IGCMenuConfig(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lConfig.YML");

        red = new ItemBuilder(DynamicMaterial.RED_WOOL, 1);
        green = new ItemBuilder(DynamicMaterial.LIME_WOOL, 1);
    }

    @Override
    public void open()
    {

        HashMap<String, Object> map = getCc().getSettings().getConfigValues();
        InventoryBuilder ib =
                createDefault(InventoryUtils.getRowsFor(2, getCc().getSettings().getConfigValues().keySet().size()));

        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(ib.getInv().getSize() - 9, IGCDefaultItems.EXIT_BUTTON.getIb());

        int i = 2;

        String[] sortedObj = new String[map.keySet().size()];

        int back = 0;
        int forward = 0;
        for (String s : map.keySet())
        {
            Object val = map.get(s);
            if (val instanceof Boolean)
            {
                sortedObj[forward] = s;
                forward++;
            }
            else
            {
                back++;
                sortedObj[sortedObj.length - back] = s;
            }
        }

        for (String sv : sortedObj)
        {
            if (i % 9 == 0)
            {
                i += 2;
            }


            if (map.get(sv) instanceof Boolean)
            {
                ItemBuilder newBuilder =
                        ((boolean) map.get(sv) ? green : red).setName("&a" + sv).setLore("&e&oCurrent value: " + map.get(sv))
                                .addLore("");
                for (String lore : SettingsValues.getByPath(sv).getDescriptor())
                {
                    newBuilder.addLore("&7" + lore);
                }
                ib.setItem(i, newBuilder);
                slotsWithBoolean.add(i);
            }
            else
            {
                boolean isCollection = map.get(sv) instanceof Collection;

                ItemBuilder newBuilder =
                        new ItemBuilder(!isCollection ? DynamicMaterial.ORANGE_WOOL : DynamicMaterial.LIGHT_GRAY_WOOL, 1)
                                .setName("&a" + sv);
                if (isCollection)
                {
                    newBuilder.setLore("&e&oCurrent value: ");
                    List<String> objects = (List<String>) map.get(sv);
                    for (String obj : objects)
                        newBuilder.addLore(obj);
                }
                else
                {
                    newBuilder.setLore("&e&oCurrent value: " + map.get(sv));
                }

                newBuilder.addLore("");

                for (String lore : SettingsValues.getByPath(sv).getDescriptor())
                {
                    newBuilder.addLore("&7" + lore);
                }
                ib.setItem(i, newBuilder);
            }
            i++;
        }


        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        Inventory inv = getIb().getInv();
        if (slotsWithBoolean.contains(slot))
        {
            ItemBuilder newBuilder = new ItemBuilder(inv.getItem(slot));
            SettingsValues sv = SettingsValues.getByPath(newBuilder.getName(true));

            if (NMSUtils.Version.v1_12.isServerVersionOrEarlier())
            {
                newBuilder.getStack().setDurability((byte) (newBuilder.getStack().getDurability() == 5 ? 14 : 5));
            }
            else
            {
                if (DynamicMaterial.RED_WOOL.isSameMaterial(newBuilder.getStack()))
                    newBuilder.getStack().setType(DynamicMaterial.LIME_WOOL.parseMaterial());
                else
                    newBuilder.getStack().setType(DynamicMaterial.RED_WOOL.parseMaterial());
            }

            getIb().setItem(slot, newBuilder);

            getCc().getSettings().getConfigValues().put(sv.getPath(), newBuilder.getStack().getDurability() == 5 ||
                    DynamicMaterial.LIME_WOOL.isSameMaterial(newBuilder.getStack()));
            open();
        }
        else if (DynamicMaterial.ORANGE_WOOL.isSameMaterial(inv.getItem(slot)))
        {
            ItemStack item = inv.getItem(slot);
            SettingsValues sv = SettingsValues.getByPath(new ItemBuilder(item).getName(true));

            new InputMenu(getCc(), getP(), sv.getPath(), sv.getValue(getCc()).toString(), sv.getObj(), this, true);
        }
        else if (DynamicMaterial.LIGHT_GRAY_WOOL.isSameMaterial(inv.getItem(slot)))
        {
            ItemStack item = inv.getItem(slot);
            SettingsValues sv = SettingsValues.getByPath(new ItemBuilder(item).getName(true));
            new IGCListEditor(getCc(), getP(), this, "inv-reward-item-lore", "Line", (List<String>) sv.getValue(getCc()),
                    DynamicMaterial.BOOK, 1).open();
        }
        else
        {
            if (slot == 0)
            {
                getCc().getSettings().writeSettingsValues();
                ChatUtils.msgSuccess(getP(), "Config.YML saved!");
            }
            else if (slot == 9)
            {
                reload();
            }
            else if (slot == getIb().getInv().getSize() - 9)
            {
                up();
            }
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        SettingsValues sv = SettingsValues.getByPath(value);
        Settings settings = getCc().getSettings();
        String path = sv.getPath();

        if (sv.getObj() == String.class)
        {
            settings.getConfigValues().put(sv.getPath(), input);
            ChatUtils.msgSuccess(getP(), "Set " + path + " to '" + input + "'");
            return true;
        }
        else if (sv.getObj() == Integer.class)
        {
            if (Utils.isInt(input))
            {
                settings.getConfigValues().put(path, Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + path + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
            }
        }
        else if (sv.getObj() == Double.class)
        {
            if (Utils.isDouble(input))
            {
                settings.getConfigValues().put(path, input);
                ChatUtils.msgSuccess(getP(), "Set " + path + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid decimal value, please try again.");
            }
        }

        return false;
    }
}
