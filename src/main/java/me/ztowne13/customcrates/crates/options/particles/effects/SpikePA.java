package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;

import java.util.Random;

/**
 * Created by ztowne13 on 6/26/16.
 */
public class SpikePA extends ParticleAnimationEffect
{
    int updatesPerSec = 20;

    Random r;

    double currentLength = -1, currentXAngle, currentYAngle;

    public SpikePA(CustomCrates cc, ParticleData particleData)
    {
        super(cc, particleData);
        r = new Random();
        this.updatesPerSec = (int) particleData.getSpeed();
    }

    @Override
    public void update()
    {
        totalTick += updatesPerSec;
        tick += updatesPerSec;

        double radius = particleData.getOffX();
        double yOffset = particleData.getOffY();
        double height = particleData.getOffZ();

        if (currentLength == -1)
        {
            currentLength = height + r.nextInt((int) Math.ceil(radius)) + 1 + r.nextDouble();
        }
    }
}
