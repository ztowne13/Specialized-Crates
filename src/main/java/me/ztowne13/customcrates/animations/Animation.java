package me.ztowne13.customcrates.animations;

import me.ztowne13.customcrates.CustomCrates;

/**
 *	Class that is the superclass to all non-crate open animations eg. holograms / particles
 */
public abstract class Animation 
{
	protected CustomCrates cc;
	
	public Animation(CustomCrates cc)
	{
		this.cc = cc;
	}

	public abstract void update(boolean force);

	public abstract void update();
	
	public abstract void stop();
	
	public abstract void tick();
}
