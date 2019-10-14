package me.ztowne13.customcrates.interfaces.igc.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.options.rewards.displaymenu.RewardDisplayType;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class IGCCratePreviewMenu extends IGCMenuCrate
{
    public IGCCratePreviewMenu(SpecializedCrates specializedCrates, Player player, Crate crate, IGCMenu lastMenu)
    {
        super(specializedCrates, player, lastMenu, "&7&l> &6&lReward Preview Menu", crate);
    }

    @Override
    public void open()
    {
        InventoryBuilder ib = createDefault(18);

        ItemBuilder nameEditor = new ItemBuilder(DynamicMaterial.PAPER, 1);
        nameEditor.setDisplayName("&aInventory Name");
        nameEditor.addLore("&7Current Value:").addLore("&f" + crates.getCs().getDisplayer().getInvName());
        nameEditor.addLore("").addAutomaticLore("&7", 30, "Edit the name of the reward preview menu.");

        ItemBuilder typeEditor = new ItemBuilder(DynamicMaterial.BEACON, 1);
        typeEditor.setDisplayName("&aPreview Menu Type");
        typeEditor.addLore("&7Current Value:").addLore("&f" + crates.getCs().getRewardDisplayType().name());
        typeEditor.addLore("").addAutomaticLore("&7", 30,
                "Edit the type of display the reward preview menu will be: from sorted to completely custom made!");

        ItemBuilder customEditor = new ItemBuilder(DynamicMaterial.LADDER, 1);
        customEditor.setDisplayName("&aEdit the Reward Preview");
        if (crates.getCs().getRewardDisplayType().equals(RewardDisplayType.CUSTOM))
            customEditor.addAutomaticLore("&7", 30, "Edit the reward preview menu to be exactly how you want!");
        else
            customEditor.addAutomaticLore("&c", 30,
                    "To edit the preview menu manually, please set the Preview Menu Type to CUSTOM.");

        ib.setItem(9, IGCDefaultItems.EXIT_BUTTON.getIb());
        ib.setItem(0, IGCDefaultItems.SAVE_ONLY_BUTTON.getIb());
        ib.setItem(11, nameEditor);
        ib.setItem(13, typeEditor);
        ib.setItem(15, customEditor);

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
                crates.getCs().getFu().save();
                ChatUtils.msgSuccess(getP(), "Saved!");
                break;
            case 11:
                new InputMenu(getCc(), getP(), "inventory name", crates.getCs().getDisplayer().getInvName(), String.class,
                        this, false);
                break;
            case 13:
                new IGCListSelector(getCc(), getP(), this, "preview menu type", Arrays.asList(RewardDisplayType.values()),
                        DynamicMaterial.PAPER, 1, RewardDisplayType.descriptions()).open();
                break;
            case 15:

                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        if (value.equalsIgnoreCase("preview menu type"))
        {
            RewardDisplayType newType = RewardDisplayType.valueOf(input);
            crates.getCs().setRewardDisplayType(newType);
            crates.getCs().getFu().get().set("reward-display.type", input);
            ChatUtils.msgSuccess(getP(), "Set the preview menu type to '" + input + "'. Reload for changes to take effect.");
        }
        else if (value.equalsIgnoreCase("inventory name"))
        {
            crates.getCs().getDisplayer().setName(input);
            crates.getCs().getFu().get().set("reward-display.name", ChatUtils.fromChatColor(input));
            ChatUtils.msgSuccess(getP(), "Set the inventory name to '" + input + "'");
            return true;
        }
        return false;
    }
}
