package me.ztowne13.customcrates.interfaces.igc.crates.crateanimations;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.types.animations.CrateAnimationType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class IGCAnimDiscover extends IGCAnimation {
    public IGCAnimDiscover(SpecializedCrates cc, Player p, IGCMenu lastMenu) {
        super(cc, p, lastMenu, "&7&l> &6&lDiscover Animation", CrateAnimationType.INV_DISCOVER);
    }

    @Override
    public void openMenu() {

        InventoryBuilder ib = createDefault(27);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(2,
                new ItemBuilder(XMaterial.BOOK).setDisplayName("&ainv-name").setLore(getcVal() + getString("inv-name"))
                        .addLore("").addAutomaticLore("&f", 30,
                        "The name of the inventory when the animation runs. This is overwritten by the crate's 'inv-name' value, if it exists."));
        ib.setItem(4, new ItemBuilder(XMaterial.PAPER).setDisplayName("&ainventory-rows")
                .setLore(getcVal()).addLore("&7" + getString("inventory-rows")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The amount of rows in the inventory."));
        ib.setItem(5, new ItemBuilder(XMaterial.PAPER).setDisplayName("&aminimum-rewards")
                .setLore(getcVal()).addLore("&7" + getString("minimum-rewards")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The minimum amount of rewards that could appear in the menu. Set to the same amount as the maximum-rewards for it to be the same amount every time."));
        ib.setItem(6, new ItemBuilder(XMaterial.PAPER).setDisplayName("&amaximum-rewards")
                .setLore(getcVal()).addLore("&7" + getString("maximum-rewards")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The maximum amount of rewards that could appear in the menu. Set to the same amount as the minimum-rewards for it to be the same amount every time."));
        ib.setItem(7, new ItemBuilder(XMaterial.PAPER).setDisplayName("&arandom-display-duration")
                .setLore(getcVal()).addLore("&7" + getString("random-display-duration")).addLore("")
                .addAutomaticLore("&f", 30, "The duration in which the 'shuffling' animation will play for."));

        boolean b = true;
        try {
            b = Boolean.parseBoolean(getString("count"));
        } catch (Exception exc) {

        }
        ib.setItem(11, new ItemBuilder(b ? XMaterial.LIME_WOOL : XMaterial.RED_WOOL).setDisplayName("&acount")
                .setLore(getcVal()).addLore("&7" + b).addLore("")
                .addAutomaticLore("&f", 30,
                        "Whether or not the cover-block's should display numbers. (i.e. whether they should be stacked items)."));
        ib.setItem(12, new ItemBuilder(XMaterial.ENDER_CHEST).setDisplayName("&acover-block")
                .setLore(getcVal()).addLore("&7" + getString("cover-block")).addLore("")
                .addAutomaticLore("&f", 30,
                        "The block that the player will have to click to chose the random reward."));

        ib.setItem(14, new ItemBuilder(XMaterial.NOTE_BLOCK).setDisplayName("&atick-sound")
                .setLore(getcVal()).addLore("&7" + getString("tick-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the inventory updates. Set to 'none' to have no sound."));
        ib.setItem(15, new ItemBuilder(XMaterial.NOTE_BLOCK).setDisplayName("&aclick-sound")
                .setLore(getcVal()).addLore("&7" + getString("click-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the player click's a cover block. Set to 'none' to have no sound."));
        ib.setItem(16, new ItemBuilder(XMaterial.NOTE_BLOCK).setDisplayName("&auncover-sound")
                .setLore(getcVal()).addLore("&7" + getString("uncover-sound")).addLore("").addAutomaticLore("&f", 30,
                        "The sound that is played every time the player uncovers a reward. Set to 'none' to have no sound."));

        // cover-block-name
        ItemBuilder coverBlockName = new ItemBuilder(XMaterial.PAPER, 1);
        coverBlockName.setDisplayName("&acover-block-name");
        coverBlockName.addLore(getcVal()).addLore("&7" + getString("cover-block-name")).addLore("");
        coverBlockName.addAutomaticLore("&f", 30, "The name of all the blocks before the user clicks anything. Use %number% for the number reward it is.");

        ItemBuilder coverBlockLore = new ItemBuilder(XMaterial.PAPER, 1);
        coverBlockLore.setDisplayName("&acover-block-lore");
        coverBlockLore.addLore(getcVal()).addLore("&7" + getString("cover-block-lore")).addLore("");
        coverBlockLore.addAutomaticLore("&f", 30, "The lore of all the blocks before the user clicks them. Use %remaining-clicks% for the number of rewards remaining for the player to choose.");

        ItemBuilder rewardBlock = new ItemBuilder(XMaterial.LIME_STAINED_GLASS, 1);
        rewardBlock.setDisplayName("&areward-block");
        rewardBlock.addLore(getcVal()).addLore("&7" + getString("reward-block")).addLore("");
        rewardBlock.addAutomaticLore("&f", 30, "The block that will be shown when a cover block is clicked.");

        ItemBuilder rewardBlockName = new ItemBuilder(XMaterial.PAPER, 1);
        rewardBlockName.setDisplayName("&areward-block-name");
        rewardBlockName.addLore(getcVal()).addLore("&7" + getString("reward-block-name")).addLore("");
        rewardBlockName.addAutomaticLore("&f", 30, "The name of the reward block when it's shuffling.");

        ItemBuilder rewardBlockWaitingName = new ItemBuilder(XMaterial.PAPER, 1);
        rewardBlockWaitingName.setDisplayName("&areward-block-waiting-name");
        rewardBlockWaitingName.addLore(getcVal()).addLore("&7" + getString("reward-block-waiting-name")).addLore("");
        rewardBlockWaitingName.addAutomaticLore("&f", 30, "The name of the reward block while it's waiting for the player to select all their rewards.");

        ItemBuilder rewardBlockUnlockName = new ItemBuilder(XMaterial.PAPER, 1);
        rewardBlockUnlockName.setDisplayName("&areward-block-unlock-name");
        rewardBlockUnlockName.addLore(getcVal()).addLore("&7" + getString("reward-block-unlock-name")).addLore("");
        rewardBlockUnlockName.addAutomaticLore("&f", 30, "The name of the reward block when the user is supposed to get their final reward.");

        ib.setItem(19, coverBlockName);
        ib.setItem(20, coverBlockLore);

        ib.setItem(22, rewardBlock);
        ib.setItem(23, rewardBlockWaitingName);
        ib.setItem(24, rewardBlockName);
        ib.setItem(25, rewardBlockUnlockName);

        ib.open();

        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
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
                boolean b = !Boolean.parseBoolean(getString("count"));
                fc.set(getPath("count"), b);
                getIb().setItem(20,
                        new ItemBuilder(b ? XMaterial.LIME_WOOL : XMaterial.RED_WOOL, 1).setDisplayName("&acount")
                                .setLore(getcVal() + b).addLore("").addLore("&7Do the 'cover-block's display numbers?"));
                break;
            case 12:
                new InputMenu(getCc(), getP(), "cover-block", getString("cover-block"), "Formatted: MATERIAL;DURABILITY",
                        String.class, this, true);
                break;
            case 14:
                new InputMenu(getCc(), getP(), "tick-sound", getString("ticks-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, VOLUME, PITCH",
                        String.class, this, true);
                break;
            case 15:
                new InputMenu(getCc(), getP(), "click-sound", getString("click-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, VOLUME, PITCH",
                        String.class, this, true);
                break;
            case 16:
                new InputMenu(getCc(), getP(), "uncover-sound", getString("uncover-sound"),
                        "Set to 'none' to have no sound. Formatted: SOUND, VOLUME, PITCH", String.class, this, true);
                break;
            case 19:
                new InputMenu(getCc(), getP(), "cover-block-name", getString("cover-block-name"),
                        String.class, this);
                break;
            case 20:
                new InputMenu(getCc(), getP(), "cover-block-lore", getString("cover-block-lore"),
                        "User %remaining-clicks% for the amount of rewards left.", String.class, this);
                break;
            case 22:
                new InputMenu(getCc(), getP(), "reward-block", getString("reward-block"),
                        "The name of the material of the reward block!", String.class, this, true);
                break;
            case 23:
                new InputMenu(getCc(), getP(), "reward-block-waiting-name", getString("reward-block-waiting-name"),
                        String.class, this);
                break;
            case 24:
                new InputMenu(getCc(), getP(), "reward-block-name", getString("reward-block-name"),
                        String.class, this);
                break;
            case 25:
                new InputMenu(getCc(), getP(), "reward-block-unlock-name", getString("reward-block-unlock-name"),
                        String.class, this);
                break;
        }
    }

    //
    //        ib.setItem(19, coverBlockName);
    //        ib.setItem(20, coverBlockLore);
    //
    //        ib.setItem(22, rewardBlock);
    //        ib.setItem(23, rewardBlockWaitingName);
    //        ib.setItem(24, rewardBlockName);
    //        ib.setItem(25, rewardBlockUnlockName);

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
                ChatUtils.msgError(getP(), "This is not a valid true / false value, please try again.");
            }
        } else {

            if ((value.equalsIgnoreCase("tick-sound") || value.equalsIgnoreCase("click-sound") ||
                    value.equalsIgnoreCase("uncover-sound")) &&
                    (input.equalsIgnoreCase("null") || input.equalsIgnoreCase("none"))) {
                fc.set(getPath(value), null);
            }

            fc.set(getPath(value), input);
            ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            return true;
        }
        return false;
    }
}
