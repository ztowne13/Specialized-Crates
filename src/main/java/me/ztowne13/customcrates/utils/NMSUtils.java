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

	public static boolean serverVersion17OrLater()
	{
		return !(NMSUtils.getServerVersion().contains("v1_6") || NMSUtils.getServerVersion().contains("v1_7"));
	}

	public static boolean serverVersion110OrLater()
	{
		return serverVersion17OrLater() && !(NMSUtils.getServerVersion().contains("v1_8"));
	}
	
	public static boolean serverVersion111OrLater()
	{
		return serverVersion110OrLater() && !(NMSUtils.getServerVersion().contains("v1_9") || NMSUtils.getServerVersion().contains("v1_10"));
	}

	public static boolean serverVersion113OrLater()
	{
		return serverVersion111OrLater() && !(NMSUtils.getServerVersion().contains("v1_11") || NMSUtils.getServerVersion().contains("v1_12"));
	}

	public enum Version {
		v1_7, v1_8, v1_9, v1_10, v1_11, v1_12, v1_13;

		public boolean isServerVersionEarlier() {
			for (Version version : Version.values()) {
				if (this == version)
					break;

				if (NMSUtils.getServerVersion().contains(version.toString()))
					return true;
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
