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
public class IGCAnimOpenChest extends IGCAnimation {
    public IGCAnimOpenChest(SpecializedCrates cc, Player p, IGCMenu lastMenu) {
        super(cc, p, lastMenu, "&7&l> &6&lOpenChest Animation", CrateAnimationType.BLOCK_CRATEOPEN);
    }

    @Override
    public void openMenu() {

        InventoryBuilder ib = createDefault(9);


        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(3, new ItemBuilder(Material.PAPER, 1, 0).setName("&achest-open-duration")
                .addLore(getcVal()).addLore("&7" + getString("chest-open-duration")).addLore("")
                .addAutomaticLore("&f", 30, "How long the chest will appear to be open for"));
        ib.setItem(4, new ItemBuilder(DynamicMaterial.REDSTONE, 1).setName("&aearly-reward-hologram")
                .addLore(getcVal()).addLore("&7" + getString("early-reward-hologram"))
                .addLore("")
                .addAutomaticLore("&f", 30, "Should the reward-hologram play early with the animation for added effect?")
                .addLore("").addAutomaticLore("&e", 30, "Has no effect if reward-holo-attach-to-item is set to TRUE."));
        ib.setItem(5, new ItemBuilder(DynamicMaterial.REPEATER, 1).setName("&areward-hologram-delay")
                .addLore(getcVal()).addLore("&7" + getString("reward-hologram-delay"))
                .addLore("").addAutomaticLore("&f", 30,
                        "If displaying the early-reward-hologram, how long to wait for it to appear? Setting this to a value of 9 plays about when the item falls down.")
                .addLore("").addAutomaticLore("&e", 30, "Has no effect if reward-holo-attach-to-item is set to TRUE."));

        ItemBuilder attachTo = new ItemBuilder(DynamicMaterial.DIAMOND, 1);
        attachTo.setDisplayName("&areward-holo-attach-to-item");
        attachTo.addLore("&7Current value:").addLore("&7" + getString("reward-holo-attach-to-item"));
        attachTo.addLore("").addAutomaticLore("&f", 30,
                "Instead of just playing the reward hologram early or normally, have it attach to the actual item to have a nice bounce effect.");
        ib.setItem(6, attachTo);

        ItemBuilder earlyActions = new ItemBuilder(DynamicMaterial.FIREWORK_ROCKET, 1);
        earlyActions.setDisplayName("&aearly-open-actions");
        earlyActions.addLore("&7Current value:").addLore("&7" + getString("early-open-actions"));
        earlyActions.addLore("").addAutomaticLore("&f", 30,
                "Play the fireworks, sounds, actions, and particles when the crate is OPENED as opposed to after it closes.");
        ib.setItem(7, earlyActions);

        getIb().open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 0:
                up();
                break;
            case 3:
                new InputMenu(getCc(), getP(), "chest-open-duration", getString("chest-open-duration"),
                        "How long the chest will appear to be open for, in ticks (20 ticks per sec).", Integer.class, this);
                break;
            case 4:
                fc.set(getPath("early-reward-hologram"), !fc.getBoolean(getPath("early-reward-hologram")));
                open();
                break;
            case 5:
                new InputMenu(getCc(), getP(), "reward-hologram-delay", getString("reward-hologram-delay"),
                        "How long to delay (or not to delay) the early-reward-hologram from appearing.",
                        Integer.class, this);
                break;
            case 6:
                fc.set(getPath("reward-holo-attach-to-item"), !fc.getBoolean(getPath("reward-holo-attach-to-item")));
                open();
                break;
            case 7:
                fc.set(getPath("early-open-actions"), !fc.getBoolean(getPath("early-open-actions")));
                open();
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        Object type = getInputMenu().getType();
        if (type == Integer.class) {
            if (Utils.isInt(input)) {
                fc.set(getPath(value), Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            } else {
                ChatUtils.msgError(getP(), "This is not a valid number, please try again.");
            }
        } else if (type == Boolean.class) {
            if (Utils.isBoolean(input)) {
                fc.set(getPath(value), Boolean.parseBoolean(input));
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                return true;
            } else {
                ChatUtils.msgError(getP(), "This is not a valid true/false value, please try again.");
            }
        } else {
            fc.set(getPath(value), input);
            ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            return true;
        }
        return false;
    }
}
