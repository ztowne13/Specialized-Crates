package me.ztowne13.customcrates.interfaces.igc.crates.crateanimations;

import me.ztowne13.customcrates.SpecializedCrates;
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
 * inventory-rows: 2
 * fill-block: STAINED_GLASS_PANE;1
 * tick-sound: ENTITY_PLAYER_BIG_FALL, 5, 5
 * update-speed: 5
 * reward-amount: 1
 */
public class IGCAnimEnclose extends IGCAnimation
{
    public IGCAnimEnclose(SpecializedCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lEnclose Animation", CrateType.INV_ENCLOSE);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(18);


        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(2,
                new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").addLore(getcVal() + getString("inv-name"))
                        .addLore("").addAutomaticLore("&f", 30,
                        "The name of the inventory when the animation runs. This is overwritten by the crate's 'inv-name' value, if it exists."));
        ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&ainventory-rows")
                .addLore(getcVal()).addLore("&7" + getString("inventory-rows")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The amount of rows in addition to the center row, both top and bottom. Meaning, to have 3 rows TOTAL, this value would be 1: 1 row up + 1 row down + center row."));
        ib.setItem(5, new ItemBuilder(Material.PAPER, 1, 0).setName("&aupdate-speed")
                .addLore(getcVal()).addLore("&7" + getString("update-speed")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The delay between each frame (the speed at which the animation will update and play)."));
        ib.setItem(6, new ItemBuilder(Material.PAPER, 1, 0).setName("&areward-amount")
                .addLore(getcVal()).addLore("&7" + getString("reward-amount")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The amount of rewards that the player will receive from the crate. Must be an odd number of rewards, otherwise it will be rounded up."));
        ib.setItem(11, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&atick-sound")
                .addLore(getcVal()).addLore("&7" + getString("tick-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the inventory updates. Set to 'none' to have no sound."));
        ib.setItem(14, new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&afill-block")
                .addLore(getcVal()).addLore("&7" + getString("fill-block")).addLore("")
                .addAutomaticLore("&f", 30, "The block that will fill the empty space in the animation."));

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
                        "The amount of rows in addition to the center row, both top and bottom. Meaning, to have 3 rows TOTAL, this value would be 1: 1 row up + 1 row down + center row.",
                        Integer.class, this);
                break;
            case 5:
                new InputMenu(getCc(), getP(), "update-speed", getString("minimum-rewards"),
                        "How fast each reward disappears (in ticks).", Integer.class, this);
                break;
            case 6:
                new InputMenu(getCc(), getP(), "reward-amount", getString("maximum-rewards"),
                        "The amount of rewards that are left displayed and given to the player (must be an odd number).",
                        Integer.class, this);
                break;
            case 11:
                new InputMenu(getCc(), getP(), "tick-sound", getString("tick-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, PITCH, VOLUME. The sound played on every update.",
                        String.class, this, true);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "fill-block", getString("fill-block"),
                        "Formatted: MATERIAL;DURABILITY. The block that fills the empty spots", String.class, this, true);
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
            if (value.equalsIgnoreCase("tick-sound") &&
                    (input.equalsIgnoreCase("null") || input.equalsIgnoreCase("none")))
            {
                fc.set(getPath(value), null);
            }
            fc.set(getPath(value), input);
            ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            return true;
        }
        return false;
    }
}
