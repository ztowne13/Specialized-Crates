package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.PlaceHolderAPIHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ztowne13 on 3/3/16.
 */
public class ChatUtils {

    private ChatUtils() {
        // EMPTY
    }

    public static void log(String[] args) {
        Bukkit.getLogger().info("-----------------------------------");
        for (String arg : args) {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatUtils.toChatColor(arg));
        }
        Bukkit.getLogger().info("-----------------------------------");
    }

    public static void log(String s) {
        Bukkit.getServer().getConsoleSender().sendMessage(ChatUtils.toChatColor("&7" + s));
    }

    public static void logFailLoad(String crate, String failLoad, String failLoadValue) {
        log(new String[]{"Failed to load " + failLoad + " for crate " + crate + ": " + failLoadValue});
    }

    public static String formatAndColor(SpecializedCrates specializedCrates, Player player, String str) {
        if (specializedCrates.isUsingPlaceholderAPI()) {
            str = PlaceHolderAPIHandler.setPlaceHolders(player, str);
        }

        return toChatColor(str);
    }

    public static void msg(Player p, String s) {
        p.sendMessage(toChatColor(s));
    }

    public static void msg(CommandSender sender, String s) {
        sender.sendMessage(toChatColor(s));
    }

    public static void msgError(Player p, String s) {
        msg(p, "&4&lERROR! &c" + s);
    }

    public static void msgSuccess(Player p, String s) {
        msg(p, "&2&lSUCCESS! &a" + s);
    }

    public static void msgInfo(Player p, String s) {
        msg(p, "&6&lInfo &e" + s);
    }

    public static void msgHey(Player p, String s) {
        msg(p, "&6&lHey! &e" + s);
    }

    public static String toChatColor(String s) {
        s = s.replace("<!!special_chat_encoding!!>", "§");
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String fromChatColor(String s) {
        return s.replace("§", "&");
    }

    public static String stripQuotes(String s) {
        while (s.startsWith("'") || s.startsWith("\""))
            s = s.substring(1);

        while (s.endsWith("'") || s.endsWith("\""))
            s = s.substring(0, s.length() - 1);

        return s;
    }

    public static String removeColor(String s) {
        s = ChatColor.stripColor(s);
        StringBuilder newString = new StringBuilder();
        int i = 0;
        while (i < s.length()) {
            if (s.charAt(i) != '&') {
                newString.append(s.charAt(i));
            }
            i++;
        }
        return newString.toString();
    }

    public static String stripFromWhitespace(String s) {
        return s.replaceAll("\\s+", "");
    }

    public static List<String> removeColorFrom(List<?> list) {
        ArrayList<String> newList = new ArrayList<>();
        for (Object s : list) {
            newList.add(ChatColor.stripColor(s.toString()));
        }
        return newList;
    }

    public static List<String> fromColor(List<?> list) {
        ArrayList<String> newList = new ArrayList<>();
        for (Object s : list) {
            newList.add(s.toString().replace("§", "&"));
        }
        return newList;
    }

    public static String lastChatColor(String s) {
        String original = fromChatColor(s);
        int index = original.lastIndexOf('&');
        if (index < 0 || index == original.length() - 1) {
            return toChatColor("&f");
        } else {
            return toChatColor(original.substring(index, index + 2));
        }
    }
}
