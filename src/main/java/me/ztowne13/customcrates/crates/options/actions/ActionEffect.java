package me.ztowne13.customcrates.crates.options.actions;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.NMSUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by ztown on 2/14/2017.
 */
public abstract class ActionEffect
{
    CustomCrates cc;

    public ActionEffect(CustomCrates cc)
    {
        this.cc = cc;
    }

    public void playActionBar(Player player, String msg)
    {
        try
        {
            Class c1 = Class.forName("org.bukkit.craftbukkit." + NMSUtils.getServerVersion() + ".entity.CraftPlayer");
            Object p = c1.cast(player);

            Class c4 = NMSUtils.getNmsClass("PacketPlayOutChat");
            Class c5 = NMSUtils.getNmsClass("Packet");

            Object ppoc = null;

            if (NMSUtils.getServerVersion().contains("v1_8") || NMSUtils.getServerVersion().contains("v1_7"))
            {
                Method m3 = NMSUtils.getNmsClass("IChatBaseComponent$ChatSerializer").getDeclaredMethod("a", new Class[] { String.class });
                Object cbc = NMSUtils.getNmsClass("IChatBaseComponent").cast(m3.invoke(NMSUtils.getNmsClass("IChatBaseComponent$ChatSerializer"), new Object[] { "{\"text\": \"" + msg + "\"}" }));
                ppoc = c4.getConstructor(new Class[] { NMSUtils.getNmsClass("IChatBaseComponent"), Byte.TYPE }).newInstance(new Object[] { cbc, (byte)2 });
            }
            else
            {
                Object o = NMSUtils.getNmsClass("ChatComponentText").getConstructor(new Class[] { String.class }).newInstance(new Object[] { msg });
                ppoc = c4.getConstructor(new Class[] { NMSUtils.getNmsClass("IChatBaseComponent"), Byte.TYPE }).newInstance(new Object[] { o, (byte)2 });
            }

            Method m1 = c1.getDeclaredMethod("getHandle", new Class[0]);
            Object h = m1.invoke(p, new Object[0]);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", new Class[] { c5 });
            m5.invoke(pc, new Object[] { ppoc });
        }
        catch (Exception ex)
        {
            ChatUtils.log("The ACTIONBAR method has failed to run, please make sure you are on the latest version on the plugin and latest version of spigot / bukkit.");
            ex.printStackTrace();
        }
    }

    public abstract void newTitle();

    public abstract void playTitle(Player p);

    public abstract void setDisplayTitle(String title);

    public abstract void setDisplaySubtitle(String subtitle);
}
