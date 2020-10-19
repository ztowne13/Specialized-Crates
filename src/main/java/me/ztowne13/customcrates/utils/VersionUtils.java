package me.ztowne13.customcrates.utils;

import org.bukkit.Bukkit;

public class VersionUtils {
    private static String cachedVersionRaw = "";
    private static String cachedServerVersion = "";

    public static String getVersionRaw() {
        if (cachedVersionRaw.equals("")) {
            cachedVersionRaw = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        }
        return cachedVersionRaw;
    }

    public static String getServerVersion() {
        if (cachedServerVersion.equals("")) {
            cachedServerVersion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
        }
        return cachedServerVersion;
    }

    public enum Version {
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
        v1_17,
        v1_18;

        public boolean isServerVersionOrEarlier() {
            for (Version version : Version.values()) {
                if (VersionUtils.getServerVersion().contains(version.name()))
                    return true;
                if (this == version)
                    break;
            }
            return false;
        }

        public boolean isServerVersionOrLater() {
            boolean found = false;

            for (Version version : Version.values()) {
                if (this == version && !found)
                    found = true;

                if (found && VersionUtils.getServerVersion().contains(version.name()))
                    return true;
            }
            return false;
        }

    }

}
