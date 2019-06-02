package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ztowne13 on 4/3/16.
 */
public class IGCCrateActions extends IGCTierMenu
{
    List<String> actionTypes = new ArrayList<>();
    boolean deleteMode = false;

    public IGCCrateActions(CustomCrates cc, Player p, IGCMenu lastMenu, Crate crates, String tier)
    {
        super(cc, p, lastMenu, "&7&l> &6&lActions", crates, tier);
        actionTypes = Arrays.asList(
                "MESSAGE",
                "BROADCAST",
                "TITLE",
                "SUBTITLE",
                "ACTIONBAR",
                "PRE_MESSAGE",
                "PRE_BROADCAST",
                "PRE_TITLE",
                "PRE_SUBTITLE",
                "PRE_ACTIONBAR"
        );
    }

    @Override
    public void open()
    {

        int count = 0;
        if (cs.getCa().getActions().containsKey(tier))
        {
            for (String actionType : cs.getCa().getActions().get(tier).keySet())
            {
                count += cs.getCa().getActions().get(tier).get(actionType).size();
            }
        }

        InventoryBuilder ib = createDefault(InventoryUtils.getRowsFor(4, count) + 9, 18);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        if (!deleteMode)
        {
            getIb().setItem(8,
                    new ItemBuilder(DynamicMaterial.RED_CARPET.parseMaterial(), 1, 14).setName("&aEnable 'delete' mode")
                            .setLore("&7By enabling 'delete' mode").addLore("&7you can just click on actions")
                            .addLore("&7to remove "));
        }
        else
        {
            getIb().setItem(8,
                    new ItemBuilder(DynamicMaterial.RED_CARPET.parseMaterial(), 1, 14).setName("&cDisable 'delete' mode")
                            .setLore("&7This will stop you from").addLore("&7removing actions"));
        }
        ib.setItem(17, new ItemBuilder(Material.PAPER, 1, 0).setName("&aAdd a new action"));

        int i = 2;
        if (cs.getCa().getActions().containsKey(tier))
        {
            for (String actionType : cs.getCa().getActions().get(tier).keySet())
            {
                for (String actionMSG : cs.getCa().getActions().get(tier).get(actionType))
                {
                    if (i % 9 == 7)
                    {
                        i += 4;
                    }

                    ib.setItem(i, new ItemBuilder(Material.BOOK, 1, 0).setName("&a" + actionType).setLore(actionMSG));
                    i++;
                }
            }
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        if (slot == 0)
        {
            up();
        }
        else if (slot == 8)
        {
            deleteMode = !deleteMode;
            if (!deleteMode)
            {
                getIb().setItem(8,
                        new ItemBuilder(DynamicMaterial.RED_CARPET.parseMaterial(), 1, 14).setName("&aEnable 'delete' mode")
                                .setLore("&7By enabling 'delete' mode").addLore("&7you can just click on rewards")
                                .addLore("&7to remove "));
            }
            else
            {
                getIb().setItem(8,
                        new ItemBuilder(DynamicMaterial.RED_CARPET.parseMaterial(), 1, 14).setName("&cDisable 'delete' mode")
                                .setLore("&7This will stop you from").addLore("&7removing rewards"));
            }
        }
        else if (slot == 17)
        {
            new InputMenu(getCc(), getP(), "new action - type", "null", "Valid action types: " + actionTypes.toString(),
                    String.class, this, true);
        }
        else if (getIb().getInv().getItem(slot) != null && getIb().getInv().getItem(slot).getType().equals(Material.BOOK))
        {
            if (deleteMode)
            {
                ItemMeta im = getIb().getInv().getItem(slot).getItemMeta();
                cs.getCa().removeEntry(ChatUtils.removeColor(im.getDisplayName()), im.getLore().get(0), tier);
                open();
            }
        }
    }

    String actionType;

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("new action - type"))
        {
            if (actionTypes.contains(input.toUpperCase()))
            {
                actionType = input.toUpperCase();
                new InputMenu(getCc(), getP(), "new action - message", "null", "What message would you like displayed?",
                        String.class, this, true);
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid action type: " + actionTypes.toString());
            }
        }
        else if (value.equalsIgnoreCase("new action - message"))
        {
            cs.getCa().addEntry(actionType, input, tier);
            ChatUtils.msgSuccess(getP(),
                    "Added a new action with action type '" + actionType + "' and message '" + input + "'");
            return true;
        }
        return false;
    }
}
