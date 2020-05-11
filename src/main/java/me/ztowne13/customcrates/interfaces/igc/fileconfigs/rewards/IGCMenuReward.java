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
    Reward reward;
    boolean unsavedChanges = false;

    public IGCMenuReward(SpecializedCrates cc, Player p, IGCMenu lastMenu, String rName)
    {
        super(cc, p, lastMenu, "&7&l> &6&l" + rName);

        if (CRewards.getAllRewards().keySet().contains(rName))
        {
            reward = CRewards.getAllRewards().get(rName);
        }
        else
        {
            reward = new Reward(getCc(), rName);
            reward.loadFromConfig();
            reward.loadChance();
            CRewards.allRewards.put(rName, reward);
        }
    }

    @Override
    public void open()
    {

        InventoryBuilder ib = createDefault(45);

        getIb().setItem(0, IGCDefaultItems.SAVE_BUTTON.getIb().setName("&aSave the reward to file")
                .setLore("&7Save your current changes"));
        getIb().setItem(36, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(4, new ItemBuilder(DynamicMaterial.LIGHT_BLUE_DYE, 1).setName("&a" + reward.getRewardName()).addLore("")
                .addAutomaticLore("&e", 40,
                        "The &6&ldisplay &6&lname &6&levery &6&lplayer &6&lsees &e is not NOT the reward name " +
                                "- that is only an identifier to add rewards to crates. To edit the actual reward display name, please do so in the " +
                                "display-item editor."));
        ib.setItem(8,
                new ItemBuilder(DynamicMaterial.RED_CARPET, 1).setName("&cDelete this reward")
                        .addAutomaticLore("&7", 30, "You will have to confirm before deleting."));

        //Commands
        ItemBuilder commands =
                new ItemBuilder(reward.getCommands() == null || reward.getCommands().isEmpty() ? DynamicMaterial.RED_DYE :
                        DynamicMaterial.COMMAND_BLOCK, 1)
                        .setName("&aEdit the commands")
                        .setLore("&7Current value: ");
        if (reward.getCommands() != null && !reward.getCommands().isEmpty())
        {
            for (String cmds : reward.getCommands())
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
        ib.setItem(22, commands);

        // Display item
        ItemBuilder displayItem = new ItemBuilder(reward.getDisplayBuilder().getStack());
        displayItem.clearLore();
        displayItem.setDisplayName("&aEdit the display item");
        displayItem.addLore("");
        displayItem.addAutomaticLore("&f", 40,
                "Edit things like the &edisplay &ename&f, lore, enchantments, potion effects, etc.");
        ib.setItem(11, displayItem);

        // Chance
        ib.setItem(23,
                new ItemBuilder(
                        reward.getChance() == null || reward.getChance() == 0 ? DynamicMaterial.RED_DYE :
                                DynamicMaterial.FISHING_ROD,
                        1)
                        .setName("&aEdit the chance").setLore("&7Current value: ")
                        .addLore("&7" + getName(reward.getChance() == 0 ? null : (reward.getChance() + ""))).addLore("")
                        .addAutomaticLore("&f", 30, "This is the actual chance of the lore."));

        // Rarity Level (Tier)
        ib.setItem(24,
                new ItemBuilder(DynamicMaterial.DIAMOND_BLOCK, 1)
                        .setName("&aEdit the rarity level")
                        .setLore("&7Current value: ")
                        .addLore("&7" + getName(reward.getRarity())).addLore("").addAutomaticLore("&f", 30,
                        "This is the 'tier' of the reward. If you aren't using tiers, ignore this."));

        // Give display item
        ItemBuilder giveDisplayItem = new ItemBuilder(DynamicMaterial.ITEM_FRAME, 1);
        giveDisplayItem.setDisplayName("&aGive the Display Item");
        giveDisplayItem.addLore("&7Current value: ");
        giveDisplayItem.addLore("&7" + reward.isGiveDisplayItem());
        giveDisplayItem.addLore("");
        giveDisplayItem.addAutomaticLore("&f", 30,
                "In addition to running the commands, should the player be given the display item when this reward is won?");
        ib.setItem(31, giveDisplayItem);

        // Give display item lore
        ItemBuilder giveDisplayItemLore = new ItemBuilder(DynamicMaterial.PAPER, 1);
        giveDisplayItemLore.setDisplayName("&aGive the Display Item with Lore");
        giveDisplayItemLore.addLore("&7Current value: ");
        giveDisplayItemLore.addLore("&7" + reward.isGiveDisplayItemLore());
        giveDisplayItemLore.addLore("");
        giveDisplayItemLore.addAutomaticLore("&f", 30,
                "IF the display item is being given to the player, should it include it's display-item lore?");
        ib.setItem(32, giveDisplayItemLore);

        // Give display item name
        ItemBuilder giveDisplayItemName = new ItemBuilder(DynamicMaterial.BOOK, 1);
        giveDisplayItemName.setDisplayName("&aGive the Display Item with its Name");
        giveDisplayItemName.addLore("&7Current value: ");
        giveDisplayItemName.addLore("&7" + reward.isGiveDisplayItemName());
        giveDisplayItemName.addLore("");
        giveDisplayItemName.addAutomaticLore("&f", 30,
                "IF the display item is being given to the player, should it include it's display-item name? or should the name be removed?");
        ib.setItem(33, giveDisplayItemName);

        // Fallback reward
        ItemBuilder fallbackReward = new ItemBuilder(DynamicMaterial.DIAMOND);
        fallbackReward.setDisplayName("&aFallback Reward Settings");
        fallbackReward.addLore("");
        fallbackReward.addAutomaticLore("&f", 30, "Specify the fallback reward permission and" +
                " the name of the fallback reward itself. A fallback reward is given IF a player has the correct permission. This is to" +
                " help prevent players from winning duplicate ranks, etc.");
        ib.setItem(29, fallbackReward);

        getIb().open();
        putInMenu();
    }

    @Override
    public void manageClick(int slot)
    {
        if (slot == 8)
        {
            String n = ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName());
            if (n.equalsIgnoreCase("Confirm deletion"))
            {
                reward.delete(true);
                up();
            }
            else
            {
                try
                {
                    ItemBuilder builder = new ItemBuilder(getIb().getInv().getItem(slot)).setName("&cConfirm deletion")
                            .setLore("&7Crates that use this reward:");
                    boolean none = true;
                    for (String s : reward.delete(false).replace("[", "").replace("]", "").split(", "))
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
        else if (slot == 22)
        {
            new IGCListEditor(getCc(), getP(), this, "Commands Editor", "Command", reward.getCommands(),
                    DynamicMaterial.COMMAND_BLOCK, 1).open();
        }
        else if (slot == 11)
        {
            new IGCItemEditor(getCc(), getP(), this, reward.getSaveBuilder()).open();
        }
        else if (slot == 23)
        {
            new InputMenu(getCc(), getP(), "chance", reward.getChance().toString(), Double.class, this);
        }
        else if (slot == 24)
        {
            new InputMenu(getCc(), getP(), "rarity", reward.getRarity(), String.class, this);
        }
        else if (slot == 31)
        {
            reward.setGiveDisplayItem(!reward.isGiveDisplayItem());
            open();
        }
        else if (slot == 32)
        {
            reward.setGiveDisplayItemLore(!reward.isGiveDisplayItemLore());
            open();
        }
        else if (slot == 33)
        {
            reward.setGiveDisplayItemName(!reward.isGiveDisplayItemName());
            open();
        }
        else if (slot == 29)
        {
            new IGCMenuFallbackReward(getCc(), getP(), this, reward).open();
        }
        else if (slot == 0)
        {
            ItemBuilder b = new ItemBuilder(getIb().getInv().getItem(slot));
            b.setName("&4&lERROR! &cPlease configure the");
            if (reward.getRewardName() == null)
            {
                b.setLore("&creward name.");
            }
            else if (reward.getSaveBuilder() == null)
            {
                b.setLore("&creward item.");
            }
            else if (reward.getChance() == null || reward.getChance() == 0)
            {
                b.setLore("&cchance.");
            }
            else if (reward.getRarity() == null)
            {
                b.setLore("&crarity.");
            }
            else
            {
                b.setName("&2SUCCESS");
                b.setLore("&7Please reload the plugin for").addLore("&7these changes to take effect.");
                reward.writeToFile();
            }
            getIb().setItem(slot, b);
            unsavedChanges = false;
            return;
        }
        else if (slot == 36)
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
                    reward.setChance(Integer.parseInt(input));
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
                    reward.setChance(Double.parseDouble(input));
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
                reward.getCommands().add(input.replace("/", ""));
                ChatUtils.msgSuccess(getP(), "Added '" + input + "' to the reward commands.");
            }
            else if (value.equalsIgnoreCase("removecommand"))
            {
                for (String s : reward.getCommands())
                {
                    if (s.equalsIgnoreCase(input))
                    {
                        reward.getCommands().remove(s);
                        ChatUtils.msgSuccess(getP(), "Removed '" + input + "' from the reward commands.");
                        return true;
                    }
                }
                ChatUtils.msgError(getP(), "'" + input + "' is not an existing command.");
                return false;
            }
            else if (value.equalsIgnoreCase("rarity"))
            {
                reward.setRarity(input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            }
            else if (value.equalsIgnoreCase("head-player-name"))
            {
                reward.getSaveBuilder().setPlayerHeadName(input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            }
        }
        return true;
    }

    public String getName(String val)
    {
        return reward == null || val == null ? "&cSet this value." : val;
    }

}
