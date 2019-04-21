package me.ztowne13.customcrates.interfaces.igc.crates.crateanimations;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 7/6/16.
 * <p>
 * inv-name: '&8&l> &6&l%crate%'
 * inventory-rows: 3
 * minimum-rewards: 1
 * maximum-rewards: 8
 */
public class IGCAnimMenu extends IGCAnimation
{
    public IGCAnimMenu(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lMenu Animation", CrateType.INV_MENU);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(9);


        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(2,
                new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").addLore(getcVal() + getString("inv-name"))
                        .addLore("").addAutomaticLore("&f", 30,
                        "The name of the inventory when the animation runs. This is overwritten by the crate's 'inv-name' value, if it exists."));
        ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&ainventory-rows")
                .addLore(getcVal()).addLore("&7" + getString("inventory-rows")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The amount of rows that the inventory will have for random items to spawn on."));
        ib.setItem(5, new ItemBuilder(Material.PAPER, 1, 0).setName("&aminimum-rewards")
                .addLore(getcVal()).addLore("&7" + getString("minimum-rewards")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The minimum amount of rewards that could appear in the menu. Set to the same amount as the maximum-rewards for it to be the same amount every time."));
        ib.setItem(6, new ItemBuilder(Material.PAPER, 1, 0).setName("&amaximum-rewards")
                .addLore(getcVal()).addLore("&7" + getString("maximum-rewards")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The maximum amount of rewards that could appear in the menu. Set to the same amount as the minimum-rewards for it to be the same amount every time."));

        getIb().open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 0:
                up();
                break;
            case 2:
                new InputMenu(getCc(), getP(), "inv-name", getString("inv-name"), "The name of the inventory", String.class,
                        this);
                break;
            case 4:
                new InputMenu(getCc(), getP(), "inventory-rows", getString("inventory-rows"),
                        "How many rows the menu crate has.", Integer.class, this);
                break;
            case 5:
                new InputMenu(getCc(), getP(), "minimum-rewards", getString("minimum-rewards"),
                        "The low end of the random amount of rewards that will spawn.", Integer.class, this);
                break;
            case 6:
                new InputMenu(getCc(), getP(), "maximum-rewards", getString("maximum-rewards"),
                        "The high end of the random amount of rewards that will spawn.", Integer.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        Object type = getInputMenu().getType();
        if (type == Integer.class)
        {
            if (Utils.isInt(input))
            {
                fc.set(getPath(value), Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
            }
        }
        else
        {
            fc.set(getPath(value), input);
            ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            return true;
        }
        return false;
    }
}
