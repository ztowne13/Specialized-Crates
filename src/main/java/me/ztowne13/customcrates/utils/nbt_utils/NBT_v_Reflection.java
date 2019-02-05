package me.ztowne13.customcrates.utils.nbt_utils;

import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 6/11/16.
 */
public class NBT_v_Reflection
{
	public static Class getCraftItemStack()
	{
		try
		{
			return Class.forName("org.bukkit.craftbukkit." + NMSUtils.getVersionRaw() + ".inventory.CraftItemStack");
		}
		catch(Exception exc)
		{
			ChatUtils.log("Failed to load CraftItemStack. Please check plugin is up to date.");
		}
		return null;
	}

	public static Object getNMSItemStack(ItemStack stack)
	{
		try
		{
			return getCraftItemStack().getMethod("asNMSCopy", ItemStack.class).invoke(getCraftItemStack(), stack);
		}
		catch(Exception exc)
		{
			ChatUtils.log("Failed to load NMS ItemStack. Please check plugin is up to date.");
		}
		return null;
	}

	public static Object getNewNBTTagCompound()
	{
		try
		{
			return Class.forName("net.minecraft.server." + NMSUtils.getVersionRaw() + ".NBTTagCompound").newInstance();
		}
		catch(Exception exc)
		{
			ChatUtils.log("Failed to create new NBT Tag Compound. Please check plugin is up to date.");
		}
		return null;
	}

	public static Object getNBTTagCompound(Object nmsStack)
	{
		try
		{
			return nmsStack.getClass().getMethod("getTag").invoke(nmsStack);
		}
		catch(Exception exc)
		{
			ChatUtils.log("Failed to get existing NBT Tag Compound. Please check plugin is up to date.");
		}
		return null;
	}

	public static ItemStack applyTo(ItemStack item, String tag)
	{
		Object stack = getNMSItemStack(item);
		Object tagCompound = getNBTTagCompound(stack);
		if(tagCompound == null){
			tagCompound = getNewNBTTagCompound();
		}

		if(item.getType().equals(Material.MONSTER_EGG))
		{
			Object idNTC = getNewNBTTagCompound();
			try
			{
				idNTC.getClass().getMethod("setString", String.class, String.class).invoke(idNTC, "id", EntityType.valueOf(tag.toUpperCase()).getName());
				tagCompound.getClass().getMethod("set", String.class, Class.forName("net.minecraft.server." + NMSUtils.getVersionRaw() + ".NBTBase")).invoke(tagCompound, "EntityTag", idNTC);
			}
			catch(Exception exc)
			{
				ChatUtils.log("Failed to get apply monster egg NBT Tag Compound. Please check plugin is up to date.");
			}
		}
		else
		{
			try
			{
				tagCompound.getClass().getMethod("setString", String.class, String.class).invoke(tagCompound, "Potion", "minecraft:" + tag);
			}
			catch(Exception exc)
			{
				ChatUtils.log("Failed to get apply potion NBT Tag Compound. Please check plugin is up to date.");
			}
		}

		try
		{
			stack.getClass().getMethod("setTag", tagCompound.getClass()).invoke(stack, tagCompound);
			return (ItemStack) getCraftItemStack().getMethod("asBukkitCopy", stack.getClass()).invoke(getCraftItemStack(), stack);
		}
		catch(Exception exc)
		{
			ChatUtils.log("Failed to get apply final Tag. Please check plugin is up to date.");
		}
		return null;
	}

	public static List<String> getFrom(ItemStack item)
	{
		List<String> list = new ArrayList<>();

		Object stack = getNMSItemStack(item);
		Object tagCompound = getNBTTagCompound(stack);

		if(item.getType().equals(Material.MONSTER_EGG))
		{
			try
			{
				Object nbtTagCompound = tagCompound.getClass().getMethod("getCompound", String.class).invoke(tagCompound, "EntityTag");
				list.add(nbtTagCompound.getClass().getMethod("getString", String.class).invoke(nbtTagCompound, "id").toString());
			}
			catch(Exception exc)
			{
				ChatUtils.log("Failed to load NBT Tag Compound BASE from stack. Please check plugin is up to date.");
			}
		}
		else
		{
			try
			{
				list.add(tagCompound.getClass().getMethod("getString", String.class).invoke(tagCompound, "Potion").toString().split(":")[1]);
			}
			catch(Exception exc)
			{
				ChatUtils.log("Failed to load NBT Tag Compound from stack. Please check plugin is up to date.");
			}
		}
		return list;
	}
}
