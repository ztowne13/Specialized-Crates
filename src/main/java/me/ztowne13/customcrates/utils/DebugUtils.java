package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Bukkit;

public class DebugUtils
{
    SpecializedCrates cc;
    boolean forceDebug = false;

    String[] sort = new String[]
            {
                    "me.ztowne13.customcrates.players",
                    "me.ztowne13.customcrates.interfaces.sql",
            };

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
        try
        {
            boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());

            String msg = clas.getName() + "." + s;

            boolean found = sort.length == 0;
            for (String toSort : sort)
            {
                if (msg.startsWith(toSort))
                {
                    found = true;
                    break;
                }
            }

            if (found)
            {
                if (debug)
                    Bukkit.getLogger().info("[DEBUG] " + msg);
                if (dumpStack)
                    dumpStack();
            }
        }
        catch(Exception exc)
        {

        }
    }

    public void dumpStack()
    {
        boolean debug = forceDebug || Boolean.valueOf(cc.getSettings().getConfigValues().get("debug").toString());
        if(debug)
            Thread.dumpStack();
    }
}
