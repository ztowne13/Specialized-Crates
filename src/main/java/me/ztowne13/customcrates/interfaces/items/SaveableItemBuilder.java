package me.ztowne13.customcrates.interfaces.items;

import com.cryptomorin.xseries.XMaterial;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.interfaces.items.attributes.CompressedEnchantment;
import me.ztowne13.customcrates.interfaces.items.attributes.RGBColor;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.Optional;

public class SaveableItemBuilder extends ItemBuilder implements SaveableItem {
    public SaveableItemBuilder(ItemBuilder builder) {
        super(builder);
    }

    public SaveableItemBuilder(ItemStack fromStack) {
        super(fromStack);
    }

    public SaveableItemBuilder(XMaterial m, int amnt) {
        super(m, amnt);
    }

    @Override
    public void saveItem(FileHandler fileHandler, String prefix, boolean allowUnnamedItems) {
        FileConfiguration fc = fileHandler.get();
        short durability = getStack().getDurability();
        try {
            XMaterial dynMat = XMaterial.matchXMaterial(getStack());
            fc.set(prefix + ".material", dynMat.name());
            fc.set(prefix + ".damage",
                    durability == 0 ||
                            dynMat.getData() != 0 ||
                            VersionUtils.Version.v1_13.isServerVersionOrLater()
                            ? null : durability);
        } catch (Exception exc) {
            if (VersionUtils.Version.v1_12.isServerVersionOrEarlier()) {
                getStack().setDurability((short) 0);
                fc.set(prefix + ".material", XMaterial.matchXMaterial(getStack()).name());
                fc.set(prefix + ".damage", durability);
            } else {
                exc.printStackTrace();
            }
        }

        fc.set(prefix + ".glow", isGlowing());
        fc.set(prefix + ".amount", getStack().getAmount());
        fc.set(prefix + ".player-head-name", getPlayerHeadName());
        if (VersionUtils.Version.v1_13.isServerVersionOrLater() && getItemMeta() instanceof Damageable) {
            fc.set(prefix + ".damage", ((Damageable) getItemMeta()).getDamage());
        }

        if (!hasDisplayName()) {
            fc.set(prefix + ".name", null);
        } else {
            fc.set(prefix + ".name", getDisplayNameFromChatColor(false));
        }

        // Enchantments
        if (!getEnchantments().isEmpty()) {
            ArrayList<String> enchants = new ArrayList<>();
            for (CompressedEnchantment enchant : getEnchantments())
                enchants.add(enchant.toString());

            fc.set(prefix + ".enchantments", enchants);
        } else
            fc.set(prefix + ".enchantments", null);

        // Potion Effects
        if (!getPotionEffects().isEmpty()) {
            ArrayList<String> parsedPots = new ArrayList<>();
            for (CompressedPotionEffect potion : getPotionEffects())
                parsedPots.add(potion.toString());

            fc.set(prefix + ".potion-effects", parsedPots);
        } else
            fc.set(prefix + ".potion-effects", null);

        // Lore
        if (!getLore().isEmpty())
            fc.set(prefix + ".lore", ChatUtils.fromColor(getLore()));
        else
            fc.set(prefix + ".lore", null);

        // NBT Tags
        if (!getNBTTags().isEmpty())
            fc.set(prefix + ".nbt-tags", getNBTTags());
        else
            fc.set(prefix + ".nbt-tags", null);

        // Item Flags
        if (!getItemFlags().isEmpty()) {
            ArrayList<String> flags = new ArrayList<>();
            for (ItemFlag flag : getItemFlags())
                flags.add(flag.name());

            fc.set(prefix + ".item-flags", flags);
        } else
            fc.set(prefix + ".item-flags", null);

        // Leather armour color
        if (isColorable() && getColor() != null) {
            fc.set(prefix + ".color.red", getColor().getR());
            fc.set(prefix + ".color.green", getColor().getG());
            fc.set(prefix + ".color.blue", getColor().getB());
        }

    }

    public boolean loadItem(FileHandler fileHandler, String prefix) {
        return loadItem(fileHandler, prefix, null, null, null, null, null, null, null);
    }

