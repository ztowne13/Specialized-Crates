package me.ztowne13.customcrates.gui;

import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

	public ItemBuilder setLore(String s)
	{
		ItemMeta im = im();
		im.setLore(null);
		setIm(im);

		addLore(s);

		return this;
	}

	public ItemBuilder addEnchantment(Enchantment ench, int level)
	{
		getStack().addUnsafeEnchantment(ench, level);
		return this;
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
