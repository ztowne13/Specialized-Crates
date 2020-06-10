package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.InventoryUtils;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

/**
 * Created by ztowne13 on 4/3/16.
 */
public class IGCCrateActions extends IGCTierMenu
{
    List<String> actionTypes;
    List<String> descriptors;
    List<ItemBuilder> builders;
    boolean deleteMode = false;

    public IGCCrateActions(SpecializedCrates cc, Player p, IGCMenu lastMenu, Crate crates, String tier)
    {
        super(cc, p, lastMenu, "&7&l> &6&lActions", crates, tier);
        actionTypes = Arrays.asList(
                "MESSAGE",
                "BROADCAST",
                "TITLE",
                "SUBTITLE",
                "ACTIONBAR",
                "COMMAND",
                "PRE_MESSAGE",
                "PRE_BROADCAST",
                "PRE_TITLE",
                "PRE_SUBTITLE",
                "PRE_ACTIONBAR",
                "PRE_COMMAND"
                );

        descriptors = Arrays.asList(
                "Send a message to the player after the crate animation has been completed.",
                "Sends a broadcast to the server after the crate animation has been completed.",
                "Displays a title to the player after the crate animation has been completed.",
                "Displays a subtitle to the player after the crate animation has been completed.",
                "Displays an action bar to the player after the crate animation has been completed.",
                "Runs a command in console after the crate animation has been completed. To add per-reward commands, visit the rewards.yml.",
                "Send a message to the player immediately when the player begins opening the crate.",
                "Sends a broadcast to the server immediately when the player begins opening the crate.",
                "Displays a title to the player immediately when the player begins opening the crate.",
                "Displays a subtitle to the player immediately when the player begins opening the crate.",
                "Displays an action bar to the player immediately when the player begins opening the crate.",
                "Runs a command in console immediately when the player begins opening the crate. To add per-reward commands, visit the rewards.yml."
        );

        builders = Arrays.asList(
                new ItemBuilder(DynamicMaterial.PAPER),
                new ItemBuilder(DynamicMaterial.BEACON),
                new ItemBuilder(DynamicMaterial.WRITTEN_BOOK),
                new ItemBuilder(DynamicMaterial.BOOK),
                new ItemBuilder(DynamicMaterial.IRON_INGOT),
                new ItemBuilder(DynamicMaterial.COMMAND_BLOCK),
                new ItemBuilder(DynamicMaterial.PAPER),
                new ItemBuilder(DynamicMaterial.BEACON),
                new ItemBuilder(DynamicMaterial.WRITTEN_BOOK),
                new ItemBuilder(DynamicMaterial.BOOK),
                new ItemBuilder(DynamicMaterial.IRON_INGOT),
                new ItemBuilder(DynamicMaterial.COMMAND_BLOCK)
        );
    }

    @Override
    public void openMenu()
    {

        int count = 0;
        if (cs.getActions().getActions().containsKey(tier))
        {
            for (String actionType : cs.getActions().getActions().get(tier).keySet())
            {
                count += cs.getActions().getActions().get(tier).get(actionType).size();
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
        if (cs.getActions().getActions().containsKey(tier))
        {
            for (String actionType : cs.getActions().getActions().get(tier).keySet())
            {
                for (String actionMSG : cs.getActions().getActions().get(tier).get(actionType))
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
    public void handleClick(int slot)
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
//            new InputMenu(getCc(), getP(), "new action - type", "null", "Valid action types: " + actionTypes.toString(),
//                    String.class, this, true);
            new IGCListSelector(getCc(), getP(), this, "Actions", actionTypes, DynamicMaterial.PAPER, 1, descriptors, builders).open();
        }
        else if (getIb().getInv().getItem(slot) != null && getIb().getInv().getItem(slot).getType().equals(Material.BOOK))
        {
            if (deleteMode)
            {
                ItemMeta im = getIb().getInv().getItem(slot).getItemMeta();
                cs.getActions().removeEntry(ChatUtils.removeColor(im.getDisplayName()), im.getLore().get(0), tier);
                open();
            }
        }
    }

    String actionType;

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("Actions"))
        {
            if (actionTypes.contains(input.toUpperCase()))
            {
                actionType = input.toUpperCase();
                new InputMenu(getCc(), getP(), "new action - message", "null", "Placeholders: %name%, %nickname%, %rewards%, %crate%",
                        String.class, this, false);

                Bukkit.getScheduler().scheduleSyncDelayedTask(getCc(), new Runnable()
                {
                    @Override
                    public void run()
                    {
                        getP().closeInventory();
                    }
                }, 1);
            }
            else
            {
                ChatUtils.msgError(getP(), input + " is not a valid action type: " + actionTypes.toString());
            }
        }
        else if (value.equalsIgnoreCase("new action - message"))
        {
            cs.getActions().addEntry(actionType, input, tier);
            ChatUtils.msgSuccess(getP(),
                    "Added a new action with action type '" + actionType + "' and message '" + input + "'");
            return true;
        }
        return false;
    }
}
