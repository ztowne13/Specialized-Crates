package me.ztowne13.customcrates.gui.ingame.rewards;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.crates.options.CRewards;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.gui.InventoryBuilder;
import me.ztowne13.customcrates.gui.ItemBuilder;
import me.ztowne13.customcrates.gui.dynamicmenus.InputMenu;
import me.ztowne13.customcrates.gui.ingame.IGCDefaultItems;
import me.ztowne13.customcrates.gui.ingame.IGCListEditor;
import me.ztowne13.customcrates.gui.ingame.IGCMenu;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ztowne13 on 3/22/16.
 */
public class IGCMenuReward extends IGCMenu
{
    Reward r;
    boolean unsavedChanges = false;

    public IGCMenuReward(CustomCrates cc, Player p, IGCMenu lastMenu, String rName)
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
        getP().closeInventory();
        putInMenu();

        InventoryBuilder ib = createDefault(27);

        getIb().setItem(0, IGCDefaultItems.SAVE_BUTTON.getIb().setName("&aSave the reward to file")
                .setLore("&7Save your current changes"));
        getIb().setItem(18, IGCDefaultItems.EXIT_BUTTON.getIb());

        ib.setItem(4, new ItemBuilder(DynamicMaterial.LIGHT_BLUE_DYE, 1).setName("&a" + r.getRewardName()));
        ib.setItem(8,
                new ItemBuilder(DynamicMaterial.RED_CARPET.parseMaterial(), 1, 14).setName("&cDelete this reward")
                        .setLore("&7You will have confirmation").addLore("&7before deleting."));

        ib.setItem(10,
                new ItemBuilder(r.getDisplayName() == null ? DynamicMaterial.ROSE_RED : DynamicMaterial.PAPER, 1)
                        .setLore("&7Current value: ").addLore("&f" + getName(r.getDisplayName()))
                        .setName("&aSet the display name."));

        ItemBuilder commands =
                new ItemBuilder(r.getCommands() == null ? DynamicMaterial.ROSE_RED : DynamicMaterial.COMMAND_BLOCK, 1)
                        .setName("&aEdit the commands")
                        .setLore("&7Current value: ");
        if (commands.getStack().getType() != DynamicMaterial.INK_SAC.parseMaterial())
        {
            for (String cmds : r.getCommands())
            {
                commands.addLore("&f" + cmds);
            }
        }
        ib.setItem(12, commands);

        ItemBuilder lore = new ItemBuilder(
                r.getCustomLore() == null || r.getCustomLore().isEmpty() ? DynamicMaterial.ROSE_RED :
                        DynamicMaterial.WRITABLE_BOOK, 1).setName("&aEdit the lore")
                .setLore("&7Current value: ");
        if (lore.getStack().getType() != DynamicMaterial.INK_SAC.parseMaterial())
        {
            for (String loreLine : r.getCustomLore())
            {
                lore.addLore(loreLine);
            }
        }
        else
        {
            lore.addLore("&6&lNote: &eCurrently using default").addLore("&elore from config.yml");
        }

        ib.setItem(11, lore);

        ib.setItem(13,
                (r == null || r.getDisplayItem() == null ? new ItemBuilder(DynamicMaterial.ROSE_RED, 1) :
                        new ItemBuilder(r.getDisplayItem())).setName("&aSet the display item.")
                        .setLore("&7By clicking this object you will").addLore("&7set the display item to your")
                        .addLore("&7item you are currently holding."));
        ib.setItem(14,
                new ItemBuilder(r.getChance() == null ? DynamicMaterial.ROSE_RED : DynamicMaterial.FISHING_ROD, 1)
                        .setName("&aSet the chance").setLore("&7Current value: ")
                        .addLore("&f" + getName(r.getChance() + "")));
        ib.setItem(15,
                new ItemBuilder(r.getRarity() == null ? DynamicMaterial.ROSE_RED : DynamicMaterial.DIAMOND_BLOCK, 1)
                        .setName("&aSet the rarity level").setLore("&7Current value: ")
                        .addLore("&f" + getName(r.getRarity())));
        ib.setItem(16, new ItemBuilder(DynamicMaterial.DARK_OAK_FENCE, 1)
                .setName("&cSet the receive-limit").setLore("&cThis value is currently non-usable."));


