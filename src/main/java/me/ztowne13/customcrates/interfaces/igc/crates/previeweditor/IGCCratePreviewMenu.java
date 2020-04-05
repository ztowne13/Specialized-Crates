package me.ztowne13.customcrates.interfaces.igc.crates.previeweditor;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.custom.CustomRewardDisplayer;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.crates.IGCMenuCrate;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class IGCCratePreviewMenu extends IGCMenuCrate
{
    boolean isCustom;
    public IGCCratePreviewMenu(SpecializedCrates specializedCrates, Player player, Crate crate, IGCMenu lastMenu)
    {
        super(specializedCrates, player, lastMenu, "&7&l> &6&lReward Preview Menu", crate);

        isCustom = getCrates().getSettings().getRewardDisplayType().equals(RewardDisplayType.CUSTOM);
    }

    @Override
    public void open()
    {
        InventoryBuilder ib = createDefault(18);

        ItemBuilder nameEditor = new ItemBuilder(DynamicMaterial.PAPER, 1);
        nameEditor.setDisplayName("&aInventory Name");
        nameEditor.addLore("&7Current Value:").addLore("&f" + getCrates().getSettings().getDisplayer().getInvName());
        nameEditor.addLore("").addAutomaticLore("&7", 30, "Edit the name of the reward preview menu.");

        ItemBuilder typeEditor = new ItemBuilder(DynamicMaterial.BEACON, 1);
        typeEditor.setDisplayName("&aPreview Menu Type");
        typeEditor.addLore("&7Current Value:").addLore("&f" + getCrates().getSettings().getRewardDisplayType().name());
        typeEditor.addLore("").addAutomaticLore("&7", 30,
                "Edit the type of display the reward preview menu will be: from sorted to completely custom made!");

        ItemBuilder forward = new ItemBuilder(DynamicMaterial.ARROW, 1);

        ItemBuilder backward = new ItemBuilder(DynamicMaterial.ARROW, 1);

        ItemBuilder customEditor = new ItemBuilder(isCustom ? DynamicMaterial.LADDER : DynamicMaterial.RED_DYE, 1);
        customEditor.setDisplayName((isCustom ? "&a" : "&4") + "Edit the Reward Preview");
        if (isCustom)
        {
            customEditor.addAutomaticLore("&7", 30, "Edit the reward preview menu to be exactly how you want!");
            customEditor.addLore("").addAutomaticLore("&e", 30, "PLEASE READ THE 'HELP' MESSAGE THAT APPEARS TO LEARN HOW TO CONFIGURE IT.");

            backward.setDisplayName("&aSelect the backward page button");
            backward.addAutomaticLore("&7", 30,
                    "Put an item in preview-menu editor that will be the backward button. Select that exact item here to assign it to be the 'backward page' button.");

            forward.setDisplayName("&aSelect the forward page button");
            forward.addAutomaticLore("&7", 30,
                    "Put an item in preview-menu editor that will be the forward button. Select that item exact here to assign it to be the 'forward page' button.");
        }
        else
        {
            customEditor.addAutomaticLore("&c", 30,
                    "To edit the preview menu manually, please set the Preview Menu Type to CUSTOM.");

            forward.addAutomaticLore("&c", 30,
                    "To edit the forward button manually, please set the Preview Menu Type to CUSTOM.");

            backward.addAutomaticLore("&c", 30,
                    "To edit the backward button manually, please set the Preview Menu Type to CUSTOM.");
        }

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(2, nameEditor);
        ib.setItem(4, typeEditor);
        ib.setItem(6, customEditor);
        ib.setItem(15, forward);
        ib.setItem(16, backward);

        ib.open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        switch (slot)
        {
            case 9:
                up();
                break;
            case 0:
                getCrates().getSettings().getFileHandler().save();
                ChatUtils.msgSuccess(getP(), "Saved!");
                break;
            case 2:
                new InputMenu(getCc(), getP(), "inventory name", getCrates().getSettings().getDisplayer().getInvName(),
                        String.class,
                        this, false);
                break;
            case 4:
                new IGCListSelector(getCc(), getP(), this, "preview menu type", Arrays.asList(RewardDisplayType.values()),
                        DynamicMaterial.PAPER, 1, RewardDisplayType.descriptions()).open();
                break;
            case 6:
            case 15:
            case 16:
                if(isCustom)
                {
                    CustomRewardDisplayer cdr;
                    try
                    {
                      cdr = (CustomRewardDisplayer) getCrates().getSettings().getDisplayer();
                    }
                    catch(Exception exc)
                    {
                        ChatUtils.msgError(getP(), "Please SAVE and RELOAD before editing.");
                        break;
                    }

                    if(slot == 6)
                    {
                        new IGCCratePreviewPageChooser(getCc(), getP(), getCrates(), this, 1).open();
                        for (int i = 0; i < 30; i++)
                            ChatUtils.msg(getP(), "");

                        ChatUtils.msg(getP(), "&4&lHow to configure the CUSTOM preview menu.");
                        ChatUtils.msg(getP(), "");
                        ChatUtils.msg(getP(), "&6&lCreating the menu.");
                        ChatUtils.msg(getP(),
                                "&eIt's very similar to just drag and drop but not exactly the same. Click any blank space " +
                                        "with nothing in hand to bring up a reward selector to add a reward to that spot. This is just a convenience" +
                                        " so that if you edit a rewards chance, name, lore, etc. in the future it will automatically update. You can" +
                                        " also add any normal items you want! To do this, hold an item in your hand and click or click and drag" +
                                        " it into the slots. It does not get placed into the inventory, it simply sets the slot you clicked to that" +
                                        " item. So if you only want one item at a slot, make sure the stack you're holding in your cursor" +
                                        " is only 1 item, not a stack. To remove items OR rewards, just click them with a blank cursor and they'll disappear." +
                                        " All of the items support any customizations that rewards do (names, lores, enchants, etc.)! Just make" +
                                        " sure you customize the items before you put them in the inventory!");
                        ChatUtils.msg(getP(), "&6&lMultiple page support");
                        ChatUtils.msg(getP(),
                                "&eIn this menu, click the 'paper' to add more pages to the preview menu. This is if " +
                                        "you have more than 54 rewards or if you want to make it really fancy!");
                        ChatUtils.msg(getP(), "&6&lForward / Backwards Buttons");
                        ChatUtils.msg(getP(),
                                "&eIf you only have one page, you can ignore all of this: these buttons are optional" +
                                        ". To navigate between these pages, you can add forward and backwards buttons! FIRST," +
                                        " make sure that you create the preview menu and add some item that will be the 'forward' and some item" +
                                        " that will be the 'backwards' button (THIS MEANS DESIGN IT ALL FIRST). Then, on the previous menu," +
                                        " click the arrow, and choose the item that will be the forward button and click the other arrow to " +
                                        "choose the item that will be the backwards button. This assigns that specific item to be the forward" +
                                        " or backward button - you can move it around wherever you want or have as many of them as you want!");
                        ChatUtils.msg(getP(), "&c&l!! &6&lPLEASE READ THE MESSAGE ABOVE. &c&l!!");
                        ChatUtils.msg(getP(), "&c&l!! &6&lPLEASE READ THE MESSAGE ABOVE. &c&l!!");
                    }
                    else if(slot == 15)
                    {
                        new IGCListSelector(getCc(), getP(), this, "Forward Button",
                                new ArrayList<>(cdr.getItems().keySet()), DynamicMaterial.PAPER, 1,
                                cdr.getDescriptors(), new ArrayList<ItemBuilder>(cdr.getItems().values())).open();
                    }
                    else if(slot == 16)
                    {
                        new IGCListSelector(getCc(), getP(), this, "Backwards Button", new ArrayList<>(cdr.getItems().keySet()),
                                DynamicMaterial.PAPER, 1,
                                cdr.getDescriptors(), new ArrayList<ItemBuilder>(cdr.getItems().values())).open();
                    }
                }
                else
                {
                    ChatUtils.msgError(getP(), "The preview menu type is not CUSTOM.");
                }
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        FileConfiguration fc = getCs().getFileHandler().get();

        if (value.equalsIgnoreCase("preview menu type"))
        {
            RewardDisplayType newType = RewardDisplayType.valueOf(input);
            getCrates().getSettings().setRewardDisplayType(newType);
            getCrates().getSettings().getFileHandler().get().set("reward-display.type", input);
            ChatUtils.msgSuccess(getP(), "PLEASE RELOAD NOW. Set the preview menu type to '" + input + "'.");
        }
        else if (value.equalsIgnoreCase("inventory name"))
        {
            getCrates().getSettings().getDisplayer().setName(input);
            getCrates().getSettings().getFileHandler().get().set("reward-display.name", ChatUtils.fromChatColor(input));
            ChatUtils.msgSuccess(getP(), "Set the inventory name to '" + input + "'");
            return true;
        }
        else if(value.equalsIgnoreCase("Forward Button"))
        {
            CustomRewardDisplayer cdr = (CustomRewardDisplayer) getCrates().getSettings().getDisplayer();
            fc.set("reward-display.custom-display.nextpageitem", input);
            cdr.setNextPageItem(input);
            ChatUtils.msgSuccess(getP(), "Set the forward button to " + input);
        }
        else if(value.equalsIgnoreCase("Backwards Button"))
        {
            CustomRewardDisplayer cdr = (CustomRewardDisplayer) getCrates().getSettings().getDisplayer();
            fc.set("reward-display.custom-display.lastpageitem", input);
            cdr.setPrevPageItem(input);
            ChatUtils.msgSuccess(getP(), "Set the backwards button to " + input);
        }
        return false;
    }
}
