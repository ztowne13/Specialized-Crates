package me.ztowne13.customcrates.crates.types.display;

import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;

/**
 * Created by ztowne13 on 2/27/16.
 */
public enum EntityTypes
{
    // v1.8
    PLAYER(2, VersionUtils.Version.v1_8),
    SPIDER(1, VersionUtils.Version.v1_8),
    GIANT(2, VersionUtils.Version.v1_8),
    GHAST(3, VersionUtils.Version.v1_8),
    BLAZE(2, VersionUtils.Version.v1_8),
    ENDER_DRAGON(5, VersionUtils.Version.v1_8),
    GUARDIAN(2, VersionUtils.Version.v1_8),
    SQUID(1, VersionUtils.Version.v1_8),
    BAT(.5, VersionUtils.Version.v1_8),
    CAVE_SPIDER(.5, VersionUtils.Version.v1_8),
    CHICKEN(1, VersionUtils.Version.v1_8),
    COW(1, VersionUtils.Version.v1_8),
    CREEPER(2, VersionUtils.Version.v1_8),
    ENDERMAN(2.9, VersionUtils.Version.v1_8),
    ENDERMITE(.5, VersionUtils.Version.v1_8),
    HORSE(1.2, VersionUtils.Version.v1_8),
    IRON_GOLEM(2.7, VersionUtils.Version.v1_8),
    MAGMA_CUBE(1.7, VersionUtils.Version.v1_8),
    MUSHROOM_COW(1.4, VersionUtils.Version.v1_8),
    OCELOT(.5, VersionUtils.Version.v1_8),
    PIG(.8, VersionUtils.Version.v1_8),
    PIG_ZOMBIE(2, VersionUtils.Version.v1_8),
    RABBIT(.5, VersionUtils.Version.v1_8),
    SHEEP(1.2, VersionUtils.Version.v1_8),
    SILVERFISH(.5, VersionUtils.Version.v1_8),
    SKELETON(2, VersionUtils.Version.v1_8),
    SLIME(1.7, VersionUtils.Version.v1_8),
    SNOWMAN(2, VersionUtils.Version.v1_8),
    VILLAGER(2, VersionUtils.Version.v1_8),
    WITCH(2, VersionUtils.Version.v1_8),
    WITHER(3.5, VersionUtils.Version.v1_8),
    WOLF(.8, VersionUtils.Version.v1_8),
    ZOMBIE(2, VersionUtils.Version.v1_8),

    // v1.9
    SHULKER(2, VersionUtils.Version.v1_9),

    // v1.10
    POLAR_BEAR(2, VersionUtils.Version.v1_10),

    // v1.11
    ELDER_GUARDIAN(2, VersionUtils.Version.v1_11),
    WITHER_SKELETON(2, VersionUtils.Version.v1_11),
    STRAY(2, VersionUtils.Version.v1_11),
    HUSK(2, VersionUtils.Version.v1_11),
    ZOMBIE_VILLAGER(2, VersionUtils.Version.v1_11),
    SKELETON_HORSE(2, VersionUtils.Version.v1_11),
    ZOMBIE_HORSE(2, VersionUtils.Version.v1_11),
    DONKEY(2, VersionUtils.Version.v1_11),
    MULE(2, VersionUtils.Version.v1_11),
    EVOKER(2, VersionUtils.Version.v1_11),
    VEX(2, VersionUtils.Version.v1_11),
    VINDICATOR(2, VersionUtils.Version.v1_11),
    LLAMA(2, VersionUtils.Version.v1_11),

    // v1.12
    ILLUSIONER(2, VersionUtils.Version.v1_12),
    PARROT(2, VersionUtils.Version.v1_12),

    // v1.13
    TURTLE(2, VersionUtils.Version.v1_13),
    PHANTOM(2, VersionUtils.Version.v1_13),
    TRIDENT(2, VersionUtils.Version.v1_13),
    COD(2, VersionUtils.Version.v1_13),
    SALMON(2, VersionUtils.Version.v1_13),
    PUFFERFISH(2, VersionUtils.Version.v1_13),
    TROPICAL_FISH(2, VersionUtils.Version.v1_13),
    DROWNED(2, VersionUtils.Version.v1_13),
    DOLPHIN(2, VersionUtils.Version.v1_13),

    // v1.14
    CAT(2, VersionUtils.Version.v1_14),
    PANDA(2, VersionUtils.Version.v1_14),
    PILLAGER(2, VersionUtils.Version.v1_14),
    RAVAGER(2, VersionUtils.Version.v1_14),
    TRADER_LLAMA(2, VersionUtils.Version.v1_14),
    WANDERING_TRADER(2, VersionUtils.Version.v1_14),
    FOX(2, VersionUtils.Version.v1_14),

