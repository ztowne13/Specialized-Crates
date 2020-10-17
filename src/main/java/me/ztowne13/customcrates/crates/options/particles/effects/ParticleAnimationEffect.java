package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

import java.util.ArrayList;


/**
 * Created by ztowne13 on 6/25/16.
 */
public abstract class ParticleAnimationEffect {
    protected final SpecializedCrates instance;
    protected final ParticleData particleData;

    protected final ArrayList<Location> toDisplay = new ArrayList<>();

    protected int totalTick = 0;
    protected int tick = 0;

    public ParticleAnimationEffect(SpecializedCrates instance, ParticleData particleData) {
        this.instance = instance;
        this.particleData = particleData;
    }

    public abstract void update();

    public void display(Location location) {
        for (Location toDisplayLocation : toDisplay) {
            toDisplayLocation.setWorld(location.getWorld());

            Location modifiedL = location.clone().add(toDisplayLocation);
            modifiedL.setY(modifiedL.getY() - 1);

            particleData.display(modifiedL);
        }
    }
}
