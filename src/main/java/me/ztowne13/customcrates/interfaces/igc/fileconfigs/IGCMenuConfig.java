package me.ztowne13.customcrates.interfaces.igc.fileconfigs;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 3/11/16.
 */
public class IGCMenuConfig extends IGCMenu
{
    ArrayList<Integer> slotsWithBoolean = new ArrayList<Integer>();

    ItemBuilder red;
    ItemBuilder green;

    public IGCMenuConfig(SpecializedCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lConfig.YML");

        red = new ItemBuilder(DynamicMaterial.RED_WOOL, 1);
        green = new ItemBuilder(DynamicMaterial.LIME_WOOL, 1);
    }

    @Override
    public void openMenu()
    {
        InventoryBuilder ib = createDefault(27);

        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(9, IGCDefaultItems.RELOAD_BUTTON.getIb());
        ib.setItem(ib.getInv().getSize() - 9, IGCDefaultItems.EXIT_BUTTON.getIb());

        int slotCounter = 2;
        for(SettingsValue.Category category : SettingsValue.Category.values())
        {
            if((slotCounter + 1) % 9 == 0)
            {
                slotCounter += 3;
            }

            ItemBuilder builder = new ItemBuilder(DynamicMaterial.BOOK);
            builder.setDisplayName("&a" + category.getTitle());
            builder.addAutomaticLore("&7", 30, category.getDescription()[0]);
            builder.addLore("");
            builder.addAutomaticLore("&e", 30, category.getDescription()[1]);

            ib.setItem(slotCounter, builder);

            slotCounter++;
        }

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot)
    {
        Inventory inv = getIb().getInv();
        if(inv.getItem(slot) == null || inv.getItem(slot).getType().equals(Material.AIR))
        {
            return;
        }

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
        else
        {
            String itemName = ChatUtils.removeColor(inv.getItem(slot).getItemMeta().getDisplayName());
            for(SettingsValue.Category category : SettingsValue.Category.values())
            {
                if (itemName.equalsIgnoreCase(category.getTitle()))
                {
                    new IGCMenuConfigCategory(getCc(), getP(), this, category).open();
                    return;
                }
            }
        }

    }

    @Override
    public boolean handleInput(String value, String input)
    {
        return false;
    }
}
