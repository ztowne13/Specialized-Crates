package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.interfaces.items.attributes.BukkitGlowEffect;
import me.ztowne13.customcrates.interfaces.items.attributes.CompressedEnchantment;
import me.ztowne13.customcrates.interfaces.items.attributes.RGBColor;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemBuilder implements EditableItem
{
    ItemStack stack;

    boolean glowing = false;
    RGBColor rgbColor;
    List<CompressedEnchantment> enchantments = null;
    List<CompressedPotionEffect> potionEffects = null;
    List<String> nbtTags = null;
    List<String> lore;
    List<ItemFlag> flags = null;

    public ItemBuilder(EditableItem builder)
    {
        glowing = builder.isGlowing();
        stack = new ItemStack(builder.getStack());
        updateFromItem();
    }

    public ItemBuilder(ItemStack fromStack)
    {
        stack = fromStack.clone();
        updateFromItem();
    }

    @Deprecated
    public ItemBuilder(Material m, int amnt, int byt)
    {
        create(DynamicMaterial.fromString(m.name() + ";" + byt), amnt);
    }

    public ItemBuilder(DynamicMaterial material)
    {
        create(material, 1);
    }

    public ItemBuilder(DynamicMaterial m, int amnt)
    {
        create(m, amnt);
    }

    public void reset()
    {
        getEnchantments().clear();
        getNBTTags().clear();
        getLore().clear();
        getPotionEffects().clear();
        getItemFlags().clear();
        rgbColor = null;
    }

    public void updateFromItem()
    {
        reset();

        List<String> cachedTags = NBTTagBuilder.getFrom(stack, false);

        // Enchantments
        Map<Enchantment, Integer> enchants;
        if (im() instanceof EnchantmentStorageMeta)
        {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) im();
            enchants = meta.getStoredEnchants();
        }
        else
        {
            enchants = stack.getEnchantments();
        }
        for (Enchantment enchantment : enchants.keySet())
        {
            addEnchantment(enchantment, enchants.get(enchantment));
        }

        // Potion Effects
        if (stack.hasItemMeta() && im() instanceof PotionMeta)
        {
            PotionMeta pm = (PotionMeta) im();

            if (VersionUtils.Version.v1_9.isServerVersionOrLater())
            {
                PotionData baseData = pm.getBasePotionData();
                if(baseData.getType().getEffectType() != null)
                {
                    addPotionEffect(
                            new CompressedPotionEffect(baseData.getType().getEffectType(), baseData.isExtended() ? 1 : 0,
                                    baseData.isUpgraded() ? 1 : 0));
                }

                for (PotionEffect pe : pm.getCustomEffects())
                {
                    addPotionEffect(new CompressedPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier()));
                }
            }
            else
            {
                Potion pot = Potion.fromItemStack(stack);

                try
                {
                    Potion toClearPot = Potion.fromItemStack(stack);
                    toClearPot.getEffects().clear();
                    toClearPot.apply(stack);
                }
                catch(Exception exc)
                {

                }

                if(pot != null && pot.getType() != null)
                {
                    addPotionEffect(new CompressedPotionEffect(pot.getType().getEffectType(), 1, 1));

                    ArrayList<CompressedPotionEffect> toAdd = new ArrayList<>();

                    for (PotionEffect pe : pot.getEffects())
                    {
                        toAdd.add(new CompressedPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier()));
                    }

                    for(CompressedPotionEffect effect : toAdd)
                    {
                        addPotionEffect(effect);
                    }
                }
            }
        }

        // NBT Tags
        for (String tags : cachedTags)
        {
            addNBTTag(tags);
        }

        // Lore
        if (stack.hasItemMeta() && stack.getItemMeta().hasLore())
        {
            for (String line : stack.getItemMeta().getLore())
                addLore(line);
        }

        // Item Flags
        if (stack.hasItemMeta() && stack.getItemMeta().getItemFlags() != null)
        {
            for (ItemFlag flag : stack.getItemMeta().getItemFlags())
                addItemFlag(flag);
        }

        // Coloured Armor
        if (im() instanceof LeatherArmorMeta)
        {
            LeatherArmorMeta meta = (LeatherArmorMeta) im();
            RGBColor newColor =
                    new RGBColor(meta.getColor().getRed(), meta.getColor().getGreen(), meta.getColor().getBlue());
            setColor(newColor);
        }
    }

    public void create(DynamicMaterial m, int amnt)
    {
        stack = m.parseItem();
        stack.setAmount(amnt);
        if (m.preProgrammedNBTTag && VersionUtils.Version.v1_12.isServerVersionOrEarlier())
            addNBTTag("EntityTag " + m.nbtTag);
    }

    public ItemMeta im()
    {
        if(getStack().getItemMeta() == null) {
            return Bukkit.getItemFactory().getItemMeta(getStack().getType());
        }
        return getStack().getItemMeta();
    }

    public void setIm(ItemMeta im)
    {
        getStack().setItemMeta(im);
    }

    @Deprecated
    public ItemBuilder setName(String name)
    {
        setDisplayName(name);
        return this;
    }

    @Override
    public void setDisplayName(String name)
    {
        ItemMeta im = im();
        if (name == null)
        {
            im.setDisplayName(null);
        }
        else
        {
            im.setDisplayName(ChatUtils.toChatColor(name));
        }
        setIm(im);
    }

    public void removeDisplayName()
    {
        ItemMeta im = im();
        im.setDisplayName(null);
        setIm(im);
    }

    public String getDisplayNameFromChatColor(boolean useMaterialWhenNull)
    {
        return ChatUtils.fromChatColor(getDisplayName(useMaterialWhenNull));
    }

    @Override
    public String getDisplayName(boolean useMaterialWhenNull)
    {
        if (useMaterialWhenNull)
        {
            return hasDisplayName() ? im().getDisplayName() :
                    WordUtils.capitalizeFully(get().getType().name().replaceAll("_", " "));
        }

        return im().getDisplayName();
    }

    public boolean hasDisplayName()
    {
        return im() != null &&
                getDisplayName(false) != null &&
                !getDisplayName(false).equalsIgnoreCase("");
    }

    public String getName(boolean strippedOfColor)
    {
        return strippedOfColor ? ChatUtils.removeColor(im().getDisplayName()) : im().getDisplayName();
    }

    public ItemBuilder addLore(String s)
    {
        getLore().add(s);
        reapplyLore();
        return this;
    }

    public ItemBuilder addAutomaticLore(String lineColor, int charLength, String lore)
    {
        return addAutomaticLore(lineColor, charLength, false, lore);
    }

    public ItemBuilder addAutomaticLore(String lineColor, int charLength, boolean maintainColor, String lore)
    {
        String[] split = lore.split(" ");
        int lineSize = 0;
        String currentLine = "";
        String lastLine = "";

        for (String word : split)
        {
            int wordLength = word.length();
            if (lineSize + wordLength <= charLength)
            {
                lineSize += wordLength + 1;
                currentLine += word + " ";
            }
            else
            {
                if(maintainColor)
                {
                    lineColor = ChatUtils.lastChatColor(lastLine);
                }
                String line = lineColor + currentLine.substring(0, currentLine.length() - 1);
                addLore(line);
                currentLine = word + " ";
                lineSize = wordLength;
                lastLine = line;


            }
        }
        if(maintainColor)
        {
            lineColor = ChatUtils.lastChatColor(lastLine);
        }
        if (lineSize != 0)
            addLore(lineColor + currentLine.substring(0, currentLine.length() - 1));

        return this;
    }

    public ItemBuilder clearLore()
    {
        getLore().clear();
        reapplyLore();
        return this;
    }

    @Deprecated
    public ItemBuilder setLore(String s)
    {
        clearLore();
        addLore(s);

        return this;
    }

    @Override
    public List<String> getLore()
    {
        if (lore == null)
            lore = new ArrayList<>();
        return lore;
    }

    @Override
    public void reapplyLore()
    {
        ItemMeta im = im();

        ArrayList<String> colorizedLore = new ArrayList<>();
        for (String line : getLore())
            colorizedLore.add(ChatUtils.toChatColor(line));

        im.setLore(colorizedLore);
        setIm(im);
    }

    public void addEnchantment(Enchantment enchantment, int level)
    {
        addEnchantment(new CompressedEnchantment(enchantment, level));
    }

    public void addEnchantment(CompressedEnchantment compressedEnchantment)
    {
        getEnchantments().add(compressedEnchantment);
        reapplyEnchantments();
    }

    public void addPotionEffect(CompressedPotionEffect compressedPotionEffect)
    {
        getPotionEffects().add(compressedPotionEffect);
        reapplyPotionEffects();
    }

    public void addNBTTag(String tag)
    {
        getNBTTags().add(tag);
        reapplyNBTTags();
    }

    public boolean hasNBTTag(String tag)
    {
        for (String tagParsed : getNBTTags())
        {
            if (tagParsed.split(" ")[0].equalsIgnoreCase(tag))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void addItemFlag(ItemFlag flag)
    {
        getItemFlags().add(flag);
        reapplyItemFlags();
    }

    @Override
    public void removeItemFlag(ItemFlag flag)
    {
        getItemFlags().remove(flag);
        reapplyItemFlags();
    }


    @Override
    public void setPlayerHeadName(String name)
    {
        if (DynamicMaterial.fromItemStack(getStack()).equals(DynamicMaterial.PLAYER_HEAD))
        {
            if (Character.isLetterOrDigit(name.charAt(0)))
            {
                SkullMeta skullMeta = (SkullMeta) im();
                try
                {
                    UUID uuid = UUID.fromString(name);
                    OfflinePlayer op = Bukkit.getOfflinePlayer(uuid);
                    skullMeta.setOwningPlayer(op);
                }
                catch (Exception exc)
                {
                    skullMeta.setOwner(name);

                }
                setIm(skullMeta);
            }
        }
    }

    @Override
    public String getPlayerHeadName()
    {
        if (DynamicMaterial.fromItemStack(getStack()).equals(DynamicMaterial.PLAYER_HEAD))
        {
            SkullMeta skullMeta = (SkullMeta) im();
            return skullMeta.getOwner();
        }

        return null;
    }

    @Override
    public boolean isGlowing()
    {
        return glowing;
    }

    @Override
    public void setGlowing(boolean glow)
    {
        BukkitGlowEffect ge = new BukkitGlowEffect(stack);
        if (glow)
            stack = ge.apply();
        else
            stack = ge.remove();
        glowing = glow;
    }

    @Override
    public List<String> getNBTTags()
    {
        if (nbtTags == null)
            nbtTags = new ArrayList<>();

        return nbtTags;
    }

    @Override
    public void reapplyNBTTags()
    {
        for (String tag : getNBTTags())
        {
            stack = NBTTagBuilder.applyTo(stack, tag);
        }
    }

    @Override
    public List<ItemFlag> getItemFlags()
    {
        if (flags == null)
            flags = new ArrayList<>();

        return flags;
    }

    @Override
    public void reapplyItemFlags()
    {
        ItemStack stack = get();
        ItemMeta im = stack.getItemMeta();

        im.getItemFlags().clear();

        for (ItemFlag flag : getItemFlags())
            im.addItemFlags(flag);

        stack.setItemMeta(im);
        setStack(stack);
    }

    @Override
    public List<CompressedEnchantment> getEnchantments()
    {
        if (enchantments == null)
            enchantments = new ArrayList<>();

        return enchantments;
    }

    @Override
    public void reapplyEnchantments()
    {
        for (Enchantment enchantment : stack.getEnchantments().keySet())
            im().removeEnchant(enchantment);

        for (CompressedEnchantment compressedEnchantment : getEnchantments())
        {
            compressedEnchantment.applyTo(this);
        }
    }

    @Override
    public List<CompressedPotionEffect> getPotionEffects()
    {
        if (potionEffects == null)
            potionEffects = new ArrayList<>();

        return potionEffects;
    }

    @Override
    public void reapplyPotionEffects()
    {
        boolean first = true;

        if (stack.getItemMeta() instanceof PotionMeta)
        {
            PotionMeta pm = (PotionMeta) im();
            pm.clearCustomEffects();

            if (!getPotionEffects().isEmpty())
            {

                CompressedPotionEffect firstVal = getPotionEffects().get(0);
                if (VersionUtils.Version.v1_9.isServerVersionOrLater())
                {
                    boolean isOld = (firstVal.getAmplifier() == 1 && firstVal.getDuration() == 1);

                    PotionData potionData =
                            new PotionData(PotionType.getByEffect(firstVal.getType()), firstVal.getDuration() == 1 && !isOld,
                                    firstVal.getAmplifier() == 1 && !isOld);
                    pm.setBasePotionData(potionData);
                }
                else
                {
                    Potion pot = new Potion(PotionType.getByEffect(firstVal.getType()), firstVal.getAmplifier() == 0 ? 1 : 2,
                            stack.getType().equals(DynamicMaterial.SPLASH_POTION.parseMaterial()));
                    pot.apply(stack);
                    first = false;
                }
            }

            stack.setItemMeta(pm);

            for (CompressedPotionEffect compressedPotionEffect : getPotionEffects())
            {
                if (!first)
                    stack = compressedPotionEffect.applyTo(stack);
                first = false;
            }
        }
    }

    @Override
    public void setColor(RGBColor rgbColor)
    {
        this.rgbColor = rgbColor;
        reapplyColor();
    }

    @Override
    public void reapplyColor()
    {
        if (isColorable() && getColor() != null)
        {
            LeatherArmorMeta meta = (LeatherArmorMeta) im();
            meta.setColor(Color.fromRGB(getColor().getR(), getColor().getG(), getColor().getB()));
            setIm(meta);
        }
    }

    @Override
    public RGBColor getColor()
    {
        if (isColorable())
        {
            if (rgbColor == null)
            {
                RGBColor rgbColor = new RGBColor(160, 101, 64);
                setColor(rgbColor);
            }
            return rgbColor;
        }
        return null;
    }

    @Override
    public boolean isColorable()
    {
        return im() instanceof LeatherArmorMeta;
    }

    @Override
    public boolean equals(Object obj)
    {
        ItemBuilder compare = (ItemBuilder) obj;

        boolean loreEquals = true;

        if (compare == null)
            return false;

        if (getLore().size() == compare.getLore().size())
        {
            for (int i = 0; i < getLore().size(); i++)
            {
                if (!getLore().get(i).equals(compare.getLore().get(i)))
                    loreEquals = false;
            }
        }
        else
        {
            loreEquals = false;
        }

        boolean equal =
                !(!compare.hasDisplayName() && hasDisplayName())
                        && !(compare.hasDisplayName() && !hasDisplayName())
                        && ((!compare.hasDisplayName() && !hasDisplayName()) ||
                        (compare.getDisplayName(false).equals(getDisplayName(false))))
                        && loreEquals
                        && compare.getStack().getType().equals(getStack().getType())
                        && compare.getStack().getAmount() == getStack().getAmount();

        return equal;
    }

    @Override
    public void setDamage(int damage)
    {
        if(damage == 0 && getDamage() == 0)
        {
            return;
        }

        if(VersionUtils.Version.v1_13.isServerVersionOrLater() && im() instanceof Damageable)
        {
            Damageable meta = (Damageable)im();
            meta.setDamage(damage);
            setIm((ItemMeta)meta);
        }
        else if(VersionUtils.Version.v1_12.isServerVersionOrEarlier())
        {
            getStack().setDurability((short)damage);
        }
    }

    @Override
    public int getDamage()
    {
        if(VersionUtils.Version.v1_13.isServerVersionOrLater() && im() instanceof Damageable)
        {
            Damageable meta = (Damageable)im();
            return meta.getDamage();
        }
        else if(VersionUtils.Version.v1_12.isServerVersionOrEarlier())
        {
            return get().getDurability();
        }
        return 0;
    }

    public ItemStack get()
    {
        return getStack();
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public void setStack(ItemStack stack)
    {
        this.stack = stack;
    }
}
