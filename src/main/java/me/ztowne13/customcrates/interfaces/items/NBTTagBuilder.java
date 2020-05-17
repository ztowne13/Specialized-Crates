package me.ztowne13.customcrates.interfaces.items;

import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.ReflectionUtilities;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Created by ztowne13 on 6/11/16.
 */
public class NBTTagBuilder
{
    public static Class getCraftItemStack()
    {
        try
        {
            return Class.forName("org.bukkit.craftbukkit." + VersionUtils.getVersionRaw() + ".inventory.CraftItemStack");
        }
        catch (Exception exc)
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
        catch (Exception exc)
        {
            ChatUtils.log("Failed to load NMS ItemStack. Please check plugin is up to date.");
        }
        return null;
    }

    public static Object getNewNBTTagCompound()
    {
        try
        {
            return Class.forName("net.minecraft.server." + VersionUtils.getVersionRaw() + ".NBTTagCompound").newInstance();
        }
        catch (Exception exc)
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
        catch (Exception exc)
        {
            ChatUtils.log("Failed to get existing NBT Tag Compound. Please check plugin is up to date.");
        }
        return null;
    }

    public static ItemStack applyTo(ItemStack item, String tag)
    {

        Object stack = getNMSItemStack(item);
        Object tagCompound = getNBTTagCompound(stack);
        if (tagCompound == null)
        {
            tagCompound = getNewNBTTagCompound();
        }

        String[] args = tag.split(" ");
        String key = null, value = null;

        try
        {
            key = args[0];

            String remainingValue = "";
            for(int i = 1; i < args.length; i++)
            {
                remainingValue += args[i] + " ";
            }
            remainingValue = remainingValue.substring(0, remainingValue.length() - 1);

            value = remainingValue.replaceAll(",Properties:\\{textures:\\[0:\\{Value:", ",Properties:{textures:[{Value:");
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            ChatUtils.log("Tag " + tag +
                    " is not formatted 'TagType Tag' (without the quotes). Try using the in-game config, it does NBT tags automatically!");
        }

        try
        {
            if (value.startsWith("[") && value.endsWith("]"))
            {
                try
                {
                    Class clazz = VersionUtils.getNmsClass("MojangsonParser");
                    Object comp = clazz.getMethod("parse", String.class).invoke(clazz, "{" + key + ":" + value + "}");
                    Object nbtBase = ReflectionUtilities.getMethod(comp.getClass(), "get", new Class[]{String.class}).invoke(comp, key);

                    tagCompound.getClass().getMethod("set", String.class, VersionUtils.getNmsClass("NBTBase"))
                            .invoke(tagCompound, key, nbtBase);
                }
                catch(Exception exc) {
                    exc.printStackTrace();
                }
            }
            else if (value.startsWith("{") && value.endsWith("}"))
            {
                Class clazz = VersionUtils.getNmsClass("MojangsonParser");
                Object newComp = clazz.getMethod("parse", String.class).invoke(clazz, value);

                tagCompound.getClass().getMethod("set", String.class, VersionUtils.getNmsClass("NBTBase"))
                        .invoke(tagCompound, key, newComp);
            }

            else if ((value.startsWith("'") && value.endsWith("'")) || (value.startsWith("\"") && value.endsWith("\"")))
            {
                value = ChatUtils.stripQuotes(value);

                if (value.equalsIgnoreCase("1b"))
                {
                    tagCompound.getClass().getMethod("setInt", String.class, int.class)
                            .invoke(tagCompound, key, 1);
                }
                else
                {
                    tagCompound.getClass().getMethod("setString", String.class, String.class)
                            .invoke(tagCompound, key, value);
                }
            }
            else if (Utils.isInt(value))
            {
                tagCompound.getClass().getMethod("setInt", String.class, int.class)
                        .invoke(tagCompound, key, Integer.parseInt(value));
            }
            else if (Utils.isDouble(value))
            {
                tagCompound.getClass().getMethod("setDouble", String.class, double.class)
                        .invoke(tagCompound, key, Double.valueOf(value));
            }
            else
            {
                tagCompound.getClass().getMethod("setString", String.class, String.class)
                        .invoke(tagCompound, key, value);
            }
        }
        catch (Exception exc)
        {
            ChatUtils.log("Failed to apply '" + key + " " + value + "' tag. Please check plugin is up to date.");
            exc.printStackTrace();
        }

        try
        {
            stack.getClass().getMethod("setTag", tagCompound.getClass()).invoke(stack, tagCompound);
            ItemStack toReturn = (ItemStack) getCraftItemStack().getMethod("asBukkitCopy", stack.getClass())
                    .invoke(getCraftItemStack(), stack);

            return toReturn;
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            ChatUtils.log("Failed to get apply final Tag. Please check plugin is up to date.");
        }
        return null;
    }

    private static String[] excludedTags = new String[]{
            "display",
            "Enchantments",
            "ench",
            "HideFlags",
            "Potion",
            "StoredEnchantments",

            // Don't work
            "pages"
    };

    private static String[] booleanTags = new String[]{
            "Unbreakable"

    };

    public static List<String> getFrom(ItemStack item, boolean ignoreExcluded)
    {
        List<String> list = new ArrayList<>();

        Object stack = getNMSItemStack(item);
        Object tagCompound = getNBTTagCompound(stack);

        try
        {
            Set<String> keys = (Set<String>) tagCompound.getClass().getMethod(
                    VersionUtils.Version.v1_13.isServerVersionOrLater() ? "getKeys" : "c").invoke(tagCompound);

            for (String key : keys)
            {
                boolean toSkip = false;
                for (String excludedTag : excludedTags)
                {
                    if (key.equalsIgnoreCase(excludedTag))
                    {
                        toSkip = true;
                        break;
                    }
                }

                if (!toSkip || ignoreExcluded)
                {
                    Object nbtBase;
                    if (Arrays.asList(booleanTags).contains(key))
                    {
                        nbtBase = tagCompound.getClass().getMethod("getBoolean", String.class).invoke(tagCompound, key);
                        if ((boolean) nbtBase)
                            list.add(key + " " + 1);
                        else
                            list.add(key + " " + 0);
                    }
                    else
                    {
                        nbtBase = tagCompound.getClass().getMethod("get", String.class).invoke(tagCompound, key);
                        String baseAsString = nbtBase.toString();
//                        if(key.equalsIgnoreCase("pages"))
//                        {
//                            baseAsString = baseAsString.replaceAll("\\\\\\\\", "\\\\");;
//                            baseAsString = baseAsString.replace("\\\"", "\"");
//                        }
                        list.add(key + " " + baseAsString);
                    }

                }
            }
        }
        catch (Exception exc)
        {
            //exc.printStackTrace();
        }
        return list;
    }
}
