package me.ztowne13.customcrates.utils;

import org.bukkit.Bukkit;

public class NMSUtils
{
	public static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException 
	{
	    return Class.forName("net.minecraft.server." + getVersionRaw() + "." + nmsClassName);
	}

	public static String getVersionRaw()
	{
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	public static String getServerVersion()
	{
	    return Bukkit.getServer().getClass().getPackage().getName().substring(23);
	}

	public enum Version {
		v1_7, v1_8, v1_9, v1_10, v1_11, v1_12, v1_13;

		public boolean isServerVersionOrEarlier() {
			for (Version version : Version.values()) {
				if (NMSUtils.getServerVersion().contains(version.toString()))
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

				if (found && NMSUtils.getServerVersion().contains(version.toString()))
					return true;
			}
			return false;
		}

	}

}
