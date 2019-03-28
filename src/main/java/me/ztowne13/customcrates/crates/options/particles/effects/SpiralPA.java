package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 6/25/16.
 */
public class SpiralPA extends ParticleAnimationEffect
{
    int updatesPerSec = 20;

    double toChangeHeight = 0, currentYOffset = 0;

    public SpiralPA(CustomCrates cc, ParticleData particleData)
    {
        super(cc, particleData);
        this.updatesPerSec = (int) particleData.getSpeed();
    }

    @Override
    public void update()
    {
        toDisplay.clear();

        totalTick += updatesPerSec;
        tick += updatesPerSec;

        double radius = particleData.getOffX();
        double yOffset = particleData.getOffY();
        double height = particleData.getOffZ();

        if (toChangeHeight == 0 && height != 0)
        {
            toChangeHeight = height / 360;
        }

        for (int i = tick; i < tick + updatesPerSec; i++)
        {
            if (i % (2 + particleData.getAmount()) == 0)
            {
                currentYOffset += toChangeHeight;
                double toX = Math.sin(Math.toRadians(i)) * radius;
                double toY = Math.cos(Math.toRadians(i)) * radius;

                Location newL = new Location(null, toX, currentYOffset + yOffset, toY);
                toDisplay.add(newL);

                if (i > 360)
                {
                    i = 1;
                    tick = 0;
                }

                if ((currentYOffset > height && toChangeHeight > 0) || (currentYOffset < 0 && toChangeHeight < 0))
                {
                    toChangeHeight = -toChangeHeight;
                }
            }
        }
    }
}
