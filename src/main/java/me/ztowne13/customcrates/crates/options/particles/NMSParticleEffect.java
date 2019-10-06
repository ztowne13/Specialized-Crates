package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.utils.ChatUtils;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 6/24/16.
 */
public class NMSParticleEffect extends ParticleData
{
    ParticleEffect particleEffect;

    public NMSParticleEffect(ParticleEffect particleEffect, String name, boolean hasAnimation)
    {
        super(name, hasAnimation);
        this.particleEffect = particleEffect;
    }

    @Override
    public void display(Location l)
    {
        try
        {
//            Iterator iterator = Bukkit.getOnlinePlayers().iterator();
//            while (iterator.hasNext())
//            {
//                Player p = (Player) iterator.next();
//                if (isHasAnimation())
//                {
//                    particleEffect.sendToPlayer(p, LocationUtils.getLocationCentered(l), 0, 0, 0, 0, 1);
//                }
//                else
//                {
//                    particleEffect.sendToPlayer(p, LocationUtils.getLocationCentered(l), getRangeX(), getRangeY(), getRangeZ(),
//                            getSpeed(), getAmount());
//                }
//            }
        }
        catch (Exception e)
        {
            ChatUtils.log(new String[]{"Error loading particle: " + particleEffect.name()});
        }
    }

    @Override
    public boolean setParticle(String particleName)
    {
        try
        {
            particleEffect = ParticleEffect.valueOf(particleName);
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
        return particleEffect.name();
    }
}