    /*
     * TODO: Add status logger event for 'damage'
     */
    @Override
    public boolean loadItem(FileHandler fileHandler, String prefix, StatusLogger statusLogger, StatusLoggerEvent itemFailure,
                            StatusLoggerEvent improperEnchant, StatusLoggerEvent improperPotion,
                            StatusLoggerEvent improperGlow, StatusLoggerEvent improperAmount,
                            StatusLoggerEvent invalidItemFlag) {
        FileConfiguration fc = fileHandler.get();

        convertOldConfigurations(fileHandler, prefix);

        short foundMaterialDurability;

        // material
        if (!fc.contains(prefix + ".material")) {
            if (itemFailure != null)
                itemFailure.log(statusLogger, new String[]{"The '" + prefix + ".material' value does not exist."});
            return false;
        } else {
            String mat = fc.getString(prefix + ".material");

            Optional<XMaterial> optional = XMaterial.matchXMaterial(mat.replace(";", ":"));

            if (optional.isPresent()) {
                XMaterial dynamicMaterial = optional.get();
                foundMaterialDurability = dynamicMaterial.getData();
                setStack(dynamicMaterial.parseItem());

                if (dynamicMaterial.equals(XMaterial.AIR)) {
                    return true;
                }
            } else {
                if (itemFailure != null)
                    itemFailure.log(statusLogger, new String[]{mat + " is not a valid material."});
                return false;
            }
        }


        if (fc.contains(prefix + ".damage") && foundMaterialDurability == 0) {
            if (Utils.isInt(fc.getString(prefix + ".damage"))) {
                int damage = Integer.parseInt(fc.getString(prefix + ".damage"));
                setDamage(damage);
            }
        }

        // Name
        if (!fc.contains(prefix + ".name")) {
            setDisplayName(null);
        }
        setDisplayName(fc.getString(prefix + ".name"));

        // Lore
        if (fc.contains(prefix + ".lore"))
            for (String line : fc.getStringList(prefix + ".lore"))
                addLore(line);

        // Enchantments
        if (fc.contains(prefix + ".enchantments")) {
            for (String unparsedEnchant : fc.getStringList(prefix + ".enchantments")) {
                try {
                    CompressedEnchantment compressedEnchantment = CompressedEnchantment.fromString(unparsedEnchant);
                    addEnchantment(compressedEnchantment);
                } catch (Exception exc) {
                    if (improperEnchant != null)
                        improperEnchant.log(statusLogger, new String[]{unparsedEnchant +
                                " is not formatted enchant;level or either the enchant is not a valid enchantment or the level is not a number."});
                }
            }
        }

        // Glow

        if (fc.contains(prefix + ".glow") && getEnchantments().isEmpty()) {
            String unparsedGlow = fc.getString(prefix + ".glow");
            if (Utils.isBoolean(unparsedGlow))
                setGlowing(Boolean.parseBoolean(unparsedGlow));
            else if (improperGlow != null)
                improperGlow
                        .log(statusLogger, new String[]{"The '" + prefix + ".glow' value is not a proper true/false value"});
        }

        // Potion Effects
        if (fc.contains(prefix + ".potion-effects")) {
            for (String unparsedPotion : fc.getStringList(prefix + ".potion-effects")) {
                try {
                    CompressedPotionEffect compressedPotionEffect = CompressedPotionEffect.fromString(unparsedPotion);
                    addPotionEffect(compressedPotionEffect);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    if (improperPotion != null)
                        improperPotion.log(statusLogger, new String[]{unparsedPotion +
                                " is not formatted potiontype;duration;amplifier or either the potion is not a valid potion type or the duration/amplifier is not a number."});
                }
            }
        }

        // NBT Tags
        if (fc.contains(prefix + ".nbt-tags"))
            for (String line : fc.getStringList(prefix + ".nbt-tags"))
                addNBTTag(line);

        // Item Flags
        if (fc.contains(prefix + ".item-flags")) {
            for (String line : fc.getStringList(prefix + ".item-flags")) {
                try {
                    ItemFlag flag = ItemFlag.valueOf(line);
                    addItemFlag(flag);
                } catch (Exception exc) {
                    if (invalidItemFlag != null)
                        invalidItemFlag.log(statusLogger, new String[]{line + " is an invalid flag."});
                }
            }
        }

        // Amount

        if (fc.contains(prefix + ".amount")) {
            String unparsedAmount = fc.getString(prefix + ".amount");
            if (Utils.isInt(unparsedAmount))
                getStack().setAmount(Integer.parseInt(unparsedAmount));
            else if (improperAmount != null)
                improperAmount.log(statusLogger, new String[]{"The '" + prefix + ".amount' value is not a valid number."});
        }

        // Player Head Name

        if (fc.contains(prefix + ".player-head-name")) {
            // If the head has the SkullOwner tag, it LIKELY doesn't need a player-head name. So, don't save the player-head
            // name, otherwise it will overwrite the tag.
            if (!hasNBTTag("SkullOwner")) {
                setPlayerHeadName(fc.getString(prefix + ".player-head-name"));
            }
        }

        // Leather armour color

        if (fc.contains(prefix + ".color") && isColorable()) {
            int red = 0;
            int green = 0;
            int blue = 0;

            String unparsedRed = fc.getString(prefix + ".color.red");
            String unparsedGreen = fc.getString(prefix + ".color.green");
            String unparsedBlue = fc.getString(prefix + ".color.blue");

            if (Utils.isInt(unparsedRed)) {
                red = Integer.parseInt(unparsedRed);
            }
            if (Utils.isInt(unparsedGreen)) {
                green = Integer.parseInt(unparsedGreen);
            }
            if (Utils.isInt(unparsedBlue)) {
                blue = Integer.parseInt(unparsedBlue);
            }

            setColor(new RGBColor(red, green, blue));
        }

        return true;
    }

