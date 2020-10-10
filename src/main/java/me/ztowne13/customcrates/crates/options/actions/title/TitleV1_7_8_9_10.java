package me.ztowne13.customcrates.crates.options.actions.title;

import me.ztowne13.customcrates.utils.ReflectionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Minecraft 1.8 (1.9) TitleV1_7_8_9_10
 *
 * @author Maxim Van de Wynckel
 * @version 1.0.5
 */
public class TitleV1_7_8_9_10 implements Title {
    /* TitleV1_7_8_9_10 packet */
    private static Class<?> packetTitle;
    /* TitleV1_7_8_9_10 packet actions ENUM */
    private static Class<?> packetActions;
    /* Chat serializer */
    private static Class<?> nmsChatSerializer;
    private static Class<?> chatBaseComponent;
    private static Field playerConnection;
    private static Method sendPacket;
    /* TitleV1_7_8_9_10 text and color */
    private String title;
    private ChatColor titleColor = ChatColor.WHITE;
    /* Subtitle text and color */
    private String subtitle;
    private ChatColor subtitleColor = ChatColor.WHITE;
    /* TitleV1_7_8_9_10 timings */
    private int fadeInTime;
    private int stayTime;
    private int fadeOutTime;
    private boolean ticks = false;

    public TitleV1_7_8_9_10() {
        this("", "");
    }

    /**
     * Create a new 1.8 title
     *
     * @param title TitleV1_7_8_9_10
     */
    public TitleV1_7_8_9_10(String title) {
        this(title, "");
    }

    /**
     * Create a new 1.8 title
     *
     * @param title    TitleV1_7_8_9_10 text
     * @param subtitle Subtitle text
     */
    public TitleV1_7_8_9_10(String title, String subtitle) {
        this(title, subtitle, -1, -1, -1);
    }

    /**
     * Copy 1.8 title
     *
     * @param title TitleV1_7_8_9_10
     */
    public TitleV1_7_8_9_10(TitleV1_7_8_9_10 title) {
        // Copy title
        this.title = title.getTitle();
        this.subtitle = title.getSubtitle();
        this.titleColor = title.getTitleColor();
        this.subtitleColor = title.getSubtitleColor();
        this.fadeInTime = title.getFadeInTime();
        this.fadeOutTime = title.getFadeOutTime();
        this.stayTime = title.getStayTime();
        this.ticks = title.isTicks();
        loadClasses();
    }

    /**
     * Create a new 1.8 title
     *
     * @param title       TitleV1_7_8_9_10 text
     * @param subtitle    Subtitle text
     * @param fadeInTime  Fade in time
     * @param stayTime    Stay on screen time
     * @param fadeOutTime Fade out time
     */
    public TitleV1_7_8_9_10(String title, String subtitle, int fadeInTime, int stayTime,
                            int fadeOutTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.fadeInTime = fadeInTime;
        this.stayTime = stayTime;
        this.fadeOutTime = fadeOutTime;
        loadClasses();
    }

    /**
     * Load spigot and NMS classes
     */
    private void loadClasses() {
        if (packetTitle == null) {
            packetTitle = ReflectionUtilities.getNMSClass("PacketPlayOutTitle");
            packetActions = ReflectionUtilities.getNMSClass("PacketPlayOutTitle$EnumTitleAction");
            chatBaseComponent = ReflectionUtilities.getNMSClass("IChatBaseComponent");
            nmsChatSerializer = ReflectionUtilities.getNMSClass("ChatComponentText");
            /* NMS player and connection */
            Class<?> nmsPlayer = ReflectionUtilities.getNMSClass("EntityPlayer");
            Class<?> nmsPlayerConnection = ReflectionUtilities.getNMSClass("PlayerConnection");
            playerConnection = ReflectionUtilities.getField(nmsPlayer, "playerConnection");
            sendPacket = ReflectionUtilities.getMethod(nmsPlayerConnection, "sendPacket");
        }
    }

    /**
     * Get title text
     *
     * @return TitleV1_7_8_9_10 text
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set title text
     *
     * @param title TitleV1_7_8_9_10
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get subtitle text
     *
     * @return Subtitle text
     */
    public String getSubtitle() {
        return this.subtitle;
    }

    /**
     * Set subtitle text
     *
     * @param subtitle Subtitle text
     */
    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    /**
     * Set timings to ticks
     */
    public void setTimingsToTicks() {
        ticks = true;
    }

    /**
     * Set timings to seconds
     */
    public void setTimingsToSeconds() {
        ticks = false;
    }

