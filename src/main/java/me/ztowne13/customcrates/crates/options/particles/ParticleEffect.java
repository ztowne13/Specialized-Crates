package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ReflectionUtilities;
import me.ztowne13.customcrates.utils.Utils;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public enum ParticleEffect {

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

    private static final Class<?> nmsPacketPlayOutParticle;
    private static final Class<?> nmsEnumParticle;
    private static final Method nmsEnumParticleMethod;
    private static Constructor<?> cachedConstructor = null;

    static {
        nmsEnumParticle = ReflectionUtilities.getNMSClass("EnumParticle");
        nmsPacketPlayOutParticle = ReflectionUtilities.getNMSClass("PacketPlayOutWorldParticles");
        nmsEnumParticleMethod = ReflectionUtilities.getMethod(nmsEnumParticle, "valueOf", String.class);
        try {
            cachedConstructor = nmsPacketPlayOutParticle.getConstructor(
                    nmsEnumParticle, Boolean.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE,
                    Float.TYPE, Float.TYPE, Float.TYPE, Integer.TYPE, int[].class);
        } catch (NoSuchMethodException e) {
            // IGNORED
        }
    }

    private final String particleName;
    private final String enumValue;
    private Object cachedEnum;

    ParticleEffect(String particleName, String enumValue) {
        this.particleName = particleName;
        this.enumValue = enumValue;
    }

    ParticleEffect(String particleName) {
        this(particleName, null);
    }

    ParticleEffect() {
        this(null, null);
    }

    public String getName() {
        return this.particleName;
    }

    public void sendToPlayer(SpecializedCrates sc, Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed,
                             int count) {
        if (!Utils.isPlayerInRange(sc, player, location)) {
            return;
        }
        if (!VersionUtils.getServerVersion().contains("v1_7")) {
            try {
                if (cachedEnum != null) {
                    cachedEnum = nmsEnumParticleMethod.invoke(null, this.enumValue != null ? this.enumValue : name());
                }
                Object packet = cachedConstructor.newInstance(cachedEnum, true,
                        (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY,
                        offsetZ, speed, count, new int[0]);
                Object handle = ReflectionUtilities.getHandle(player);
                Object connection = ReflectionUtilities.getField(handle.getClass(), "playerConnection").get(handle);
                ReflectionUtilities.getMethod(connection.getClass(), "sendPacket").invoke(connection, packet);
            } catch (Exception e) {
                throw new IllegalArgumentException("Unable to send Particle " + name() + ". (Version 1.8 / 1.9)");
            }
        }
    }
}
