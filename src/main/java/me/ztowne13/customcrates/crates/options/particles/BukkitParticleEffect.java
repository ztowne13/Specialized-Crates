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

    public BukkitParticleEffect(String particleName, boolean hasAnimation)
    {
        this(Particle.valueOf(particleName.toUpperCase()), hasAnimation);
    }

    public BukkitParticleEffect(Particle particle, boolean hasAnimation)
    {
        super(hasAnimation);
        this.particle = particle;
    }

    @Override
    public void display(Location l)
    {
        if (false /*isHasColor()*/)
        {
            //l.getWorld().spawnParticle(particle, l, 1, isHasAnimation() ? 0 : getOffX(), isHasAnimation() ? 0 : getOffY(), isHasAnimation() ? 0 : getOffZ(), getSpeed(), red, green, blue);
        }
        else
        {
            if (isHasAnimation())
            {
                l.getWorld().spawnParticle(particle, LocationUtils.getLocationCentered(l), 1, 0, 0, 0, 0);
            }
            else
            {
                l.getWorld().spawnParticle(particle, LocationUtils.getLocationCentered(l), getAmount(),
                        isHasAnimation() ? 0 : getOffX(), isHasAnimation() ? 0 : getOffY(), isHasAnimation() ? 0 : getOffZ(),
                        getSpeed());
            }
        }
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
}
