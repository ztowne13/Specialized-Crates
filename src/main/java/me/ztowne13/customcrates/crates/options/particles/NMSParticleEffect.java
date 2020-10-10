package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by ztowne13 on 6/24/16.
 */
public class NMSParticleEffect extends ParticleData {
    ParticleEffect particleEffect;

    public NMSParticleEffect(SpecializedCrates sc, ParticleEffect particleEffect, String name, boolean hasAnimation) {
        super(sc, name, hasAnimation);
        this.particleEffect = particleEffect;
    }

    @Override
    public void display(Location l) {
        try {
            Location centered;
            int amnt = getAmount();
            float offX, offY, offZ, speed;

            if (isHasAnimation()) {
                centered = LocationUtils.getLocationCentered(l);
                offX = offY = offZ = 0;
                speed = 0;
            } else {
                centered = LocationUtils.getLocationCentered(l).add(getCenterX(), getCenterY(), getCenterZ());
                offX = getRangeX();
                offY = getRangeY();
                offZ = getRangeZ();
                speed = getSpeed();
            }

            for (Player p : Bukkit.getOnlinePlayers()) {
                particleEffect.sendToPlayer(sc, p, centered, offX, offY, offZ, speed, amnt);
            }
        } catch (Exception e) {
            ChatUtils.log(new String[]{"Error loading particle: " + particleEffect.name()});
        }
    }

    @Override
    public boolean setParticle(String particleName) {
        try {
            particleEffect = ParticleEffect.valueOf(particleName);
            return true;
        } catch (Exception exc) {
            return false;
        }
    }

    @Override
    public String getParticleName() {
        return particleEffect.name();
    }
}
