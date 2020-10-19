package me.ztowne13.customcrates.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReflectionUtilities {

    private static final Map<Object, Object> cachedHandles = new HashMap<>();
    private static final HashMap<Class<?>, HashMap<String, Field>> cachedFields = new HashMap<>();
    private static final HashMap<String, Class<?>> cachedOBCClass = new HashMap<>();
    private static final HashMap<String, Class<?>> cachedNMSClass = new HashMap<>();

    private ReflectionUtilities() {
        // EMPTY
    }

    public static Map<Object, Object> getCachedHandles() {
        return cachedHandles;
    }

    public static void setValue(Object instance, String fieldName, Object value) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static Object getValue(Object instance, String fieldName)
            throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public static Class<?> getNMSClass(String className) {
        if (DebugUtils.LOG_CACHED_INFO) {
            ChatUtils.log("Cached NMS classes: " + cachedNMSClass.size());
        }

        Class<?> val = cachedNMSClass.get(className);
        if (val != null) {
            return val;
        }

        String fullName = "net.minecraft.server." + VersionUtils.getVersionRaw() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cachedNMSClass.put(className, clazz);

        return clazz;
    }

    public static Class<?> getOBCClass(String className) {
        if (DebugUtils.LOG_CACHED_INFO) {
            ChatUtils.log("Cached OBC classes: " + cachedOBCClass.size());
        }

        Class<?> val = cachedOBCClass.get(className);
        if (val != null) {
            return val;
        }

        String fullName = "org.bukkit.craftbukkit." + VersionUtils.getVersionRaw() + "." + className;
        Class<?> clazz = null;
        try {
            clazz = Class.forName(fullName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cachedOBCClass.put(className, clazz);

        return clazz;
    }

    public static Object getHandle(Object obj) {
        if (DebugUtils.LOG_CACHED_INFO) {
            ChatUtils.log("Cached handles: " + cachedHandles.size());
        }

        Object val = cachedHandles.get(obj);
        if (val != null) {
            return val;
        }

        try {
            Object returnObj = getMethod(obj.getClass(), "getHandle").invoke(obj);
            cachedHandles.put(obj, returnObj);
            return returnObj;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Field getField(Class<?> clazz, String name) {
        if (DebugUtils.LOG_CACHED_INFO) {
            ChatUtils.log("Cached fields: " + cachedFields.size());
        }

        if (!DebugUtils.ENABLE_CACHING) {
            return getFieldOriginal(clazz, name);
        }

        HashMap<String, Field> val = cachedFields.computeIfAbsent(clazz, c -> {
            HashMap<String, Field> blank = new HashMap<>();
            cachedFields.put(clazz, blank);
            return blank;
        });

        Field val2 = val.get(name);
        if (val2 != null) {
            return val2;
        } else {
            Field field = getFieldOriginal(clazz, name);
            cachedFields.get(clazz).put(name, field);
            return field;
        }
    }

    public static Field getFieldOriginal(Class<?> clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Method getMethod(Class<?> clazz, String name, Class<?>... args) {
        try {
            Method method = clazz.getDeclaredMethod(name, args);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void clearLoaded() {
        cachedHandles.clear();
        cachedNMSClass.clear();
        cachedOBCClass.clear();
        cachedFields.clear();
    }
}
