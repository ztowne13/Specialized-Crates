package me.ztowne13.customcrates.interfaces.externalhooks.holograms.nms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.utils.ReflectionUtilities;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Public Hologram utility by ParaPhoenix aka Phoenix1123.
 * Original credit goes to Janhektor on the Spigot forums.
 * https://www.spigotmc.org/members/janhektor.14134/
 * <p>
 * Feel free to use this however you see fit, and I hope that it is
 * of use. ^-^
 * <p>
 * To stay up to date with the latest version, you can find it on my Gist here:
 * https://gist.github.com/Phoenix1123/4a264e530368f96a435df7b0e3ae65fa
 * <p>
 * A brief explanation on how this class works for those who may not know or are wondering,
 * it uses reflection to access CB/NMS classes, which use an invisible armor stand to create floating names
 * (known as Holograms). These armor stands cannot be interacted through, so please do take note of this,
 * it may be a future feature however it is not an aim for this utility as of current.
 * <p>
 * If you do wish, you can remove this message however the update link would be useful!
 * <p>
 * Thank you! o/
 */
public class NMSHologram extends Hologram {

    private static Class<?> craftWorld;
    private static Class<?> entityClass;
    private static Class<?> nmsWorld;
    private static Class<?> armorStand;
    private static Class<?> entityLiving;
    private static Class<?> spawnPacket;