        if (r.getDisplayItem() != null && DynamicMaterial.PLAYER_HEAD.isSameMaterial(r.getDisplayItem()))
        {
            ib.setItem(22, new ItemBuilder(DynamicMaterial.NAME_TAG, 1).setName("&aSet the player-head name")
                    .setLore("&7Current value:").addLore("&f" + getName(r.getHeadName())));
        }

        getIb().open();
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
        if (slot == 10)
        {
            new InputMenu(getCc(), getP(), "displayname", r.getDisplayName(), String.class, this);
        }
        else if (slot == 11)
        {
            getP().closeInventory();
            new IGCListEditor(getCc(), getP(), this, "Lore Editor", "Line", r.getCustomLore(), DynamicMaterial.BOOK, 1)
                    .open();
        }
        else if (slot == 12)
        {
            getP().closeInventory();
            new IGCListEditor(getCc(), getP(), this, "Commands Editor", "Command", r.getCommands(),
                    DynamicMaterial.COMMAND_BLOCK, 1).open();
        }
        else if (slot == 13)
        {
            ItemStack stack = getP().getItemInHand();
            if (stack == null || stack.getType() == DynamicMaterial.AIR.parseMaterial())
            {
                getIb().setItem(slot, new ItemBuilder(getIb().getInv().getItem(slot)).setName("&cNo item in hand"));
                ChatUtils.msgError(getP(), "You do not have an item in your hand!");
            }
            else
            {
                ItemBuilder ib = new ItemBuilder(getP().getItemInHand());
                ib.setName("&aSet the display item.").setLore("&7By clicking this object you will")
                        .addLore("&7set the display item to your").addLore("&7item you are currently holding.");
                getIb().setItem(slot, ib);
                r.setDisplayItem(ib.get());
            }

            if (DynamicMaterial.PLAYER_HEAD.isSameMaterial(r.getDisplayItem()))
            {
                getIb().setItem(22, new ItemBuilder(DynamicMaterial.NAME_TAG, 1).setName("&aSet the player-head name")
                        .setLore("&7Current value:").addLore("&f" + getName(r.getHeadName())));
            }
        }
        else if (slot == 14)
        {
            new InputMenu(getCc(), getP(), "chance", r.getChance().toString(), Integer.class, this);
        }
        else if (slot == 15)
        {
            new InputMenu(getCc(), getP(), "rarity", r.getRarity(), String.class, this);
        }
        else if (slot == 16)
        {
            ChatUtils.msgError(getP(), "This value is currently unusable and non-settable.");
            unsavedChanges = false;
            return;
        }
        else if(slot == 22)
        {
            new InputMenu(getCc(), getP(), "head-player-name", r.getHeadName(), String.class, this);
        }
        else if (slot == 0)
        {
            ItemBuilder b = new ItemBuilder(getIb().getInv().getItem(slot));
            b.setName("&4&lERROR! &cPlease configure the");
            if (r.getRewardName() == null)
            {
                b.setLore("&creward name.");
            }
            else if (r.getDisplayItem() == null)
            {
                b.setLore("&creward item.");
            }
            else if (r.getChance() == null)
            {
                b.setLore("&cchance.");
            }
            else if (r.getRarity() == null)
            {
                b.setLore("&crarity.");
            }
            else if (r.getCommands() == null || r.getCommands().isEmpty())
            {
                b.setLore("&ccommands.");
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
        else if (slot == 18)
        {
            if (!unsavedChanges || ChatUtils.removeColor(getIb().getInv().getItem(slot).getItemMeta().getDisplayName())
                    .equalsIgnoreCase("Are you sure?"))
            {
                up();
            }
            else
            {
                getIb().setItem(18, new ItemBuilder(getIb().getInv().getItem(slot)).setName("&4Are you sure?")
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
            if (value.equalsIgnoreCase("displayname"))
            {
                r.setDisplayName(input);
                ChatUtils.msgSuccess(getP(), "Set " + value + " to '" + input + "'");
            }
            else if (value.equalsIgnoreCase("addcommand"))
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
            else if(value.equalsIgnoreCase("head-player-name"))
            {
                r.setHeadName(input);
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
