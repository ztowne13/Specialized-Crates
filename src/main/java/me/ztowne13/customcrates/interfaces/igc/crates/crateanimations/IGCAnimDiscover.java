package me.ztowne13.customcrates.interfaces.igc.crates.crateanimations;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class IGCAnimDiscover extends IGCAnimation
{
    public IGCAnimDiscover(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lDiscover Animation", CrateType.INV_DISCOVER);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(18);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(2,
                new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").setLore(getcVal() + getString("inv-name"))
                        .addLore("").addAutomaticLore("&f", 30,
                        "The name of the inventory when the animation runs. This is overwritten by the crate's 'inv-name' value, if it exists."));
        ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&ainventory-rows")
                .setLore(getcVal()).addLore("&7" + getString("inventory-rows")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The amount of rows in the inventory."));
        ib.setItem(5, new ItemBuilder(Material.PAPER, 1, 0).setName("&aminimum-rewards")
                .setLore(getcVal()).addLore("&7" + getString("minimum-rewards")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The minimum amount of rewards that could appear in the menu. Set to the same amount as the maximum-rewards for it to be the same amount every time."));
        ib.setItem(6, new ItemBuilder(Material.PAPER, 1, 0).setName("&amaximum-rewards")
                .setLore(getcVal()).addLore("&7" + getString("maximum-rewards")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The maximum amount of rewards that could appear in the menu. Set to the same amount as the minimum-rewards for it to be the same amount every time."));
        ib.setItem(7, new ItemBuilder(Material.PAPER, 1, 0).setName("&arandom-display-duration")
                .setLore(getcVal()).addLore("&7" + getString("random-display-duration")).addLore("")
                .addAutomaticLore("&f", 30, "The duration in which the 'shuffling' animation will play for."));

        boolean b = true;
        try
        {
            b = Boolean.valueOf(getString("count"));
        }
        catch (Exception exc)
        {

        }
        ib.setItem(11, new ItemBuilder(b ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1).setName("&acount")
                .setLore(getcVal()).addLore("&7" + b).addLore("")
                .addAutomaticLore("&f", 30,
                        "Whether or not the cover-block's should display numbers. (i.e. whether they should be stacked items)."));
        ib.setItem(12, new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&acover-block")
                .setLore(getcVal()).addLore("&7" + getString("cover-block")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The block that the player will have to click to chose the random reward."));

        ib.setItem(14, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&atick-sound")
                .setLore(getcVal()).addLore("&7" + getString("tick-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the inventory updates. Set to 'none' to have no sound."));
        ib.setItem(15, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&aclick-sound")
                .setLore(getcVal()).addLore("&7" + getString("click-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the player click's a cover block. Set to 'none' to have no sound."));
        ib.setItem(16, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&auncover-sound")
                .setLore(getcVal()).addLore("&7" + getString("uncover-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the player uncovers a reward. Set to 'none' to have no sound."));

        ib.open();
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
                new InputMenu(getCc(), getP(), "inv-name", getString("inv-name"), String.class, this);
                break;
            case 4:
                new InputMenu(getCc(), getP(), "inventory-rows", getString("inventory-rows"), Integer.class, this);
                break;
            case 5:
                new InputMenu(getCc(), getP(), "minimum-rewards", getString("minimum-rewards"), Integer.class, this);
                break;
            case 6:
                new InputMenu(getCc(), getP(), "maximum-rewards", getString("maximum-rewards"), Integer.class, this);
                break;
            case 7:
                new InputMenu(getCc(), getP(), "random-display-duration", getString("random-display-duration"),
                        "How many ticks the random display of green grass plane will run for.", Integer.class, this);
                break;
            case 11:
                boolean b = !Boolean.valueOf(getString("count"));
                fc.set(getPath("count"), b);
                getIb().setItem(20,
                        new ItemBuilder(b ? DynamicMaterial.LIME_WOOL : DynamicMaterial.RED_WOOL, 1).setName("&acount")
                                .setLore(getcVal() + b).addLore("").addLore("&7Do the 'cover-block's display numbers?"));
                break;
            case 12:
                new InputMenu(getCc(), getP(), "cover-block", getString("cover-block"), "Formatted: MATERIAL;DURABILITY",
                        String.class, this);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "tick-sound", getString("ticks-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, VOLUME, PITCH",
                        String.class, this);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "click-sound", getString("click-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, VOLUME, PITCH",
                        String.class, this);
                break;
            case 16:
                new InputMenu(getCc(), getP(), "uncover-sound", getString("uncover-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, VOLUME, PITCH", String.class, this);
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
        else if (type == Boolean.class)
        {
            if (Utils.isBoolean(input))
            {
                fc.set(getPath(value), Boolean.parseBoolean(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid true / false value, please try again.");
            }
        }
        else
        {

            if ((value.equalsIgnoreCase("tick-sound") || value.equalsIgnoreCase("click-sound") ||
                    value.equalsIgnoreCase("uncover-sound")) &&
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
