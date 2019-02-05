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
		return !(NMSUtils.getServerVersion().contains("v1_6") || NMSUtils.getServerVersion().contains("v1_7") || NMSUtils.getServerVersion().contains("v1_8"));
	}
	
	public static boolean serverVersion111OrLater()
	{
		return !(NMSUtils.getServerVersion().contains("v1_6") || NMSUtils.getServerVersion().contains("v1_7") || NMSUtils.getServerVersion().contains("v1_8") || NMSUtils.getServerVersion().contains("v1_9") || NMSUtils.getServerVersion().contains("v1_10"));
	}
}
