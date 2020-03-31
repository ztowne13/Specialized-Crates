package me.ztowne13.customcrates.crates.options.actions.actionbar;

import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBarV1_7_8 extends ActionBar
{
    @Override
    public void play(Player player, String msg)
    {
        try
        {
            Class c1 = Class.forName("org.bukkit.craftbukkit." + VersionUtils.getServerVersion() + ".entity.CraftPlayer");
            Object p = c1.cast(player);

            Class c4 = VersionUtils.getNmsClass("PacketPlayOutChat");
            Class c5 = VersionUtils.getNmsClass("Packet");

            Method m3 = VersionUtils.getNmsClass("IChatBaseComponent$ChatSerializer")
                    .getDeclaredMethod("a", new Class[]{String.class});
            Object cbc = VersionUtils.getNmsClass("IChatBaseComponent")
                    .cast(m3.invoke(VersionUtils.getNmsClass("IChatBaseComponent$ChatSerializer"),
                            new Object[]{"{\"text\": \"" + msg + "\"}"}));
            Object ppoc = c4.getConstructor(new Class[]{VersionUtils.getNmsClass("IChatBaseComponent"), Byte.TYPE})
                    .newInstance(new Object[]{cbc, (byte) 2});


            Method m1 = c1.getDeclaredMethod("getHandle", new Class[0]);
            Object h = m1.invoke(p, new Object[0]);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", new Class[]{c5});
            m5.invoke(pc, new Object[]{ppoc});
        }
        catch (Exception ex)
        {
            ChatUtils
                    .log("The ACTIONBAR method has failed to run, please make sure you are on the latest version on the plugin and latest version of spigot / bukkit.");
            ex.printStackTrace();
        }
    }
}
