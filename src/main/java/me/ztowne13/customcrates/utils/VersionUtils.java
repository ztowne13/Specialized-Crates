package me.ztowne13.customcrates.utils;

import org.bukkit.Bukkit;

import java.util.HashMap;

public class VersionUtils
{
    static HashMap<String, Class<?>> cachedNMSClasses = new HashMap<>();
    public static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException
    {
        Class<?> result = cachedNMSClasses.get(nmsClassName);
        if(result == null)
        {
            Class<?> clazz = Class.forName("net.minecraft.server." + getVersionRaw() + "." + nmsClassName);
            cachedNMSClasses.put(nmsClassName, clazz);
            return clazz;
        }

        return result;
    }

    static String cachedVersionRaw = "";
    public static String getVersionRaw()
    {
        if(cachedVersionRaw == "")
        {
            cachedVersionRaw = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        }
        return cachedVersionRaw;
    }

    static String cachedServerVersion = "";
    public static String getServerVersion()
    {
        if(cachedServerVersion == "")
        {
            cachedServerVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }
        return cachedServerVersion;
    }

    public enum Version
    {
        v1_7,
        v1_8,
        v1_8_R3,
        v1_9,
        v1_10,
        v1_11,
        v1_12,
        v1_13,
        v1_14,
        v1_15,
        v1_16,
        v1_17;

        public boolean isServerVersionOrEarlier()
        {
            for (Version version : Version.values())
            {
                if (VersionUtils.getServerVersion().contains(version.toString()))
                    return true;
                if (this == version)
                    break;
            }
            return false;
        }

        public boolean isServerVersionOrLater()
        {
            boolean found = false;

            for (Version version : Version.values())
            {
                if (this == version && !found)
                    found = true;

                if (found && VersionUtils.getServerVersion().contains(version.toString()))
                    return true;
            }
            return false;
        }

    }

}
