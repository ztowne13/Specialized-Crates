package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.Particle;

/**
 * Created by ztowne13 on 6/24/16.
 */
public class BukkitParticleEffect extends ParticleData
{
    Particle particle;

    public BukkitParticleEffect(String particleName, String name, boolean hasAnimation)
    {
        this(Particle.valueOf(particleName.toUpperCase()), name, hasAnimation);
    }

    public BukkitParticleEffect(Particle particle, String name, boolean hasAnimation)
    {
        super(name, hasAnimation);
        this.particle = particle;
    }

    @Override
    public void display(Location l)
    {
        if (isHasAnimation())
            l.getWorld().spawnParticle(particle, LocationUtils.getLocationCentered(l), 1, 0, 0, 0, 0, getValidData());
        else
            l.getWorld().spawnParticle(particle,
                    LocationUtils.getLocationCentered(l).add(getCenterX(), getCenterY(), getCenterZ()), getAmount(),
                    getRangeX(), getRangeY(), getRangeZ(), getSpeed(), getValidData());
    }

    @Override
    public boolean setParticle(String particleName)
    {
        try
        {
            particle = Particle.valueOf(particleName.toUpperCase());
            return true;
        }
        catch (Exception exc)
        {
            return false;
        }
    }

    @Override
    public String getParticleName()
    {
        return particle.name();
    }

    public Object getValidData()
    {
        if (particle.equals(Particle.REDSTONE))
        {
            return getDustOptions();
        }

        return null;
    }
}
