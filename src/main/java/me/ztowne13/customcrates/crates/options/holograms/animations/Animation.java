package me.ztowne13.customcrates.crates.options.holograms.animations;

import me.ztowne13.customcrates.SpecializedCrates;

/**
 * Class that is the superclass to all non-crate open animations eg. holograms / particles
 */
public abstract class Animation
{
    protected SpecializedCrates cc;

    public Animation(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public abstract void update(boolean force);

    public abstract void update();

    public abstract void stop();

    public abstract void tick();
}
