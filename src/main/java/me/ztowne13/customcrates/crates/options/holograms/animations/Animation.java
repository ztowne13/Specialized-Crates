package me.ztowne13.customcrates.crates.options.holograms.animations;

import me.ztowne13.customcrates.SpecializedCrates;

/**
 * Class that is the superclass to all non-crate open animations eg. holograms / particles
 */
public abstract class Animation {
    protected final SpecializedCrates instance;

    public Animation(SpecializedCrates instance) {
        this.instance = instance;
    }

    public abstract void update(boolean force);

    public abstract void update();

    public abstract void stop();

    public abstract void tick();
}
