package me.ztowne13.customcrates.utils.nbt_utils;

import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by ztowne13 on 3/7/16.
 */
public class NBTUtilsV1_9
{
	public static ItemStack applyTo(ItemStack item, String tag)
	{
		/*if(NMSUtils.getServerVersion().contains("R2"))
		{
			return NBT_v_R2.applyToV_R2(item, tag);
		}
		else
		{
			return NBT_v_R1.applyToV_R1(item, tag);
		}*/
		return NBT_v_Reflection.applyTo(item, tag);
	}

	public static List<String> getFrom(ItemStack item)
	{
		/*if(NMSUtils.getServerVersion().contains("R2"))
		{
			return NBT_v_R2.getFrom(item);
		}
		else
		{
			return NBT_v_R1.getFrom(item);
		}*/
		return NBT_v_Reflection.getFrom(item);
	}
}
