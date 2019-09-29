package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;

public class DebugUtils
{
    SpecializedCrates cc;
    boolean forceDebug = true;

    public DebugUtils(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public boolean beta = false;

    public void log(String s)
    {
        boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());
        if (debug)
        {
            Bukkit.getLogger().info("[DEBUG] " + s);
        }
    }

    public void log(String s, Class<?> clas)
    {
        log(s, clas, false);
    }

    public void log(String s, Class<?> clas, boolean dumpStack)
    {
        boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());
        if (debug)
            Bukkit.getLogger().info("[DEBUG] " + clas.getName() + "." + s);
        if(dumpStack)
            dumpStack();
    }

    public void dumpStack()
    {
        boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());
        if(debug)
            Thread.dumpStack();
    }
}
