package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectionUtilities
{
    private static HashMap<Class<?>, HashMap<String, Method>> cachedMethods = new HashMap<>();
    private static HashMap<Object, Object> cachedHandles = new HashMap<>();
    private static HashMap<Class<?>, HashMap<String, Field>> cachedFields = new HashMap<>();
    private static HashMap<String, Class<?>> cachedOBCClass = new HashMap<>();
    private static HashMap<String, Class<?>> cachedNMSClass = new HashMap<>();



    public static void setValue(Object instance, String fieldName, Object value)
            throws Exception
    {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static Object getValue(Object instance, String fieldName)
            throws Exception
    {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static String getVersion()
    {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        String version = name.substring(name.lastIndexOf('.') + 1) + ".";
        return version;
    }

    public static Class<?> getNMSClass(String className)
    {
        if(SpecializedCrates.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached NMS classes: " + cachedNMSClass.size());
        }

        if(cachedNMSClass.containsKey(className))
        {
            return cachedNMSClass.get(className);
        }

        String fullName = "net.minecraft.server." + getVersion() + className;
        Class clazz = null;
        try
        {
            clazz = Class.forName(fullName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        cachedNMSClass.put(className, clazz);

        return clazz;
    }

    public static Class<?> getOBCClass(String className)
    {
        if(SpecializedCrates.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached OBC classes: " + cachedOBCClass.size());
        }

        if(cachedOBCClass.containsKey(className))
        {
            return cachedOBCClass.get(className);
        }

        String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
        Class clazz = null;
        try
        {
            clazz = Class.forName(fullName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        cachedOBCClass.put(className, clazz);

        return clazz;
    }

    public static Object getHandle(Object obj)
    {
        if(SpecializedCrates.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached handles: " + cachedHandles.size());
        }

        if(cachedHandles.containsKey(obj))
        {
            return cachedHandles.get(obj);
        }

        try
        {
            Object returnObj =  getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj);
            cachedHandles.put(obj, returnObj);

            return returnObj;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String name)
    {
        if(SpecializedCrates.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached fields: " + cachedFields.size());
        }

        if(!SpecializedCrates.ENABLE_CACHING)
        {
            return getFieldOriginal(clazz, name);
        }

        if(!cachedFields.containsKey(clazz))
        {
            cachedFields.put(clazz, new HashMap<String, Field>());
        }

        if(cachedFields.get(clazz).containsKey(name))
        {
            return cachedFields.get(clazz).get(name);
        }
        else
        {
            Field field = getFieldOriginal(clazz, name);

            cachedFields.get(clazz).put(name, field);

            return field;
        }
    }

    public static Field getFieldOriginal(Class<?> clazz, String name)
    {
        try
        {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>[] args)
    {
        if(SpecializedCrates.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached methods: " + cachedMethods.size());
        }

        if(!SpecializedCrates.ENABLE_CACHING)
        {
            return getMethodOriginal(clazz, name, args);
        }

        if(!cachedMethods.containsKey(clazz))
        {
            cachedMethods.put(clazz, new HashMap<String, Method>());
        }

        if(cachedMethods.get(clazz).containsKey(name))
        {
            return cachedMethods.get(clazz).get(name);
        }
        else
        {
            Method method = getMethodOriginal(clazz, name, args);

            cachedMethods.get(clazz).put(name, method);

            return method;
        }
    }

    public static Method getMethodOriginal(Class<?> clazz, String name, Class<?>[] args)
    {
        for (Method m : clazz.getMethods())
        {
            if ((m.getName().equals(name)) && ((args.length == 0) || (ClassListEqual(args, m.getParameterTypes()))))
            {
                m.setAccessible(true);
                return m;
            }
        }
        return null;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2)
    {
        boolean equal = true;
        if (l1.length != l2.length)
        {
            return false;
        }
        for (int i = 0; i < l1.length; i++)
        {
            if (l1[i] != l2[i])
            {
                equal = false;
                break;
            }
        }
        return equal;
    }
}
