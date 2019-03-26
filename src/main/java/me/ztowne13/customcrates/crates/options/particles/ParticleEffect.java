package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.utils.ReflectionUtilities;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public enum ParticleEffect
{

    HUGE_EXPLOSION("hugeexplosion", "EXPLOSION_HUGE"),

    LARGE_EXPLODE("largeexplode", "EXPLOSION_LARGE"),

    BUBBLE("bubble", "WATER_BUBBLE"),

    SUSPEND("suspend", "SUSPENDED"),

    DEPTH_SUSPEND("depthSuspend", "SUSPENDED_DEPTH"),

    MAGIC_CRIT("magicCrit", "CRIT_MAGIC"),

    MOB_SPELL("mobSpell", "SPELL_MOB"),

    MOB_SPELL_AMBIENT("mobSpellAmbient", "SPELL_MOB_AMBIENT"),

    INSTANT_SPELL("instantSpell", "SPELL_INSTANT"),

    WITCH_MAGIC("witchMagic", "SPELL_WITCH"),

    EXPLODE("explode", "EXPLOSION_NORMAL"),

    SPLASH("splash", "WATER_SPLASH"),

    LARGE_SMOKE("largesmoke", "SMOKE_LARGE"),

    RED_DUST("reddust", "REDSTONE"),

    SNOWBALL_POOF("snowballpoof", "SNOWBALL"),

    ANGRY_VILLAGER("angryVillager", "VILLAGER_ANGRY"),

    HAPPY_VILLAGER("happerVillager", "VILLAGER_HAPPY"),

    EXPLOSION_NORMAL(EXPLODE.getName()),

    EXPLOSION_LARGE(LARGE_EXPLODE.getName()),

    EXPLOSION_HUGE(HUGE_EXPLOSION.getName()),

    FIREWORKS_SPARK("fireworksSpark"),

    WATER_BUBBLE(BUBBLE.getName()),

    WATER_SPLASH(SPLASH.getName()),

    WATER_WAKE,

    SUSPENDED(SUSPEND.getName()),

    SUSPENDED_DEPTH(DEPTH_SUSPEND.getName()),

    CRIT("crit"),

    CRIT_MAGIC(MAGIC_CRIT.getName()),

    SMOKE_NORMAL,

    SMOKE_LARGE(LARGE_SMOKE.getName()),

    SPELL("spell"),

    SPELL_INSTANT(INSTANT_SPELL.getName()),

    SPELL_MOB(MOB_SPELL.getName()),

    SPELL_MOB_AMBIENT(MOB_SPELL_AMBIENT.getName()),

    SPELL_WITCH(WITCH_MAGIC.getName()),

    DRIP_WATER("dripWater"),

    DRIP_LAVA("dripLava"),

    VILLAGER_ANGRY(ANGRY_VILLAGER.getName()),

    VILLAGER_HAPPY(HAPPY_VILLAGER.getName()),

    TOWN_AURA("townaura"),

    NOTE("note"),

    PORTAL("portal"),

    ENCHANTMENT_TABLE("enchantmenttable"),

    FLAME("flame"),

    LAVA("lave"),

    FOOTSTEP("footstep"),

    CLOUD("cloud"),

    REDSTONE("reddust"),

    SNOWBALL("snowballpoof"),

    SNOW_SHOVEL("snowshovel"),

    SLIME("slime"),

    HEART("heart"),

    BARRIER,

    DRAGON_BREATH("dragonbreath"),

    DAMAGE_INDICATOR("damageIndicator"),

    SWEEP_ATTACK("sweepAttack"),

    END_ROD("endRod"),

    FALLING_DUST("fallingDust"),

    ITEM_CRACK,

    BLOCK_CRACK,

    BLOCK_DUST,

    WATER_DROP,

    ITEM_TAKE,

    MOB_APPEARANCE;

    private String particleName;
    private String enumValue;
    private static Class<?> nmsPacketPlayOutParticle = ReflectionUtilities.getNMSClass("PacketPlayOutWorldParticles");
    private static Class<?> nmsEnumParticle;
    private static int particleRange = 25;

    ParticleEffect(String particleName, String enumValue)
    {
        this.particleName = particleName;
        this.enumValue = enumValue;
    }

    ParticleEffect(String particleName)
    {
        this(particleName, null);
    }

    ParticleEffect()
    {
        this(null, null);
    }

    public String getName()
    {
        return this.particleName;
    }

    public void sendToPlayer(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed,
                             int count) throws Exception
    {
        if (!isPlayerInRange(player, location))
        {
            return;
        }
        if (!ReflectionUtilities.getVersion().contains("v1_7"))
        {
            try
            {
                if (nmsEnumParticle == null)
                {
                    nmsEnumParticle = ReflectionUtilities.getNMSClass("EnumParticle");
                }

                Object packet = nmsPacketPlayOutParticle.getConstructor(
                        new Class[]{nmsEnumParticle, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE,
                                Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, int[].class})
                        .newInstance(getEnum(nmsEnumParticle.getName() + "." +
                                        (this.enumValue != null ? this.enumValue : name().toUpperCase())), Boolean.valueOf(true),
                                Float.valueOf((float) location.getX()), Float.valueOf((float) location.getY()),
                                Float.valueOf((float) location.getZ()), Float.valueOf(offsetX), Float.valueOf(offsetY),
                                Float.valueOf(offsetZ), Float.valueOf(speed), Integer.valueOf(count), new int[0]);
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
                ReflectionUtilities.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, packet);
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException("Unable to send Particle " + name() + ". (Version 1.8 / 1.9)");
            }
        }
        else
        {
            try
            {
                if (this.particleName == null) throw new Exception();
                Object packet = nmsPacketPlayOutParticle.getConstructor(
                        new Class[]{String.class, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE,
                                Float.TYPE, Integer.TYPE})
                        .newInstance(this.particleName, Float.valueOf((float) location.getX()),
                                Float.valueOf((float) location.getY()), Float.valueOf((float) location.getZ()),
                                Float.valueOf(offsetX), Float.valueOf(offsetY), Float.valueOf(offsetZ), Float.valueOf(speed),
                                Integer.valueOf(count));
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
                ReflectionUtilities.getMethod(connection.getClass(), "sendPacket", new Class[0]).invoke(connection, packet);
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException("Unable to send Particle " + name() + ". (Invalid Server Version: 1.7)");
            }
        }
    }

    private static Enum<?> getEnum(String enumFullName)
    {
        String[] x = enumFullName.split("\\.(?=[^\\.]+$)");
        if (x.length == 2)
        {
            String enumClassName = x[0];
            String enumName = x[1];
            try
            {
                Class cl = Class.forName(enumClassName);
                return Enum.valueOf(cl, enumName);
            }
            catch (ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static boolean isPlayerInRange(Player p, Location center)
    {
        double distance = 0.0D;
        if (center.getWorld().equals(p.getWorld()))
        {
            if ((distance = center.distance(p.getLocation())) > 1.7976931348623157E+308D) return false;
            {
                return distance < particleRange;
            }
        }
        return false;
    }
}
