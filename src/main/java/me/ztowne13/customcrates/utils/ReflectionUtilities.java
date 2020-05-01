package me.ztowne13.customcrates.utils;

import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

public class ReflectionUtilities
{
    private static HashMap<Class<?>, HashMap<String, Method>> cachedMethods = new HashMap<>();
    public static HashMap<Object, Object> cachedHandles = new HashMap<>();
    private static HashMap<Class<?>, HashMap<String, Field>> cachedFields = new HashMap<>();
    private static HashMap<String, Class<?>> cachedOBCClass = new HashMap<>();
    private static HashMap<String, Class<?>> cachedNMSClass = new HashMap<>();
    private static String cachedVersion = "";



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
        if(cachedVersion == "")
        {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            cachedVersion = name.substring(name.lastIndexOf('.') + 1) + ".";
        }
        return cachedVersion;
    }

    public static Class<?> getNMSClass(String className)
    {
        if(DebugUtils.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached NMS classes: " + cachedNMSClass.size());
        }

        Class<?> val = cachedNMSClass.get(className);
        if(val != null)
        {
            return  val;
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
        if(DebugUtils.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached OBC classes: " + cachedOBCClass.size());
        }

        Class<?> val = cachedOBCClass.get(className);
        if(val != null)
        {
            return val;
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
        if(DebugUtils.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached handles: " + cachedHandles.size());
        }

        Object val = cachedHandles.get(obj);
        if(val != null)
        {
            return val;
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
        if(DebugUtils.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached fields: " + cachedFields.size());
        }

        if(!DebugUtils.ENABLE_CACHING)
        {
            return getFieldOriginal(clazz, name);
        }

        HashMap<String, Field> val = cachedFields.get(clazz);
        if(val == null)
        {
            HashMap<String, Field> blank = new HashMap<String, Field>();
            cachedFields.put(clazz, blank);
            val = blank;
        }

        Field val2 = val.get(name);
        if(val2 != null)
        {
            return val2;
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
        if(DebugUtils.LOG_CACHED_INFO)
        {
            ChatUtils.log("Cached methods: " + cachedMethods.size());
        }

        if(!DebugUtils.ENABLE_CACHING)
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

    public static void clearLoaded()
    {
        cachedMethods.clear();
        cachedHandles.clear();
        cachedNMSClass.clear();
        cachedOBCClass.clear();
        cachedFields.clear();
    }
}