    /**
     * Send the title to a player
     *
     * @param player Player
     */
    public void send(Player player) {
        if (packetTitle != null) {
            // First reset previous settings
            resetTitle(player);
            try {
                // Send timings first
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = playerConnection.get(handle);
                Object[] actions = packetActions.getEnumConstants();
                Object packet = packetTitle.getConstructor(packetActions,
                        chatBaseComponent, Integer.TYPE, Integer.TYPE,
                        Integer.TYPE).newInstance(actions[2], null,
                        fadeInTime * (ticks ? 1 : 20),
                        stayTime * (ticks ? 1 : 20),
                        fadeOutTime * (ticks ? 1 : 20));
                // Send if set
                if (fadeInTime != -1 && fadeOutTime != -1 && stayTime != -1)
                    sendPacket.invoke(connection, packet);

                // Send title
                Object serialized = nmsChatSerializer.getConstructor(
                        String.class).newInstance(
                        ChatColor.translateAlternateColorCodes('&', title));
                packet = packetTitle.getConstructor(packetActions,
                        chatBaseComponent).newInstance(actions[0], serialized);
                sendPacket.invoke(connection, packet);
                if (!subtitle.equals("")) {
                    // Send subtitle if present
                    serialized = nmsChatSerializer.getConstructor(String.class)
                            .newInstance(
                                    ChatColor.translateAlternateColorCodes('&',
                                            subtitle));
                    packet = packetTitle.getConstructor(packetActions,
                            chatBaseComponent).newInstance(actions[1],
                            serialized);
                    sendPacket.invoke(connection, packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTimes(Player player) {
        if (TitleV1_7_8_9_10.packetTitle != null) {
            try {
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = playerConnection.get(handle);
                Object[] actions = TitleV1_7_8_9_10.packetActions.getEnumConstants();
                Object packet = TitleV1_7_8_9_10.packetTitle.getConstructor(
                        TitleV1_7_8_9_10.packetActions, chatBaseComponent,
                        Integer.TYPE, Integer.TYPE, Integer.TYPE)
                        .newInstance(
                                actions[2],
                                null,
                                this.fadeInTime * (this.ticks ? 1 : 20),
                                this.stayTime * (this.ticks ? 1 : 20),
                                this.fadeOutTime * (this.ticks ? 1 : 20));
                if ((this.fadeInTime != -1) && (this.fadeOutTime != -1)
                        && (this.stayTime != -1)) {
                    sendPacket.invoke(connection, packet);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateTitle(Player player) {
        if (TitleV1_7_8_9_10.packetTitle != null) {
            try {
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = playerConnection.get(handle);
                Object[] actions = TitleV1_7_8_9_10.packetActions.getEnumConstants();
                Object serialized = nmsChatSerializer.getConstructor(
                        String.class)
                        .newInstance(
                                ChatColor.translateAlternateColorCodes('&',
                                        this.title));
                Object packet = TitleV1_7_8_9_10.packetTitle
                        .getConstructor(
                                TitleV1_7_8_9_10.packetActions,
                                chatBaseComponent).newInstance(
                                actions[0], serialized);
                sendPacket.invoke(connection, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateSubtitle(Player player) {
        if (TitleV1_7_8_9_10.packetTitle != null) {
            try {
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = playerConnection.get(handle);
                Object[] actions = TitleV1_7_8_9_10.packetActions.getEnumConstants();
                Object serialized = nmsChatSerializer.getConstructor(
                        String.class)
                        .newInstance(
                                ChatColor.translateAlternateColorCodes('&',
                                        this.subtitle));
                Object packet = TitleV1_7_8_9_10.packetTitle
                        .getConstructor(
                                TitleV1_7_8_9_10.packetActions,
                                chatBaseComponent).newInstance(
                                actions[1], serialized);
                sendPacket.invoke(connection, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Broadcast the title to all players
     */
    public void broadcast() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            send(p);
        }
    }

    /**
     * Clear the title
     *
     * @param player Player
     */
    public void clearTitle(Player player) {
        try {
            // Send timings first
            Object handle = ReflectionUtilities.getHandle(player);
            Object connection = playerConnection.get(handle);
            Object[] actions = packetActions.getEnumConstants();
            Object packet = packetTitle.getConstructor(packetActions,
                    chatBaseComponent).newInstance(actions[3], null);
            sendPacket.invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reset the title settings
     *
     * @param player Player
     */
    public void resetTitle(Player player) {
        try {
            // Send timings first
            Object handle = ReflectionUtilities.getHandle(player);
            Object connection = playerConnection.get(handle);
            Object[] actions = packetActions.getEnumConstants();
            Object packet = packetTitle.getConstructor(packetActions,
                    chatBaseComponent).newInstance(actions[4], null);
            sendPacket.invoke(connection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ChatColor getTitleColor() {
        return titleColor;
    }

    /**
     * Set the title color
     *
     * @param color Chat color
     */
    public void setTitleColor(ChatColor color) {
        this.titleColor = color;
    }

    public ChatColor getSubtitleColor() {
        return subtitleColor;
    }

    /**
     * Set the subtitle color
     *
     * @param color Chat color
     */
    public void setSubtitleColor(ChatColor color) {
        this.subtitleColor = color;
    }

    public int getFadeInTime() {
        return fadeInTime;
    }

    /**
     * Set title fade in time
     *
     * @param time Time
     */
    public void setFadeInTime(int time) {
        this.fadeInTime = time;
    }

    public int getFadeOutTime() {
        return fadeOutTime;
    }

    /**
     * Set title fade out time
     *
     * @param time Time
     */
    public void setFadeOutTime(int time) {
        this.fadeOutTime = time;
    }

    public int getStayTime() {
        return stayTime;
    }

    /**
     * Set title stay time
     *
     * @param time Time
     */
    public void setStayTime(int time) {
        this.stayTime = time;
    }

    public boolean isTicks() {
        return ticks;
    }
}