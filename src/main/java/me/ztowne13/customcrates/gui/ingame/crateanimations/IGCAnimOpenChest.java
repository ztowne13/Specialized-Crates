package me.ztowne13.customcrates.gui.ingame.crateanimations;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.types.CrateType;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
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
public class IGCAnimOpenChest extends IGCAnimation
{
    public IGCAnimOpenChest(CustomCrates cc, Player p, IGCMenu lastMenu)
    {
        super(cc, p, lastMenu, "&7&l> &6&lOpenChest Animation", CrateType.BLOCK_CRATEOPEN);
    }

    @Override
    public void open()
    {
        getP().closeInventory();
        putInMenu();

        InventoryBuilder ib = createDefault(9);


        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(3, new ItemBuilder(Material.PAPER, 1, 0).setName("&achest-open-duration")
                .addLore(getcVal()).addLore("&7" + getString("chest-open-duration")).addLore("")
                .addAutomaticLore("&f", 30, "How long the chest will appear to be open for"));
        ib.setItem(4, new ItemBuilder(Material.PAPER, 1, 0).setName("&aearly-reward-hologram")
                .addLore(getcVal()).addLore("&7" + getString("early-reward-hologram")).addLore("")
                .addAutomaticLore("&f", 30, "Should the reward-hologram play early with the animation for added effect?"));
        ib.setItem(5, new ItemBuilder(Material.PAPER, 1, 0).setName("&areward-hologram-delay")
                .addLore(getcVal()).addLore("&7" + getString("reward-hologram-delay")).addLore("").addAutomaticLore("&f", 30,
                        "If displaying the early-reward-hologram, how long to wait for it to appear? Setting this to a value of 9 plays about when the item falls down."));

        getIb().open();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 0:
                up();
                break;
            case 3:
                new InputMenu(getCc(), getP(), "chest-open-duration", getString("chest-open-duration"),
                        "How long the chest will appear to be open for, in ticks (20 ticks per sec).", Integer.class, this);
                break;
            case 4:
                new InputMenu(getCc(), getP(), "early-reward-hologram", getString("early-reward-hologram"),
                        "Should the reward hologram play early for added effect (true/false).", Boolean.class, this);
                break;
            case 5:
                new InputMenu(getCc(), getP(), "reward-hologram-delay", getString("reward-hologram-delay"),
                        "How long to delay (or not to delay) the early-reward-hologram from appearing.",
                        Integer.class, this);
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
                ChatUtils.msgError(getP(), "This is not a valid true/false value, please try again.");
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
