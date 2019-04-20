package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 6/26/16.
 */
public class TiltedRingsPA extends ParticleAnimationEffect
{
    int updatesPerSec = 20;

    public TiltedRingsPA(CustomCrates cc, ParticleData particleData)
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

        double radius = particleData.getRangeX();
        double yOffset = particleData.getRangeY();
        double rotation = particleData.getRangeZ();

        double rotateInRadians = Math.toRadians(rotation);

        for (int i = tick; i < tick + updatesPerSec; i++)
        {
            int iTemp = i;
            if (i % (2 + particleData.getAmount()) == 0)
            {
                i = iTemp;
                double x = Math.sin(Math.toRadians(i)) * radius;
                double y = Math.cos(Math.toRadians(i)) * radius;

                double toX = (x * Math.cos(rotateInRadians)) - (y * Math.sin(rotateInRadians));
                double toY = (x * Math.sin(rotateInRadians)) + (y * Math.cos(rotateInRadians));

                double tilt = Math.cos(Math.toRadians(i)) * radius;

                Location newL = new Location(null, toX, yOffset + tilt, -toY);
                toDisplay.add(newL);

                i = 180 - iTemp;

                x = Math.sin(Math.toRadians(i)) * radius;
                y = Math.cos(Math.toRadians(i)) * radius;

                toX = (x * Math.cos(rotateInRadians)) - (y * Math.sin(rotateInRadians));
                toY = (x * Math.sin(rotateInRadians)) + (y * Math.cos(rotateInRadians));

                tilt = Math.cos(Math.toRadians(i)) * radius;

                newL = new Location(null, toX, yOffset - tilt, -toY);
                toDisplay.add(newL);

                if (i > 360)
                {
                    i = 1;
                    tick = 0;
                }

            }

            i = iTemp;
        }
    }
}
