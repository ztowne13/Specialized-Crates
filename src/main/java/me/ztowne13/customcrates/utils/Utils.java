package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

public class Utils {

    public static int cachedParticleDistance = -1;
    static Random r = new Random();

    public static boolean hasItemInHand(Player p) {
        return p.getItemInHand() != null && !p.getItemInHand().getType().equals(Material.AIR);
    }

    public static boolean itemHasName(ItemStack stack) {
        return stack.hasItemMeta() && stack.getItemMeta().hasDisplayName();
    }

    public static String getStringFromColor(Color c) {
        if (c.asRGB() == Color.AQUA.asRGB()) {
            return "AQUA";
        } else if (c.asRGB() == Color.BLACK.asRGB()) {
            return "BLACK";
        } else if (c.asRGB() == Color.BLUE.asRGB()) {
            return "BLUE";
        } else if (c.asRGB() == Color.FUCHSIA.asRGB()) {
            return "FUCHSIA";
        } else if (c.asRGB() == Color.GRAY.asRGB()) {
            return "GRAY";
        } else if (c.asRGB() == Color.GREEN.asRGB()) {
            return "GREEN";
        } else if (c.asRGB() == Color.LIME.asRGB()) {
            return "LIME";
        } else if (c.asRGB() == Color.MAROON.asRGB()) {
            return "MAROON";
        } else if (c.asRGB() == Color.NAVY.asRGB()) {
            return "NAVY";
        } else if (c.asRGB() == Color.OLIVE.asRGB()) {
            return "OLIVE";
        } else if (c.asRGB() == Color.ORANGE.asRGB()) {
            return "ORANGE";
        } else if (c.asRGB() == Color.PURPLE.asRGB()) {
            return "PURPLE";
        } else if (c.asRGB() == Color.RED.asRGB()) {
            return "RED";
        } else if (c.asRGB() == Color.SILVER.asRGB()) {
            return "SILVER";
        } else if (c.asRGB() == Color.TEAL.asRGB()) {
            return "TEAL";
        } else if (c.asRGB() == Color.WHITE.asRGB()) {
            return "WHITE";
        } else if (c.asBGR() == Color.YELLOW.asBGR()) {
            return "YELLOW";
        }
        return null;
    }

    public static Color getColorFromString(String s) {
        if (s.equalsIgnoreCase("AQUA")) {
            return Color.AQUA;
        } else if (s.equalsIgnoreCase("BLACK")) {
            return Color.BLACK;
        } else if (s.equalsIgnoreCase("BLUE")) {
            return Color.BLUE;
        } else if (s.equalsIgnoreCase("FUCHSIA")) {
            return Color.FUCHSIA;
        } else if (s.equalsIgnoreCase("GRAY")) {
            return Color.GRAY;
        } else if (s.equalsIgnoreCase("GREEN")) {
            return Color.GREEN;
        } else if (s.equalsIgnoreCase("LIME")) {
            return Color.LIME;
        } else if (s.equalsIgnoreCase("MAROON")) {
            return Color.MAROON;
        } else if (s.equalsIgnoreCase("NAVY")) {
            return Color.NAVY;
        } else if (s.equalsIgnoreCase("OLIVE")) {
            return Color.OLIVE;
        } else if (s.equalsIgnoreCase("ORANGE")) {
            return Color.ORANGE;
        } else if (s.equalsIgnoreCase("PURPLE")) {
            return Color.PURPLE;
        } else if (s.equalsIgnoreCase("RED")) {
            return Color.RED;
        } else if (s.equalsIgnoreCase("SILVER")) {
            return Color.SILVER;
        } else if (s.equalsIgnoreCase("TEAL")) {
            return Color.TEAL;
        } else if (s.equalsIgnoreCase("WHITE")) {
            return Color.WHITE;
        } else if (s.equalsIgnoreCase("YELLOW")) {
            return Color.YELLOW;
        }
        return null;
    }

    public static int getOpenInventorySlots(Player p) {
        int slots = 0;
        for (int i = 0; i < 36; i++) {
            if (p.getInventory().getItem(i) == null || p.getInventory().getItem(i).getType().equals(Material.AIR)) {
                slots++;
            }
        }

        return slots;
    }

    public static String currentTimeParsed() {
        long millis = System.currentTimeMillis();
        Date d = new Date(millis);
        return d.toString();
    }

    public static List<String> onlyLeaveEntriesWithPref(List<String> list, String pref) {
        List<String> newList = new ArrayList<>();
        for (String s : list) {
            if (s.toLowerCase().startsWith(pref.toLowerCase())) {
                newList.add(s);
            }
        }
        return newList;
    }

    public static <T> List<T> iteratorToList(Iterator<T> i) {
        List<T> l = new ArrayList<>();
        while (i.hasNext()) {
            l.add(i.next());
        }
        return l;
    }

    public static List<Object> wrapArrays(List<Object> ar1, List<Object> ar2) {
        ar1.addAll(ar2);
        return ar1;
    }

    public static boolean isPLInstalled(String name) {
        if (Bukkit.getPluginManager().getPlugin(name) != null) {
            return Bukkit.getPluginManager().isPluginEnabled(name);
        }
        return false;
    }

    public static boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isDouble(String s) {
        try {
            Double.valueOf(s);
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isLong(String s) {
        try {
            Long.valueOf(s);
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    public static boolean isBoolean(String s) {
        try {
            return s.equalsIgnoreCase("true") || s.equalsIgnoreCase("false");
        } catch (Exception exc) {
            return false;
        }
    }

    public static void giveAllItem(ItemStack stack) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().addItem(stack);
        }
    }

    public static void addToInfoLog(SpecializedCrates cc, String s, String s2) {
        cc.getSettings().getInfoToLog().remove(s);

        cc.getSettings().getInfoToLog().put(s, s2);
    }

    public static int getRandomNumberExcluding(int limit, List<Integer> exclude) {
        int generated = r.nextInt(limit);
        return exclude.contains(generated) ? getRandomNumberExcluding(limit, exclude) : generated;
    }

    public static void addItemAndDropRest(final Player player, ItemStack stack) {
        addItemAndDropRest(player, stack, true);
    }

    public static int addItemAndDropRest(final Player player, ItemStack stack, boolean doDrop) {
        HashMap<Integer, ItemStack> list = player.getInventory().addItem(stack);
        int count = 0;

        for (ItemStack toDrop : list.values()) {
            if (doDrop)
                player.getWorld().dropItemNaturally(player.getLocation(), toDrop);

            count += toDrop.getAmount();
        }

        return count;
    }

    public static String[] convertSecondToHHMMString(int secondTime) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat df = new SimpleDateFormat("dd:HH:mm:ss");
        df.setTimeZone(tz);
        String time = df.format(new Date(secondTime * 1000L));

        String[] args = time.split(":");
        String seconds = args[3];
        String minutes = args[2];
        String hours = args[1];
        String days = (Integer.parseInt(args[0]) - 1) + "";

        return new String[]{days, hours, minutes, seconds};
    }

    public static boolean isPlayerInRange(SpecializedCrates sc, Player p, Location center) {
        if (!sc.isParticlesEnabled()) {
            return false;
        }

        if (cachedParticleDistance == -1) {
            cachedParticleDistance = (int) SettingsValue.PARTICLE_VIEW_DISTANCE.getValue(sc);
        }

        double distance;
        if (Objects.equals(center.getWorld(), p.getWorld())) {
            distance = center.distance(p.getLocation());
            if (distance > 1.7976931348623157E+308D)
                return false;
            else {
                return distance < cachedParticleDistance;
            }
        }
        return false;
    }
}