    static {
        try {
            craftWorld = ReflectionUtilities.getOBCClass("CraftWorld");
            entityClass = ReflectionUtilities.getNMSClass("Entity");
            nmsWorld = ReflectionUtilities.getNMSClass("World");
            armorStand = ReflectionUtilities.getNMSClass("EntityArmorStand");
            entityLiving = ReflectionUtilities.getNMSClass("EntityLiving");
            spawnPacket = ReflectionUtilities.getNMSClass("PacketPlayOutSpawnEntityLiving");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private final List<Integer> ids = new ArrayList<>();
    private final List<Object> entities = new ArrayList<>();
    private final double offset = 0.23D;
    private final List<String> lines = new ArrayList<>();

    public NMSHologram(SpecializedCrates cc, Location location) {
        super(cc, location);
    }

    /**
     * Add a line or multiple, character colours will be converted.
     *
     * @param line - The text to add.
     */
    @Override
    public void addLine(String line) {
        lines.add(line);
        update();
    }

    /**
     * Set the location of the hologram.
     *
     * @param location - The location to set.
     */
    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        update();
    }

    @Override
    public void setLine(int index, String line) {
        lines.set(index, line);
        update();
    }

    @Override
    public void delete() {
        remove();
    }

    /**
     * Display the hologram to a player or multiple
     *
     * @param players - The players to show the hologram to.
     */
    public void displayTo(Player... players) {
        Location current = location.clone().add(0, (offset * lines.size()) - 1.97D, 0);

        for (String str : lines) {
            Object[] packet = getCreatePacket(location, ChatColor.translateAlternateColorCodes('&', str));
            ids.add((Integer) packet[1]);

            for (Player player : players)
                sendPacket(player, packet[0]);

            current.subtract(0, offset, 0);
        }
    }

    /**
     * Delete a hologram from a player or multiple.
     *
     * @param players
     */
    public void removeFrom(Player... players) {
        Object packet = null;

        for (int id : ids)
            packet = getRemovePacket(id);

        for (Player player : players)
            if (packet != null)
                sendPacket(player, packet);
    }

    /**
     * Spawn the hologram for everyone to see.
     */
    public void spawn() {
        Location current = location.clone().add(0, (offset * lines.size()) - 1.97D, 0).add(0, offset, 0);

        for (String str : lines)
            spawnHologram(ChatColor.translateAlternateColorCodes('&', str), current.subtract(0, offset, 0));
    }

    /**
     * Spawns a hologram with -text- at -location-
     */
    private void spawnHologram(String text, Location location) {
        try {
            // The ArmorStand
            Object craftWorld = NMSHologram.craftWorld.cast(location.getWorld());
            Object entityObject = armorStand.getConstructor(nmsWorld).newInstance(NMSHologram.craftWorld.getMethod("getHandle").invoke(craftWorld));

            configureHologram(entityObject, text, location);

            NMSHologram.craftWorld.getMethod("addEntity", entityClass, CreatureSpawnEvent.SpawnReason.class).invoke(craftWorld, entityObject, CreatureSpawnEvent.SpawnReason.CUSTOM);

            entities.add(entityObject);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Delete the hologram from the world.
     */
    public void remove() {
        for (Object ent : entities)
            removeEntity(ent);
    }

    private void removeEntity(Object entity) {
        try {
            Object craftWorld = NMSHologram.craftWorld.cast(location.getWorld());

            nmsWorld.getMethod("removeEntity", entityClass).invoke(NMSHologram.craftWorld.getMethod("getHandle").invoke(craftWorld), entity);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the packet for creating a new Hologram, using EntityArmorStands and PacketPlayOutSpawnEntityLiving
     *
     * @param location - The location for which to spawn the hologram.
     * @param text     - The text (entity name) of the hologram.
     * @return Object - The PacketPlayOutSpawnEntityLiving packet in the form of an Object (Because of reflection, duh ^^)
     */
    private Object[] getCreatePacket(Location location, String text) {
        try {
            // The ArmorStand
            Object entityObject = armorStand.getConstructor(nmsWorld).newInstance(craftWorld.getMethod("getHandle").invoke(craftWorld.cast(location.getWorld())));
            Object id = entityObject.getClass().getMethod("getId").invoke(entityObject);

            configureHologram(entityObject, text, location);

            // Return the packet, and the entity id so we can later remove it.
            return new Object[]{spawnPacket.getConstructor(entityLiving).newInstance(entityObject), id};
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Get the removal packet for the hologram.
     *
     * @param id - The entity ID to remove (ArmorStand)
     * @return The destroy packet object.
     */
    private Object getRemovePacket(int id) {
        try {
            Class<?> packet = ReflectionUtilities.getNMSClass("PacketPlayOutEntityDestroy");
            return packet.getConstructor(int[].class).newInstance(new int[]{id});
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Updates the currently existant hologram.
     */
    public void update() {
        try {
            if (!entities.isEmpty()) { // spawned as an actual entity, moving is ezpz.

                for (int i = 0; i < entities.size(); i++) {
                    Object ent = entities.get(i);

                    if (i > lines.size() - 1) // 1 'hologram' per line
                        removeEntity(ent);
                }

                Location current = location.clone().add(0, (offset * lines.size()) - 1.97D, 0);

                for (int i = 0; i < lines.size(); i++) {
                    String text = ChatColor.translateAlternateColorCodes('&', lines.get(i));

                    if (i >= entities.size()) {
                        spawnHologram(text, current);
                    } else {
                        configureHologram(entities.get(i), text, current);
                    }

                    current.subtract(0, offset, 0);
                }

            } else { // TODO allow the user to update packet holograms

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Configures the hologram properties.
     *
     * @param entityObject - The EntityArmorStand object to modify.
     * @param text         - The text the hologram has.
     * @throws Exception
     */
    private void configureHologram(Object entityObject, String text, Location location) throws Exception {
        // Methods for modifying the properties
        Method setCustomName = entityObject.getClass().getMethod("setCustomName", String.class);
        Method setCustomNameVisible = entityObject.getClass().getMethod("setCustomNameVisible", boolean.class);
        Method setNoGravity = entityObject.getClass().getMethod("setNoGravity", boolean.class); // Previously setGravity(boolean) prior to 1.10
        Method setLocation = entityObject.getClass().getMethod("setLocation", double.class, double.class, double.class, float.class, float.class);
        Method setInvisible = entityObject.getClass().getMethod("setInvisible", boolean.class);

        // Setting the properties
        setCustomName.invoke(entityObject, text);
        setCustomNameVisible.invoke(entityObject, true);
        setNoGravity.invoke(entityObject, true);
        setLocation.invoke(entityObject, location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        setInvisible.invoke(entityObject, true);
    }

    /**
     * Send a packet to a player.
     *
     * @param player
     * @param packet
     */
    private void sendPacket(Player player, Object packet) {
        try {
            if (packet == null)
                return;

            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            Object connection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
            connection.getClass().getMethod("sendPacket", ReflectionUtilities.getNMSClass("Packet")).invoke(connection, packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}

