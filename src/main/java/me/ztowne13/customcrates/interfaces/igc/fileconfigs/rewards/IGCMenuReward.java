package me.ztowne13.customcrates.interfaces.igc.fileconfigs.rewards;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.igc.items.IGCItemEditor;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 3/22/16.
 */
public class IGCMenuReward extends IGCMenu
{
    Reward r;
    boolean unsavedChanges = false;

    public IGCMenuReward(SpecializedCrates cc, Player p, IGCMenu lastMenu, String rName)
    {
        super(cc, p, lastMenu, "&7&l> &6&l" + rName);

        if (CRewards.getAllRewards().keySet().contains(rName))
        {
            r = CRewards.getAllRewards().get(rName);
        }
        else
        {
            r = new Reward(getCc(), rName);
            r.loadFromConfig();
            r.loadChance();
            CRewards.allRewards.put(rName, r);
        }
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(36);

        getIb().setItem(0, IGCDefaultItems.SAVE_BUTTON.getIb().setName("&aSave the reward to file")
                .setLore("&7Save your current changes"));
        getIb().setItem(27, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(4, new ItemBuilder(DynamicMaterial.LIGHT_BLUE_DYE, 1).setName("&a" + r.getRewardName()).addLore("")
                .addAutomaticLore("&e", 40,
                        "The 'displayname' is the &6&ldisplay &6&lname &6&levery &6&lplayer &6&lsees &eNOT the reward's " +
                                "name - that is only an identifier to add rewards to crates. To edit it, please do so in the " +
                                "display-item editor."));
        ib.setItem(8,
                new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDelete this reward")
                        .addAutomaticLore("&7", 30, "You will have to confirm before deleting."));

        //Commands
        ItemBuilder commands =
                new ItemBuilder(r.getCommands() == null || r.getCommands().isEmpty() ? DynamicMaterial.RED_DYE :
                        DynamicMaterial.COMMAND_BLOCK, 1)
                        .setName("&aEdit the commands")
                        .setLore("&7Current value: ");
        if (r.getCommands() != null && !r.getCommands().isEmpty())
        {
            for (String cmds : r.getCommands())
            {
                commands.addLore("&7" + cmds);
            }
        }
        else
        {
            commands.addLore(getName(null));
        }
        commands.addLore("").addAutomaticLore("&f", 30,
                "Use %player% as a placeholder for the player's name. Use %amountX-Y% for a random number between X and Y. Ex: %amount1000-10000%");
        ib.setItem(13, commands);

        // Display item
        ib.setItem(11,
                (r == null || r.getDisplayBuilder() == null ? new ItemBuilder(DynamicMaterial.RED_DYE, 1) :
                        new ItemBuilder(r.getDisplayBuilder().getStack())).clearLore().setName("&aEdit the display item.")
                        .clearLore()
                        .addAutomaticLore("&f", 40,
                                "Edit the display item, including the 'displayname' which is the name &edisplayed &eto &eplayers&f. " +
                                        "That value is different from the name used to create the reward - that value cannot be edited " +
                                        "and that is only an identifier to add rewards to crates."));

        // Chance
        ib.setItem(14,
                new ItemBuilder(
                        r.getChance() == null || r.getChance() == 0 ? DynamicMaterial.RED_DYE : DynamicMaterial.FISHING_ROD,
                        1)
                        .setName("&aEdit the chance").setLore("&7Current value: ")
                        .addLore("&7" + getName(r.getChance() == 0 ? null : (r.getChance() + ""))).addLore("")
                        .addAutomaticLore("&f", 30, "This is the actual chance of the lore."));

        // Rarity Level (Tier)
        ib.setItem(15,
                new ItemBuilder(DynamicMaterial.DIAMOND_BLOCK, 1)
                        .setName("&aEdit the rarity level")
                        .setLore("&7Current value: ")
                        .addLore("&7" + getName(r.getRarity())).addLore("").addAutomaticLore("&f", 30,
                        "This is the 'tier' of the reward. If you aren't using tiers, ignore this."));

        // Give display item
        ItemBuilder giveDisplayItem = new ItemBuilder(DynamicMaterial.ITEM_FRAME, 1);
        giveDisplayItem.setDisplayName("&aGive the Display Item");
        giveDisplayItem.addLore("&7Current value: ");
        giveDisplayItem.addLore("&7" + r.isGiveDisplayItem());
        giveDisplayItem.addLore("");
        giveDisplayItem.addAutomaticLore("&f", 30,
                "In addition to running the commands, should the player be given the display item when this reward is won?");
        ib.setItem(22, giveDisplayItem);

        // Give display item lore
        ItemBuilder giveDisplayItemLore = new ItemBuilder(DynamicMaterial.PAPER, 1);
        giveDisplayItemLore.setDisplayName("&aGive the Display Item with Lore");
        giveDisplayItemLore.addLore("&7Current value: ");
        giveDisplayItemLore.addLore("&7" + r.isGiveDisplayItemLore());
        giveDisplayItemLore.addLore("");
        giveDisplayItemLore.addAutomaticLore("&f", 30,
                "IF the display item is being given to the player, should it include it's display-item lore?");
        ib.setItem(23, giveDisplayItemLore);

        ItemBuilder giveDisplayItemName = new ItemBuilder(DynamicMaterial.BOOK, 1);
        giveDisplayItemName.setDisplayName("&aGive the Display Item with its Name");
        giveDisplayItemName.addLore("&7Current value: ");
        giveDisplayItemName.addLore("&7" + r.isGiveDisplayItemName());
        giveDisplayItemName.addLore("");
        giveDisplayItemName.addAutomaticLore("&f", 30,
                "IF the display item is being given to the player, should it include it's display-item name? or should the name be removed?");
        ib.setItem(24, giveDisplayItemName);


        getIb().open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
		/*if(slot == 4)
		{
			setInputMenu(new InputMenu(getCc(), getP(), "rewardname", r.getRewardName(), String.class, this));
			getInputMenu().initMsg();
		}
		else */
        if (slot == 8)
        {
            String n = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            if (n.equalsIgnoreCase("Confirm deletion"))
            {
                r.delete(true);
                up();
            }
            else
            {
                try
                {
                    ItemBuilder builder = new ItemBuilder(getIb().getInv().getItem(slot)).setName("&cConfirm deletion")
                            .setLore("&7Crates that use this reward:");
                    boolean none = true;
                    for (String s : r.delete(false).replace("[", "").replace("]", "").split(", "))
                    {
                        none = false;
                        builder.addLore("&7" + s);
                    }
                    if (none)
                    {
                        builder.addLore("&7none");
                    }
                    getIb().setItem(slot, builder);
                }
                catch (Exception exc)
                {
                    exc.printStackTrace();
                }
            }
        }
//        if (slot == 11)
//        {
//            new InputMenu(getCc(), getP(), "displayname", r.getDisplayName(), String.class, this);
//        }
//        else if (slot == 11)
//        {
//            getP().closeInventory();
//            new IGCListEditor(getCc(), getP(), this, "Lore Editor", "Line", r.getCustomLore(), DynamicMaterial.BOOK, 1)
//                    .open();
//        }
        else if (slot == 13)
        {
            new IGCListEditor(getCc(), getP(), this, "Commands Editor", "Command", r.getCommands(),
                    DynamicMaterial.COMMAND_BLOCK, 1).open();
        }
        else if (slot == 11)
        {
            new IGCItemEditor(getCc(), getP(), this, r.getSaveBuilder()).open();
        }
        else if (slot == 14)
        {
            new InputMenu(getCc(), getP(), "chance", r.getChance().toString(), Double.class, this);
        }
        else if (slot == 15)
        {
            new InputMenu(getCc(), getP(), "rarity", r.getRarity(), String.class, this);
        }
        else if (slot == 22)
        {
            r.setGiveDisplayItem(!r.isGiveDisplayItem());
            open();
        }
        else if (slot == 23)
        {
            r.setGiveDisplayItemLore(!r.isGiveDisplayItemLore());
            open();
        }
        else if (slot == 24)
        {
            r.setGiveDisplayItemName(!r.isGiveDisplayItemName());
            open();
        }
        else if (slot == 0)
        {
            ItemBuilder b = new ItemBuilder(getIb().getInv().getItem(slot));
            b.setName("&4&lERROR! &cPlease configure the");
            if (r.getRewardName() == null)
            {
                b.setLore("&creward name.");
            }
            else if (r.getSaveBuilder() == null)
            {
                b.setLore("&creward item.");
            }
            else if (r.getChance() == null || r.getChance() == 0)
            {
                b.setLore("&cchance.");
            }
            else if (r.getRarity() == null)
            {
                b.setLore("&crarity.");
            }
            else
            {
                b.setName("&2SUCCESS");
                b.setLore("&7Please reload the plugin for").addLore("&7these changes to take effect.");
                r.writeToFile();
            }
            getIb().setItem(slot, b);
            unsavedChanges = false;
            return;
        }
        else if (slot == 27)
        {
            if (!unsavedChanges || ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName())
                    .equalsIgnoreCase("Are you sure?"))
            {
                up();
            }
            else
            {
                getIb().setItem(27, new ItemBuilder(getIb().getInv().getItem(slot)).setName("&4Are you sure?")
                        .setLore("&cYou have unsaved changes.").addLore("&7The changes will only be")
                        .addLore("&7temporary if not saved later").addLore("&7and will delete upon reload."));
            }
        }
        else
        {
            unsavedChanges = false;
            return;
        }

        unsavedChanges = true;
    }

