package me.ztowne13.customcrates.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 3/3/16.
 */
public class ChatUtils
{
	public static void log(String[] args)
	{
		Bukkit.getLogger().info("-----------------------------------");
		for(int i = 0; i < args.length; i++)
		{
			Bukkit.getLogger().info(args[i]);
		}
		Bukkit.getLogger().info("-----------------------------------");
	}

	public static void log(String s)
	{
		Bukkit.getLogger().info(s);
	}

	public static void logFailLoad(String crate, String failLoad, String failLoadValue)
	{
		ChatUtils.log(new String[]{"Failed to load " + failLoad + " for crate " + crate + ": " + failLoadValue});
	}

	public static String toChatColor(String s)
	{
		return ChatColor.translateAlternateColorCodes('&', s);
	}

	public static void msg(Player p, String s)
	{
		p.sendMessage(ChatUtils.toChatColor(s));
	}

	public static void msg(CommandSender sender, String s)
	{
		sender.sendMessage(ChatUtils.toChatColor(s));
	}

	public static void msgError(Player p, String s)
	{
		msg(p, "&4&lERROR! &c" + s);
	}

	public static void msgSuccess(Player p, String s)
	{
		msg(p, "&2&lSUCCESS! &a" + s);
	}

	public static void msgInfo(Player p, String s)
	{
		msg(p, "&6&lInfo &e" + s);
	}

	public static String fromChatColor(String s)
	{
		return s.replace("§", "&");
	}

	public static String removeColor(String s)
	{
		s = ChatColor.stripColor(s);
		String newString = "";
		for(int i = 0; i < s.length(); i++)
		{
			if(s.substring(i, i + 1).equalsIgnoreCase("&"))
			{
				i++;
			}
			else
			{
				newString = newString + s.substring(i, i + 1);
			}
		}
		return newString;
	}

	public static String stripFromWhitespace(String s)
	{
		return s.replaceAll("\\s+","");
	}

	public static List<String> removeColorFrom(List list)
	{
		ArrayList<String> newList = new ArrayList<String>();
		for(Object s: list)
		{
			newList.add(fromChatColor(s.toString()));
		}
		return newList;
	}
}
