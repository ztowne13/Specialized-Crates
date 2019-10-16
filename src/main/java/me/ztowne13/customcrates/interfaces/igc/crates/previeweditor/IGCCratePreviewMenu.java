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
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
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

        isCustom = getCrates().getCs().getRewardDisplayType().equals(RewardDisplayType.CUSTOM);
    }

    @Override
    public void open()
    {
        InventoryBuilder ib = createDefault(18);

        ItemBuilder nameEditor = new ItemBuilder(DynamicMaterial.PAPER, 1);
        nameEditor.setDisplayName("&aInventory Name");
        nameEditor.addLore("&7Current Value:").addLore("&f" + getCrates().getCs().getDisplayer().getInvName());
        nameEditor.addLore("").addAutomaticLore("&7", 30, "Edit the name of the reward preview menu.");

        ItemBuilder typeEditor = new ItemBuilder(DynamicMaterial.BEACON, 1);
        typeEditor.setDisplayName("&aPreview Menu Type");
        typeEditor.addLore("&7Current Value:").addLore("&f" + getCrates().getCs().getRewardDisplayType().name());
        typeEditor.addLore("").addAutomaticLore("&7", 30,
                "Edit the type of display the reward preview menu will be: from sorted to completely custom made!");

        ItemBuilder forward = new ItemBuilder(DynamicMaterial.ARROW, 1);

        ItemBuilder backward = new ItemBuilder(DynamicMaterial.ARROW, 1);

        ItemBuilder customEditor = new ItemBuilder(isCustom ? DynamicMaterial.LADDER : DynamicMaterial.RED_DYE, 1);
        customEditor.setDisplayName((isCustom ? "&a" : "&4") + "Edit the Reward Preview");
        if (isCustom)
        {
            customEditor.addAutomaticLore("&7", 30, "Edit the reward preview menu to be exactly how you want!");
            customEditor.addLore("").addLore("&e&lClick &ea BLANK SLOT to").addLore("&eadd a reward OR forward /")
                    .addLore("&ebackward buttons.");

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
                getCrates().getCs().getFu().save();
                ChatUtils.msgSuccess(getP(), "Saved!");
                break;
            case 2:
                new InputMenu(getCc(), getP(), "inventory name", getCrates().getCs().getDisplayer().getInvName(),
                        String.class,
                        this, false);
                break;
            case 4:
                if(isCustom)
                    new IGCListSelector(getCc(), getP(), this, "preview menu type", Arrays.asList(RewardDisplayType.values()),
                        DynamicMaterial.PAPER, 1, RewardDisplayType.descriptions()).open();
                else
                    ChatUtils.msgError(getP(), "The preview menu type is not CUSTOM.");
                break;
            case 6:
                new IGCCratePreviewPageChooser(getCc(), getP(), getCrates(), this, 1).open();
                break;
            case 15:
                if(isCustom)
                {
                    CustomRewardDisplayer cdr = (CustomRewardDisplayer) getCrates().getCs().getDisplayer();
                    new IGCListSelector(getCc(), getP(), this, "Forward Button", new ArrayList<>(cdr.getItems().keySet()), DynamicMaterial.PAPER, 1,
                            cdr.getDescriptors(), new ArrayList<ItemBuilder>(cdr.getItems().values())).open();
                }
                else
                    ChatUtils.msgError(getP(), "The preview menu type is not CUSTOM.");
                break;
            case 16:
                if(isCustom)
                {
                    CustomRewardDisplayer cdr = (CustomRewardDisplayer) getCrates().getCs().getDisplayer();

                    new IGCListSelector(getCc(), getP(), this, "Backwards Button", new ArrayList<>(cdr.getItems().keySet()),
                            DynamicMaterial.PAPER, 1,
                            cdr.getDescriptors(), new ArrayList<ItemBuilder>(cdr.getItems().values())).open();
                }
                else
                    ChatUtils.msgError(getP(), "The preview menu type is not CUSTOM.");
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        FileConfiguration fc = getCs().getFu().get();

        if (value.equalsIgnoreCase("preview menu type"))
        {
            RewardDisplayType newType = RewardDisplayType.valueOf(input);
            getCrates().getCs().setRewardDisplayType(newType);
            getCrates().getCs().getFu().get().set("reward-display.type", input);
            ChatUtils.msgSuccess(getP(), "PLEASE RELOAD NOW. Set the preview menu type to '" + input + "'.");
        }
        else if (value.equalsIgnoreCase("inventory name"))
        {
            getCrates().getCs().getDisplayer().setName(input);
            getCrates().getCs().getFu().get().set("reward-display.name", ChatUtils.fromChatColor(input));
            ChatUtils.msgSuccess(getP(), "Set the inventory name to '" + input + "'");
            return true;
        }
        else if(value.equalsIgnoreCase("Forward Button"))
        {
            CustomRewardDisplayer cdr = (CustomRewardDisplayer) getCrates().getCs().getDisplayer();
            fc.set("reward-display.custom-display.nextpageitem", input);
            cdr.setNextPageItem(input);
            ChatUtils.msgSuccess(getP(), "Set the forward button to " + input);
        }
        else if(value.equalsIgnoreCase("Backwards Button"))
        {
            CustomRewardDisplayer cdr = (CustomRewardDisplayer) getCrates().getCs().getDisplayer();
            fc.set("reward-display.custom-display.lastpageitem", input);
            cdr.setPrevPageItem(input);
            ChatUtils.msgSuccess(getP(), "Set the backwards button to " + input);
        }
        return false;
    }
}
