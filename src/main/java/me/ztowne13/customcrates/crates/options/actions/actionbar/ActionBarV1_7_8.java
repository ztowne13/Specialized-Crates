package me.ztowne13.customcrates.crates.options.actions.actionbar;

import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.ReflectionUtilities;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ActionBarV1_7_8 extends ActionBar {
    @Override
    public void play(Player player, String msg) {
        try {
            Class<?> c1 = ReflectionUtilities.getOBCClass("entity.CraftPlayer");
            Object p = c1.cast(player);

            Class<?> c4 = ReflectionUtilities.getNMSClass("PacketPlayOutChat");
            Class<?> c5 = ReflectionUtilities.getNMSClass("Packet");

            Method m3 = ReflectionUtilities.getNMSClass("IChatBaseComponent$ChatSerializer")
                    .getDeclaredMethod("a", String.class);
            Object cbc = ReflectionUtilities.getNMSClass("IChatBaseComponent")
                    .cast(m3.invoke(ReflectionUtilities.getNMSClass("IChatBaseComponent$ChatSerializer"),
                            "{\"text\": \"" + msg + "\"}"));
            Object ppoc = c4.getConstructor(ReflectionUtilities.getNMSClass("IChatBaseComponent"), Byte.TYPE)
                    .newInstance(cbc, (byte) 2);


            Method m1 = c1.getDeclaredMethod("getHandle");
            Object h = m1.invoke(p);
            Field f1 = h.getClass().getDeclaredField("playerConnection");
            Object pc = f1.get(h);
            Method m5 = pc.getClass().getDeclaredMethod("sendPacket", c5);
            m5.invoke(pc, ppoc);
        } catch (Exception ex) {
            ChatUtils
                    .log("The ACTIONBAR method has failed to run, please make sure you are on the latest version on the plugin and latest version of spigot / bukkit.");
            ex.printStackTrace();
        }
    }
}
