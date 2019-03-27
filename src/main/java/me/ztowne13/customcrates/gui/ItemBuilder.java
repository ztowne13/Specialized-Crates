package me.ztowne13.customcrates.gui;

import com.mojang.datafixers.Dynamic;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import me.ztowne13.customcrates.utils.nbt_utils.NBTTagManager;
import me.ztowne13.customcrates.utils.nbt_utils.NBTTagReflection;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;

public class ItemBuilder
{

	ItemStack stack;
	InventoryBuilder builder;

	public ItemBuilder()
	{

	}

	public ItemBuilder(ItemStack fromStack)
	{
		stack = fromStack.clone();
	}

	public ItemBuilder(Material m, int amnt, int byt)
	{
		create(DynamicMaterial.fromString(m.name() + ";" + byt), amnt);
	}

	public ItemBuilder(DynamicMaterial m, int amnt)
	{
		create(m, amnt);
	}

	public void create(DynamicMaterial m, int amnt)
	{
		stack = m.parseItem();
		stack.setAmount(amnt);
		if(m.preProgrammedNBTTag && NMSUtils.Version.v1_12.isServerVersionOrEarlier())
			applyNBTTag(m.nbtTag);
	}
	
	public ItemMeta im()
	{
		return getStack().getItemMeta();
	}
	
	public void setIm(ItemMeta im)
	{
		getStack().setItemMeta(im);
	}
	
	public ItemBuilder setName(String s)
	{
		ItemMeta im = im();
		im.setDisplayName(ChatUtils.toChatColor(s));
		setIm(im);
		return this;
	}

	public String getName(boolean strippedOfColor)
	{
		return strippedOfColor ? ChatUtils.removeColor(im().getDisplayName()) : im().getDisplayName();
	}

	public ItemStack get()
	{
		return getStack();
	}

	public ItemBuilder addLore(String s)
	{
		s = ChatUtils.toChatColor(s);

		ItemMeta im = im();
		ArrayList list = im.getLore() == null || im.getLore().isEmpty() ? new ArrayList() : (ArrayList) im.getLore();

		list.add(s);

		im.setLore(list);
		setIm(im);
		return this;
	}

	public ItemBuilder addAutomaticLore(String lineColor, int charLength, String lore)
	{
		String[] split = lore.split(" ");
		int lineSize = 0;
		String currentLine = "";

		for(String word : split)
		{
			int wordLength = word.length();
			if(lineSize + wordLength <= charLength)
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

		if(lineSize != 0)
			addLore(lineColor + currentLine.substring(0, currentLine.length() - 1));

		return this;
	}

	public ItemBuilder clearLore()
	{
		ItemMeta im = im();
		im.setLore(null);
		setIm(im);
		return this;
	}

	public ItemBuilder setLore(String s)
	{
		clearLore();
		addLore(s);

		return this;
	}

	public ItemBuilder addEnchantment(Enchantment ench, int level)
	{
		getStack().addUnsafeEnchantment(ench, level);
		return this;
	}

	public ItemBuilder applyNBTTag(String tag)
	{
		stack = NBTTagManager.applyTo(stack, tag);
		return this;
	}

	public void applyPlayerHeadName(String name)
	{
		if(DynamicMaterial.PLAYER_HEAD.isSameMaterial(get()))
		{
			SkullMeta skullMeta = (SkullMeta) im();
			skullMeta.setOwner(name);
			setIm(skullMeta);
		}
	}

	public String getPlayerHeadName()
	{
		if(DynamicMaterial.PLAYER_HEAD.isSameMaterial(get()))
		{
			SkullMeta skullMeta = (SkullMeta) im();
			return skullMeta.getOwner();
		}

		return null;
	}

	public ItemStack getStack()
	{
		return stack;
	}

	public void setStack(ItemStack stack)
	{
		this.stack = stack;
	}

	public InventoryBuilder getBuilder()
	{
		return builder;
	}

	public void setBuilder(InventoryBuilder builder)
	{
		this.builder = builder;
	}
}