    public void convertOldConfigurations(FileHandler fileHandler, String prefix) {
        convertOldEnchantType(fileHandler, prefix);
        convertOldItemType(fileHandler, prefix);
    }

    public void convertOldItemType(FileHandler fileHandler, String prefix) {
        FileConfiguration fc = fileHandler.get();

        if (fc.contains(prefix)) {
            if (!fc.isConfigurationSection(prefix)) {
                ChatUtils.log("Converting old item format to new item format...");

                String original = fc.getString(prefix);
                String[] args = original.split(";");

                String materialName = args[0].toUpperCase();
                String byteName = args.length > 1 ? args[1] : "0";

                Optional<XMaterial> optional = XMaterial.matchXMaterial(materialName + ":" + byteName);

                if (optional.isPresent()) {
                    XMaterial m = optional.get();

                    SaveableItemBuilder newItem = new SaveableItemBuilder(m, 1);
                    newItem.saveItem(fileHandler, prefix, true);
                    ChatUtils.log("Successfully converted " + original);
                    fileHandler.save();
                } else {
                    ChatUtils.log("Failed to convert " + fc.getString(prefix) + ", it is likely not a proper material.");
                }
            }
        }

    }

    public void convertOldEnchantType(FileHandler fileHandler, String prefix) {
        FileConfiguration fc = fileHandler.get();
        if (fc.contains(prefix + ".enchantment") && !fc.contains(prefix + ".enchantments")) {
            ChatUtils.log("Converting old enchantment format to new enchantment format...");
            try {
                boolean isSet = !fc.getStringList(prefix + ".enchantment").isEmpty();
                ArrayList<String> newEnchs = new ArrayList<>();
                if (isSet) {
                    for (String line : fc.getStringList(prefix + ".enchantment")) {
                        try {
                            CompressedEnchantment compressedEnchantment = CompressedEnchantment.fromString(line);
                            newEnchs.add(compressedEnchantment.toString());
                        } catch (Exception exc) {

                        }
                    }
                } else {
                    String enchant = fc.getString(prefix + ".enchantment");
                    CompressedEnchantment compressedEnchantment = CompressedEnchantment.fromString(enchant);
                    newEnchs.add(compressedEnchantment.toString());
                }

                fc.set(prefix + ".enchantment", null);
                fc.set(prefix + ".enchantments", newEnchs);
                ChatUtils.log("Success. Saving...");
                fileHandler.save();
                ChatUtils.log("Saved.");
            } catch (Exception exc) {
                ChatUtils.log("Failed.");
            }
        }
    }
}
