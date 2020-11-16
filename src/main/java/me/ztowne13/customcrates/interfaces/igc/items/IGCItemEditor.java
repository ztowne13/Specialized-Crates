package me.ztowne13.customcrates.interfaces.igc.items;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.InventoryBuilder;
import me.ztowne13.customcrates.interfaces.igc.IGCDefaultItems;
import me.ztowne13.customcrates.interfaces.igc.IGCListEditor;
import me.ztowne13.customcrates.interfaces.igc.IGCListSelector;
import me.ztowne13.customcrates.interfaces.igc.IGCMenu;
import me.ztowne13.customcrates.interfaces.igc.inputmenus.InputMenu;
import me.ztowne13.customcrates.interfaces.items.CompressedPotionEffect;
import me.ztowne13.customcrates.interfaces.items.EditableItem;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.items.attributes.CompressedEnchantment;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class IGCItemEditor extends IGCMenu {

    EditableItem editableItem;

    public IGCItemEditor(SpecializedCrates cc, Player p, IGCMenu lastMenu, EditableItem editableItem) {
        super(cc, p, lastMenu, "&7&l> &6&lItem Editor");

        this.editableItem = editableItem;
    }

    @Override
    public void openMenu() {
        editableItem.reapplyLore();
        editableItem.reapplyEnchantments();
        editableItem.reapplyPotionEffects();
        editableItem.reapplyNBTTags();
        editableItem.reapplyItemFlags();

        InventoryBuilder ib = createDefault(45);

        ib.setItem(0, IGCDefaultItems.EXIT_BUTTON.getIb());

        // Lore
        ItemBuilder lore = new ItemBuilder(XMaterial.WRITABLE_BOOK, 1);
        lore.setDisplayName("&aEdit the lore");
        lore.addLore("&7Current value:");
        for (String loreLine : editableItem.getLore())
            lore.addLore("&7- " + loreLine);
        lore.addLore("").addAutomaticLore("&f", 30,
                "Edit the lore of the display item. Remove all lines and leave the lore blank to use the default lore in the config.yml");

        // Display name
        ItemBuilder displayName = new ItemBuilder(XMaterial.PAPER, 1);
        displayName.setDisplayName("&aEdit the display name");
        displayName.addLore("&7Current value:").addLore("&7" + editableItem.getDisplayName(false));
        displayName.addLore("").addAutomaticLore("&f", 30, "Set the display name of the display item.");

        // Enchantments
        ItemBuilder enchantments = new ItemBuilder(XMaterial.ENCHANTING_TABLE, 1);
        enchantments.setDisplayName("&aEdit the enchantments");
        enchantments.addLore("&7Current value:");
        for (CompressedEnchantment enchantment : editableItem.getEnchantments())
            enchantments.addLore("&7- " + enchantment.toString());
        enchantments.addLore("")
                .addAutomaticLore("&f", 30, "Edit the enchantments of the display item. FORMATTED: enchantment;level");

        // NBT Tags
        ItemBuilder nbtTags = new ItemBuilder(XMaterial.NAME_TAG, 1);
        nbtTags.setDisplayName("&aEdit the nbt-tags");
        nbtTags.addLore("&7Current value:");
        for (String tag : editableItem.getNBTTags())
            nbtTags.addLore("&7- " + tag);
        nbtTags.addLore("").addAutomaticLore("&f", 30,
                "Edit the NBT Tags of an item.");

        // Glow
        ItemBuilder glow =
                new ItemBuilder(editableItem.isGlowing() ? XMaterial.NETHER_STAR : XMaterial.QUARTZ, 1);
        glow.setDisplayName("&aEdit if the item is glowing");
        glow.addLore("&7Current value:").addLore("&7" + editableItem.isGlowing());
        glow.addLore("").addAutomaticLore("&f", 30,
                "This will toggle whether or not the display item will have a glowing effect. For enchanted items, if this value is true, the enchantments will be hidden.");
        glow.addLore("").addAutomaticLore("&e&l", 30, "Same as HIDE ENCHANTS");

        XMaterial editableItemDM = XMaterial.matchXMaterial(editableItem.getStack());

        // Player head
        ItemBuilder playerHead = new ItemBuilder(XMaterial.PLAYER_HEAD, 1);
        if (editableItemDM.equals(XMaterial.PLAYER_HEAD)) {
            playerHead.setDisplayName("&aEdit the player-head's name.");
            playerHead.addLore("&7Current value:").addLore("&7" + editableItem.getPlayerHeadName());
            playerHead.addLore("")
                    .addAutomaticLore("&f", 30, "Edit the name of the player head to apply a skin to the skull.");
        } else {
            playerHead.setDisplayName("&cEdit the player-head's name");
            playerHead.addLore("").addLore("&cCurrent item is not a skull.");
        }

        // Potion
        ItemBuilder potion = new ItemBuilder(XMaterial.POTION, 1);
        if (editableItemDM.name().contains("POTION")) {
            potion.setDisplayName("&aEdit the potion effects");
            potion.addLore("&7Current value:");
            for (CompressedPotionEffect potionEffect : editableItem.getPotionEffects())
                potion.addLore("&7- " + potionEffect.toString());
            potion.addLore("").addAutomaticLore("&f", 30,
                    "Edit the potion effects of the display item. FORMATTED: potioneffect;duration;amplifier");
        } else {
            potion.setDisplayName("&cEdit the potion effects");
            potion.addLore("").addLore("&cCurrent item is not a potion.");
        }

        // Amount
        ItemBuilder amount = new ItemBuilder(XMaterial.STONE_BUTTON, 1);
        amount.setDisplayName("&aEdit the amount.");
        amount.addLore("&7Current value:");
        amount.addLore("&7" + editableItem.getStack().getAmount());
        amount.addLore("").addAutomaticLore("&f", 30, "Edit the amount of in the stack of items.");

        // Edit item
        ItemBuilder item = new ItemBuilder(editableItem);
        item.clearLore();
        item.setDisplayName("&aEdit the item material");
        item.addLore("&7Current value:").addLore("&7" + editableItemDM.name());
        item.addLore("").addAutomaticLore("&f", 30,
                "Edit ONLY the material of this item, none of the other values will be changed.");

        // Edit everything
        ItemBuilder everything = new ItemBuilder(editableItem);
        everything.clearLore();
        everything.setDisplayName("&aEdit EVERYTHING");
        everything.addLore("").addAutomaticLore("&f", 30, "Edit EVERYTHING AT ONCE. This will update all of these " +
                "values to the item you are HOLDING IN HAND. The only value it will not effect is the 'glowing' effect, everything " +
                "else will be updated: name, lore, enchants, etc. Therefore, everything will be overwritten!");

        // Edit attributes
        ItemBuilder attributes = new ItemBuilder(XMaterial.REDSTONE, 1);
        attributes.setDisplayName("&aEdit the Item Flags");
        attributes.addLore("").addAutomaticLore("&f", 30, "Edit the attributes of the item such as HIDE_ENCHANTS, " +
                "HIDE_ATTRIBUTES, etc.");
        attributes.addLore("").addAutomaticLore("&c", 30, "Note: If glow is set to true, HIDE_ENCHANTS will ALWAYS" +
                " be true unless glow: false.");

        // Edit color
        ItemBuilder color = new ItemBuilder(XMaterial.LIGHT_BLUE_DYE, 1);
        if (item.isColorable()) {
            color.setDisplayName("&aEdit the armour Color");
            color.addLore("&7Current Value:")
                    .addLore("&7" + item.getColor().getRed() + ", " + item.getColor().getGreen() + ", " + item.getColor().getBlue());
            color.addLore("").addAutomaticLore("&f", 30, "Edit the red, green, and blue values of the colored armour.");
        } else {
            color.setDisplayName("&cEdit the armour Color");
            color.addLore("").addLore("&cThis item is not colorable.");
        }

        // Damage
        ItemBuilder damage = new ItemBuilder(XMaterial.GOLDEN_AXE);
        damage.setDamage(20);
        damage.setDisplayName("&aEdit the item damage");
        damage.addLore("&7Current Value: ");
        damage.addLore("&7" + item.getDamage());
        damage.addLore("");
        damage.addAutomaticLore("&f", 30,
                "This is to set the damage of an item, NOT TO SET THE DATA VALUE. " +
                        "If you want a 'damaged' item like red wool or acacia log (for example)," +
                        " use RED_WOOL or ACACIA_LOG - EVEN ON 1.8-1.12. This value is ONLY to damage things like swords.");

        ib.setItem(10, everything);
        ib.setItem(11, item);
        ib.setItem(13, displayName);
        ib.setItem(14, lore);
        ib.setItem(15, enchantments);
        ib.setItem(16, glow);
        ib.setItem(22, nbtTags);
        ib.setItem(23, amount);
        ib.setItem(24, potion);
        ib.setItem(25, attributes);
        ib.setItem(31, playerHead);
        ib.setItem(32, color);
        ib.setItem(33, damage);

        ib.open();
        putInMenu();
    }

    @Override
    public void handleClick(int slot) {
        switch (slot) {
            case 0:
                up();
                break;
            case 10:
                ItemStack stack;
                if (VersionUtils.Version.v1_9.isServerVersionOrLater())
                    stack = getP().getInventory().getItemInMainHand();
                else
                    stack = getP().getItemInHand();

                if (!stack.getType().equals(Material.AIR)) {
                    editableItem.setStack(stack.clone());
                    editableItem.updateFromItem();
                    open();
                } else {
                    ChatUtils.msgError(getP(), "You are not holding anything in your hand!");
                }
                break;
            case 11:
                new InputMenu(getCc(), getP(), "item material",
                        XMaterial.matchXMaterial(editableItem.getStack()).toString(), Material.class, this);
                break;
            case 13:
                new InputMenu(getCc(), getP(), "displayname", editableItem.getDisplayName(false), String.class, this);
                break;
            case 14:
                new IGCListEditor(getCc(), getP(), this, "Lore", "Line", editableItem.getLore(), XMaterial.PAPER, 1)
                        .open();
                break;
            case 15:
                new IGCListEditor(getCc(), getP(), this, "Enchantments", "Enchantment", editableItem.getEnchantments(),
                        XMaterial.ENCHANTED_BOOK, 1, CompressedEnchantment.class, "fromString",
                        "That is not formatted ENCHANTMENT;LEVEL or either the enchantment name is wrong or the level is not a number.")
                        .open();
                break;
            case 16:
                editableItem.setGlowing(!editableItem.isGlowing());
                open();
                break;
            case 22:
                new IGCListEditor(getCc(), getP(), this, "NBT Tags", "Tag", editableItem.getNBTTags(),
                        XMaterial.NAME_TAG, 1).open();
                break;
            case 23:
                new InputMenu(getCc(), getP(), "amount", editableItem.getStack().getAmount() + "", Integer.class, this);
                break;
            case 24:
                if (XMaterial.matchXMaterial(editableItem.getStack()).name().contains("POTION"))
                    new IGCListEditor(getCc(), getP(), this, "Potion Effects", "Potion", editableItem.getPotionEffects(),
                            XMaterial.GLASS_BOTTLE, 1, CompressedPotionEffect.class, "fromString",
                            "That is not formatted POTIONTYPE;DURATION;AMPLIFIER or either the potion effect name " +
                                    "is incorrect, or either the duration or amplifier aren't numbers.").open();
                else
                    ChatUtils.msgError(getP(), "The current item is not a potion.");
                break;
            case 25:
                List<String> descriptors = new ArrayList<>();
                List<ItemBuilder> builders = new ArrayList<>();
                for (ItemFlag flag : ItemFlag.values()) {
                    descriptors.add("");
                    if (editableItem.getItemFlags().contains(flag))
                        builders.add(new ItemBuilder(XMaterial.LIME_WOOL, 1));
                    else
                        builders.add(new ItemBuilder(XMaterial.RED_WOOL, 1));
                }
                new IGCListSelector(getCc(), getP(), this, "Item Flags", Arrays.asList(ItemFlag.values()),
                        XMaterial.PAPER, 1,
                        descriptors, builders).open();
                break;
            case 31:
                if (XMaterial.PLAYER_HEAD.isSimilar(editableItem.getStack()))
                    new InputMenu(getCc(), getP(), "player-head-name", editableItem.getPlayerHeadName(), String.class,
                            this, true);
                else
                    ChatUtils.msgError(getP(), "The current item is not a player head or skull.");
                break;
            case 32:
                if (editableItem.isColorable()) {
                    new IGCColoredArmour(getCc(), getP(), this, editableItem).open();
                } else {
                    ChatUtils.msgError(getP(), "The current item is not a colorable piece of armor");
                }
                break;
            case 33:
                new InputMenu(getCc(), getP(), "damage", editableItem.getDamage() + "", Integer.class, this);
                break;
        }
    }

    @Override
    public boolean handleInput(String value, String input) {
        if (value.equalsIgnoreCase("item material")) {
            Optional<XMaterial> optional = XMaterial.matchXMaterial(input);
            if (optional.isPresent()) {
                XMaterial dynamicMaterial = optional.get();
                dynamicMaterial.setType(editableItem.getStack());
                ChatUtils.msgSuccess(getP(), "Set the " + value + " to " + input);
                return true;
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid material name.");
            }
        } else if (value.equalsIgnoreCase("displayname")) {
            editableItem.setDisplayName(input);
            ChatUtils.msgSuccess(getP(), "Set the " + value + " to " + input);
            return true;
        } else if (value.equalsIgnoreCase("player-head-name")) {
            editableItem.setPlayerHeadName(input);
            ChatUtils.msgSuccess(getP(), "Set the " + value + " to " + input);
            return true;
        } else if (value.equalsIgnoreCase("amount")) {
            if (Utils.isInt(input)) {
                editableItem.getStack().setAmount(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set the amount to " + input);
                return true;
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid number.");
            }
        } else if (value.equalsIgnoreCase("damage")) {
            if (Utils.isInt(input)) {
                editableItem.setDamage(Integer.parseInt(input));
                ChatUtils.msgSuccess(getP(), "Set the damage to " + input);
                return true;
            } else {
                ChatUtils.msgError(getP(), input + " is not a valid number.");
            }
        } else if (value.equalsIgnoreCase("Item Flags")) {
            ItemFlag flag = ItemFlag.valueOf(input);
            if (editableItem.getItemFlags().contains(flag)) {
                editableItem.removeItemFlag(flag);
                ChatUtils.msgSuccess(getP(), "Removed flag: " + flag.name());
            } else {
                editableItem.addItemFlag(flag);
                ChatUtils.msgSuccess(getP(), "Added flag: " + flag.name());
            }
        }
        return false;
    }

}
