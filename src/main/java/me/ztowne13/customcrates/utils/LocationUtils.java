package me.ztowne13.customcrates.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public class LocationUtils {
    private LocationUtils() {
        // EMPTY
    }

    public static String locToString(Location l) {
        return l.getWorld().getName() + ";" + l.getBlockX() + ";" + l.getBlockY() + ";" + l.getBlockZ();
    }

    public static Location stringToLoc(String s) {
        String[] split = s.split(";");
        return new Location(Bukkit.getWorld(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]),
                Integer.parseInt(split[3]));
    }

    public static Location getLocationCentered(Location l) {
        return new Location(l.getWorld(), l.getX() + .5, l.getY() + 1.5, l.getZ() + .5);
    }

    public static void removeDubBlocks(Location l) {
        if (l.getBlock() != null)
            l.getBlock().setType(Material.AIR);
        Location l2 = l.clone();
        l2.setY(l2.getY() + 1);

        if (l2.getBlock() != null)
            l2.getBlock().setType(Material.AIR);
    }
}
