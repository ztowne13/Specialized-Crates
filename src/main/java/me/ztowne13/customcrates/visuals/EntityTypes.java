package me.ztowne13.customcrates.visuals;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;

/**
 * Created by ztowne13 on 2/27/16.
 */
public enum EntityTypes
{
	PLAYER(EntityType.PLAYER, 2),

	BAT(EntityType.BAT, .5),

	CAVE_SPIDER(EntityType.CAVE_SPIDER, .5),

	CHICKEN(EntityType.CHICKEN, 1),

	COW(EntityType.COW, 1),

	CREEPER(EntityType.CREEPER, 2),

	ENDERMAN(EntityType.ENDERMAN, 2.9),

	ENDERMITE(EntityType.ENDERMITE, .5),

	HORSE(EntityType.HORSE, 1.2),

	IRON_GOLEM(EntityType.IRON_GOLEM, 2.7),

	MAGMA_CUBE(EntityType.MAGMA_CUBE, 1.7),

	MUSHROOM_COW(EntityType.MUSHROOM_COW, 1.4),

	OCELOT(EntityType.OCELOT, .5),

	PIG(EntityType.PIG, .8),

	PIG_ZOMBIE(EntityType.PIG_ZOMBIE, 2),

	RABBIT(EntityType.RABBIT, .5),

	SHEEP(EntityType.SHEEP, 1.2),

	SILVERFISH(EntityType.SILVERFISH, .5),

	SKELETON(EntityType.SKELETON, 2),

	SLIME(EntityType.SLIME, 1.7),

	SNOWMAN(EntityType.SNOWMAN, 2),

	VILLAGER(EntityType.VILLAGER, 2),

	WITCH(EntityType.WITCH, 2),

	WITHER(EntityType.WITHER, 3.5),

	WOLF(EntityType.WOLF, .8),

	ZOMBIE(EntityType.ZOMBIE, 2);


	EntityType et;
	double height;

	EntityTypes(EntityType et, double height)
	{
		this.et = et;
		this.height = height;
	}

	public EntityType getEt()
	{
		return et;
	}

	public void setEt(EntityType et)
	{
		this.et = et;
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
