package me.ztowne13.customcrates.crates.options.rewards;

import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ztown on 2/17/2017.
 */
public class NMSGlowEffect extends GlowEffect
{
    public NMSGlowEffect(ItemStack stack)
    {
        super(stack);
    }

    @Override
    public ItemStack apply()
    {
        net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);
        NBTTagCompound tag = null;
        if (!nmsStack.hasTag())
        {
            tag = new NBTTagCompound();
            nmsStack.setTag(tag);
        }
        if (tag == null) tag = nmsStack.getTag();
        NBTTagList ench = new NBTTagList();
        tag.set("ench", ench);
        nmsStack.setTag(tag);
        return CraftItemStack.asCraftMirror(nmsStack);
    }
}
