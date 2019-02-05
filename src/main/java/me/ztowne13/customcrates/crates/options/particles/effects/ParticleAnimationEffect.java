package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

import java.util.ArrayList;


/**
 * Created by ztowne13 on 6/25/16.
 */
public abstract class ParticleAnimationEffect
{
	CustomCrates cc;
	ParticleData particleData;

	ArrayList<Location> toDisplay = new ArrayList<Location>();

	int totalTick = 0;
	int tick = 0;

	public ParticleAnimationEffect(CustomCrates cc, ParticleData particleData)
	{
		this.cc = cc;
		this.particleData = particleData;
	}

	public abstract void update();

	public void display(Location l)
	{
		for(Location toDisplayLocation : toDisplay)
		{
			toDisplayLocation.setWorld(l.getWorld());

			Location modifiedL = l.clone().add(toDisplayLocation);
			modifiedL.setY(modifiedL.getY() - 1);

			particleData.display(modifiedL);
		}
	}
}
