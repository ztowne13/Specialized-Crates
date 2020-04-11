package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.interfaces.items.attributes.BukkitGlowEffect;
import me.ztowne13.customcrates.interfaces.nbt.NBTTagManager;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemBuilder implements EditableItem
{
    public static String NO_NAME = "!?!noname!?!";

    ItemStack stack;

    boolean glowing = false;
    List<CompressedEnchantment> enchantments = null;
    List<CompressedPotionEffect> potionEffects = null;
    List<String> nbtTags = null;
    List<String> lore;
    List<ItemFlag> flags = null;

    public ItemBuilder(EditableItem builder)
    {
        this(builder.getStack());

        setGlowing(builder.isGlowing());
    }

    public ItemBuilder(ItemStack fromStack)
    {
        stack = fromStack.clone();

        for (Enchantment enchantment : stack.getEnchantments().keySet())
        {
            addEnchantment(enchantment, stack.getEnchantmentLevel(enchantment));
        }

        if (stack.hasItemMeta() && im() instanceof PotionMeta)
        {
            PotionMeta pm = (PotionMeta) im();
            for (PotionEffect pe : pm.getCustomEffects())
            {
                addPotionEffect(new CompressedPotionEffect(pe.getType(), pe.getDuration(), pe.getAmplifier()));
            }
        }

        for (String tags : NBTTagManager.getFrom(stack))
        {
            addNBTTag(tags);
        }

        if (stack.hasItemMeta() && stack.getItemMeta().hasLore())
        {
            for (String line : stack.getItemMeta().getLore())
                addLore(line);
        }

        if (stack.hasItemMeta() && stack.getItemMeta().getItemFlags() != null)
        {
            for (ItemFlag flag : stack.getItemMeta().getItemFlags())
                addItemFlag(flag);
        }

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

    public void create(DynamicMaterial m, int amnt)
    {
        stack = m.parseItem();
        stack.setAmount(amnt);
        if (m.preProgrammedNBTTag && VersionUtils.Version.v1_12.isServerVersionOrEarlier())
            addNBTTag("EntityTag " + m.nbtTag);
    }

    public ItemMeta im()
    {
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
        return getDisplayName(false) != null;
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
        String[] split = lore.split(" ");
        int lineSize = 0;
        String currentLine = "";

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
                addLore(lineColor + currentLine.substring(0, currentLine.length() - 1));
                currentLine = word + " ";
                lineSize = wordLength;


            }
        }

        if (lineSize != 0)
            addLore(lineColor + currentLine.substring(0, currentLine.length() - 1));

        return this;
    }

    public ItemBuilder addAutomaticLore(int charLength, String lore)
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
                String line = ChatUtils.lastChatColor(lastLine) + currentLine.substring(0, currentLine.length() - 1);
                addLore(line);
                currentLine = word + " ";
                lineSize = wordLength;
                lastLine = line;

            }
        }

        if (lineSize != 0)
            addLore(ChatUtils.lastChatColor(lastLine) + currentLine.substring(0, currentLine.length() - 1));

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
            stack = NBTTagManager.applyTo(stack, tag);
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
            compressedEnchantment.applyTo(stack);
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
                    PotionType.valueOf(firstVal.getType().getName());
                    pm.setBasePotionData(new PotionData(PotionType.getByEffect(firstVal.getType())));
                }
                else
                {
                    Potion pot = new Potion(PotionType.getByEffect(firstVal.getType()), firstVal.getAmplifier(),
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
