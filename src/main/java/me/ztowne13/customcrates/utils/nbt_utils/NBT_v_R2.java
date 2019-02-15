package me.ztowne13.customcrates.utils.nbt_utils;

import me.ztowne13.customcrates.gui.DynamicMaterial;
import net.minecraft.server.v1_9_R2.NBTTagCompound;
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 5/13/16.
 */
@Deprecated
public class NBT_v_R2
{
	/*public static ItemStack applyToV_R2(ItemStack item, String tag)
	{
		net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tagCompound = stack.getTag();
		if(tagCompound == null){
			tagCompound = new NBTTagCompound();
		}

		if(item.getType().equals(DynamicMaterial.MONSTER_EGG.toMaterial()))
		{
			NBTTagCompound id = new NBTTagCompound();
			id.setString("id", EntityType.valueOf(tag.toUpperCase()).getName());
			tagCompound.set("EntityTag", id);
		}
		else
		{
			tagCompound.setString("Potion", "minecraft:" + tag);
		}

		stack.setTag(tagCompound);
		return CraftItemStack.asBukkitCopy(stack);
	}

	public static List<String> getFrom(ItemStack item)
	{
		List<String> list = new ArrayList<>();

		net.minecraft.server.v1_9_R2.ItemStack stack = CraftItemStack.asNMSCopy(item);
		net.minecraft.server.v1_9_R2.NBTTagCompound tagCompound = stack.getTag();

		if(item.getType().equals(DynamicMaterial.MONSTER_EGG.toMaterial()))
		{
			list.add(tagCompound.getCompound("EntityTag").getString("id"));
		}
		else
		{
			try
			{
				list.add(tagCompound.getString("Potion").split(":")[1]);
			}
			catch(Exception exc)
			{

			}
		}
		return list;
	}*/
}
