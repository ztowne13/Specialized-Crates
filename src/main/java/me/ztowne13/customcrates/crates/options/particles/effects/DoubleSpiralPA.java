package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 6/25/16.
 */
public class DoubleSpiralPA extends ParticleAnimationEffect {
    private final int updatesPerSec;

    private int extraTick = 0;
    private double toChangeHeight = 0;
    private double currentYOffset = 0;

    public DoubleSpiralPA(SpecializedCrates instance, ParticleData particleData) {
        super(instance, particleData);
        this.updatesPerSec = (int) particleData.getSpeed();
    }

    @Override
    public void update() {

        toDisplay.clear();

        totalTick += updatesPerSec;
        tick += updatesPerSec;

        double radius = particleData.getRangeX();
        double yOffset = particleData.getRangeY();
        double height = particleData.getRangeZ();

        int degrees = 360; // MODIFY THIS
        if (toChangeHeight == 0 && height != 0) {
            toChangeHeight = height / degrees;
        }

        int i = tick;
        while (i < tick + updatesPerSec) {
            currentYOffset += toChangeHeight;
            if (i % (2 + particleData.getAmount()) == 0) {
                double toX = Math.sin(Math.toRadians(i)) * radius;
                double toY = Math.cos(Math.toRadians(i)) * radius;

                Location newL = new Location(null, toX, currentYOffset + yOffset, toY);
                toDisplay.add(newL);
                Location newL2 = new Location(null, -toX, currentYOffset + yOffset, -toY);
                toDisplay.add(newL2);

                if (i > degrees + extraTick) {
                    extraTick = 0; //extraTick == 180 ? 0 : 180;
                    i = extraTick + 1;
                    tick = extraTick;
                }

                if ((currentYOffset > height && toChangeHeight > 0) || (currentYOffset < 0 && toChangeHeight < 0)) {
                    toChangeHeight = -toChangeHeight;
                }
            }
            i++;
        }
    }
}
