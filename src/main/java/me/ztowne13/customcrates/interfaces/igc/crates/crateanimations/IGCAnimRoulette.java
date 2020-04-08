package me.ztowne13.customcrates.interfaces.igc.crates.crateanimations;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 7/6/16.
 * <p>
 * inv-name: '&8&l> &6&l%crate%'
 * tick-sound: ENTITY_PLAYER_BIG_FALL, 5, 5
 * tile-update-ticks: 2
 * tick-speed-per-run: 3
 * final-crate-tick-length: 11
 * random-blocks:
 * - STAINED_GLASS_PANE;1
 * - STAINED_GLASS_PANE;2
 * - STAINED_GLASS_PANE;3
 * - STAINED_GLASS_PANE;4
 * - STAINED_GLASS_PANE;5
 * - STAINED_GLASS_PANE;6
 * - STAINED_GLASS_PANE;7
 * - STAINED_GLASS_PANE;8
 * - STAINED_GLASS_PANE;9
 */
public class IGCAnimRoulette extends IGCAnimation
{
    public IGCAnimRoulette(SpecializedCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lRoulette Animation", CrateAnimationType.INV_ROULETTE);
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(18);


        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(2,
                new ItemBuilder(Material.BOOK, 1, 0).setName("&ainv-name").addLore(getcVal())
                        .addLore("&7" + getString("inv-name")).addLore("").addAutomaticLore("&f", 30,
                        "The name of the inventory when the animation runs. This is overwritten by the crate's 'inv-name' value, if it exists."));
        ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&atick-speed-per-run")
                .addLore(getcVal()).addLore("&7" + getString("tick-speed-per-run")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The rate at which the rolling items will slow down. A lower value will make the speed of the roll" +
                                " take longer to slow down while a higher value will make the roll slow down much quicker."));
        ib.setItem(5, new ItemBuilder(Material.PAPER, 1, 0).setName("&afinal-crate-tick-length")
                .addLore(getcVal()).addLore("&7" + getString("final-crate-tick-length")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The speed the animation must reach to end. A lower value will result in the animation ending while" +
                                " the roll is still moving quickly, and a higher value will make it end when it is rolling slower."));
        ib.setItem(6, new ItemBuilder(Material.PAPER, 1, 0).setName("&atile-update-ticks")
                .addLore(getcVal()).addLore("&7" + getString("tile-update-ticks")).addLore("").addAutomaticLore("&f", 30,
                        "The delay between when the random-blocks update. A lower will make the random-blocks update very " +
                                "quickly, and a higher value update slowly."));
        ib.setItem(11, new ItemBuilder(Material.NOTE_BLOCK, 1, 0).setName("&atick-sound")
                .addLore(getcVal()).addLore("&7" + getString("tick-sound")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The sound that is played every time the inventory updates. Set to 'none' to have no sound."));

        ItemBuilder fillerBlocks =
                new ItemBuilder(Material.ENDER_CHEST, 1, 0).setName("&aAdd new random-blocks").setLore("&7Current values: ");
        for (String s : fc.getStringList(getPath("random-blocks")))
        {
            fillerBlocks.addLore("&7- " + s);
        }

        fillerBlocks.addLore("").addAutomaticLore("&f", 30, "The blocks that will fill empty space in the animation. Formatted Material;Data");
        ib.setItem(14, fillerBlocks);
        ib.setItem(15, fillerBlocks.setName("&aRemove existing random-blocks"));

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
                new InputMenu(getCc(), getP(), "tick-speed-per-run", getString("tick-speed-per-run"),
                        "How fast the animation updates the rewards.", Double.class, this);
                break;
            case 5:
                new InputMenu(getCc(), getP(), "final-crate-tick-length", getString("final-crate-tick-length"),
                        "How long the animation will display.", Double.class, this);
                break;
            case 6:
                new InputMenu(getCc(), getP(), "tile-update-ticks", getString("tile-update-ticks"),
                        "How fast, in ticks, the filler-blocks will update", Integer.class, this);
                break;
            case 11:
                new InputMenu(getCc(), getP(), "tick-sound", getString("tick-sound"),
                        "Set to 'none' to have no sound. Formatted 'SOUND, PITCH, VOLUME' (without the quotes). Click for a list of sounds -> https://www.spigotmc.org/wiki/cc-sounds-list/",
                        String.class, this, true);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "add random-blocks", "Formated: MATERIAL;ID", String.class, this, true);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "remove random-blocks", "Existing filler-blocks: " +
                        (fc.contains(crateAnimationType.getPrefix() + ".filler-blocks") ?
                                fc.getStringList(crateAnimationType.getPrefix() + ".filler-blocks") : "none"), String.class, this, true);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        Object type = getInputMenu().getType();
        if (type == Double.class)
        {
            if (Utils.isDouble(input))
            {
                fc.set(getPath(value), Double.valueOf(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid decimal value, please try again.");
            }
        }
        else if (type == Integer.class)
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
            if (value.equalsIgnoreCase("add random-blocks"))
            {
                try
                {
                    DynamicMaterial dm = DynamicMaterial.fromString(input);
                    List<String> currentList =
                            fc.contains(getPath(value.substring(4))) ? fc.getStringList(getPath(value.substring(4))) :
                                    new ArrayList<String>();
                    currentList.add(dm.name());
                    fc.set(getPath(value.substring(4)), currentList);
                    return true;
                }
                catch (Exception exc)
                {
                    ChatUtils.msgError(getP(), input + " does not have a valid material/data value or is not formatted MATERIAL;DATA");
                }
            }
            else if (value.equalsIgnoreCase("remove random-blocks"))
            {
                if (fc.contains(getPath(value.substring(7))))
                {
                    boolean found = false;
                    List<String> newList = new ArrayList<>();
                    for (String s : fc.getStringList(getPath(value.substring(7))))
                    {
                        if (s.equalsIgnoreCase(input))
                        {
                            found = true;
                        }
                        else
                        {
                            newList.add(s);
                        }
                    }

                    if (found)
                    {
                        ChatUtils.msgSuccess(getP(), "Removed the " + input + " value.");
                        fc.set(getPath(value.substring(7)), newList);
                        return true;
                    }
                    else
                    {
                        ChatUtils.msgError(getP(), input + " does not exist in the filler / random blocks: " +
                                fc.getStringList(getPath(value.substring(7))));
                    }
                }
                else
                {
                    ChatUtils.msgError(getP(), "No filler blocks currently exist to remove.");
                    return true;
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
        }
        return false;
    }
}
