package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;

public class DebugUtils {
    public static final boolean ENABLE_CACHING = true;
    public static final boolean LOG_CACHED_INFO = false;
    public static final boolean OUTPUT_AVERAGE_TICK = false;

    public static final boolean OUTPUT_FILE_SAVE_TIME = false;
    public static final boolean OUTPUT_PLAYER_DATA_LOAD_TIME = false;

    public static final boolean OUTPUT_AVERAGE_ANIMATION_TICK = false;
    private static final boolean FORCE_DEBUG = false;

    private final SpecializedCrates instance;

    private final String[] sort = new String[]
            {
                    ""
            };

    public DebugUtils(SpecializedCrates instance) {
        this.instance = instance;
    }

    public void log(String s) {
        boolean debug = FORCE_DEBUG || Boolean.parseBoolean(instance.getSettings().getConfigValues().get("debug").toString());
        if (debug) {
            Bukkit.getLogger().info(() -> "[DEBUG] " + s);
        }
    }

    public void log(String s, Class<?> clazz) {
        log(s, clazz, false);
    }

    public void log(String s, Class<?> clazz, boolean dumpStack) {
        log(s, clazz, dumpStack, false);
    }

    public void log(String s, Class<?> clazz, boolean dumpStack, boolean toChat) {
        try {
            boolean debug = FORCE_DEBUG || Boolean.parseBoolean(instance.getSettings().getConfigValues().get("debug").toString());

            String msg = clazz.getName() + "." + s;

            boolean found = sort.length == 0;
            for (String toSort : sort) {
                if (msg.startsWith(toSort)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                if (debug) {
                    if (toChat)
                        Bukkit.broadcastMessage("[DEBUG] " + msg);
                    else
                        Bukkit.getLogger().info(() -> "[DEBUG] " + msg);
                }
                if (dumpStack) {
                    dumpStack();
                }
            }
        } catch (Exception exc) {
            // IGNORED
        }
    }

    public void dumpStack() {
        boolean debug = FORCE_DEBUG || Boolean.parseBoolean(instance.getSettings().getConfigValues().get("debug").toString());
        if (debug)
            Thread.dumpStack();
    }
}