    // v1.15
    BEE(2, VersionUtils.Version.v1_15);

//    Not allowed / Disabled entities

//    EVOKER_FANGS(2, VersionUtils.Version.v1_11),
//    LLAMA_SPIT(2, VersionUtils.Version.v1_11),
//    DROPPED_ITEM(2, VersionUtils.Version.v1_9),
//    EXPERIENCE_ORB(2, VersionUtils.Version.v1_9),
//    LEASH_HITCH(2, VersionUtils.Version.v1_9),
//    PAINTING(2, VersionUtils.Version.v1_9),
//    ARROW(2, VersionUtils.Version.v1_9),
//    SNOWBALL(2, VersionUtils.Version.v1_9),
//    FIREBALL(2, VersionUtils.Version.v1_9),
//    SMALL_FIREBALL(2, VersionUtils.Version.v1_9),
//    ENDER_PEARL(2, VersionUtils.Version.v1_9),
//    ENDER_SIGNAL(2, VersionUtils.Version.v1_9),
//    THROWN_EXP_BOTTLE(2, VersionUtils.Version.v1_9),
//    ITEM_FRAME(2, VersionUtils.Version.v1_9),
//    WITHER_SKULL(2, VersionUtils.Version.v1_9),
//    PRIMED_TNT(2, VersionUtils.Version.v1_9),
//    FALLING_BLOCK(2, VersionUtils.Version.v1_9),
//    FIREWORK(2, VersionUtils.Version.v1_9),
//    ARMOR_STAND(2, VersionUtils.Version.v1_9),
//    MINECART_COMMAND(2, VersionUtils.Version.v1_9),
//    BOAT(2, VersionUtils.Version.v1_9),
//    MINECART(2, VersionUtils.Version.v1_9),
//    MINECART_CHEST(2, VersionUtils.Version.v1_9),
//    MINECART_FURNACE(2, VersionUtils.Version.v1_9),
//    MINECART_TNT(2, VersionUtils.Version.v1_9),
//    MINECART_HOPPER(2, VersionUtils.Version.v1_9),
//    MINECART_MOB_SPAWNER(2, VersionUtils.Version.v1_9),
//    ENDER_CRYSTAL(2, VersionUtils.Version.v1_9),
//    SPLASH_POTION(2, VersionUtils.Version.v1_9),
//    EGG(2, VersionUtils.Version.v1_9),
//    FISHING_HOOK(2, VersionUtils.Version.v1_9),
//    LIGHTNING(2, VersionUtils.Version.v1_9),
//    WEATHER(2, VersionUtils.Version.v1_9),
//    COMPLEX_PART(2, VersionUtils.Version.v1_9),
//    UNKNOWN(2, VersionUtils.Version.v1_9),
//    TIPPED_ARROW(2, VersionUtils.Version.v1_9),
//    SPECTRAL_ARROW(2, VersionUtils.Version.v1_9),
//    SHULKER_BULLET(2, VersionUtils.Version.v1_9),
//    DRAGON_FIREBALL(2, VersionUtils.Version.v1_9),
//    LINGERING_POTION(2, VersionUtils.Version.v1_9),
//    AREA_EFFECT_CLOUD(2, VersionUtils.Version.v1_9);

    double height;
    VersionUtils.Version minimumVersion;

    EntityTypes(double height, VersionUtils.Version minimumVersion)
    {
        this.height = height;
        this.minimumVersion = minimumVersion;
    }

    public static EntityTypes getEnum(String name)
    {
        try
        {
            EntityTypes ent = valueOf(name.toUpperCase());
            if(!ent.minimumVersion.isServerVersionOrLater())
            {
                throw new IllegalArgumentException(name + " is not available in this version");
            }
            return ent;
        }
        catch (Exception exc)
        {
            throw new IllegalArgumentException(name + " is a nonexistent entity type");
        }
    }

    public static ArrayList<EntityTypes> enumValues()
    {
        ArrayList<EntityTypes> entityTypesList = new ArrayList<>();
        for(EntityTypes type : values())
        {
            if(type.minimumVersion.isServerVersionOrLater())
            {
                entityTypesList.add(type);
            }
        }

        return entityTypesList;
    }

    public EntityType getEt()
    {
        return EntityType.valueOf(name());
    }

    public double getHeight()
    {
        return height;
    }

    public void setHeight(double height)
    {
        this.height = height;
    }
}