    @Override
    public boolean handleInput(String value, String input)
    {
        Object type = getInputMenu().getType();
        if (type == Integer.class)
        {
            if (Utils.isInt(input))
            {
                if (value.equalsIgnoreCase("chance"))
                {
                    r.setChance(Integer.parseInt(input));
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                }
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid number.");
                return false;
            }
        }
        if (type == Double.class)
        {
            if (Utils.isDouble(input))
            {
                if (value.equalsIgnoreCase("chance"))
                {
                    r.setChance(Double.parseDouble(input));
                    ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
                }
            }
            else
            {
                ChatUtils.msgError(getP(), "This is not a valid number.");
                return false;
            }
        }
        else
        {
			/*if(value.equalsIgnoreCase("rewardname"))
			{
				if(!input.contains(" "))
				{
					r.setRewardName(input);
					ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
					return true;
				}
				ChatUtils.msgError(getP(), input + " cannot have any spaces in it.");
				return false;
			}*/
            if (value.equalsIgnoreCase("addcommand"))
            {
                r.getCommands().add(input.replace("/", ""));
                ChatUtils.msgSuccess(getP(), "Added '" + input + "' to the reward commands.");
            }
            else if (value.equalsIgnoreCase("removecommand"))
            {
                for (String s : r.getCommands())
                {
                    if (s.equalsIgnoreCase(input))
                    {
                        r.getCommands().remove(s);
                        ChatUtils.msgSuccess(getP(), "Removed '" + input + "' from the reward commands.");
                        return true;
                    }
                }
                ChatUtils.msgError(getP(), "'" + input + "' is not an existing command.");
                return false;
            }
            else if (value.equalsIgnoreCase("rarity"))
            {
                r.setRarity(input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            }
            else if (value.equalsIgnoreCase("head-player-name"))
            {
                r.getSaveBuilder().setPlayerHeadName(input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            }
        }
        return true;
    }

    public String getName(String val)
    {
        return r == null || val == null ? "&cSet this value." : val;
    }

}
