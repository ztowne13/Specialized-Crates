package me.ztowne13.customcrates.crates.options.particles.effects;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;

/**
 * Created by ztowne13 on 6/25/16.
 */
public enum PEAnimationType {
    CIRCLE(CirclePA.class),

    SPIRAL(SpiralPA.class),

    DOUBLE_SPIRAL(DoubleSpiralPA.class),

    GROWING_SPIRAL(GrowingSpiralPA.class),

    TILTED_RINGS(TiltedRingsPA.class),

    OFFSET_TILTED_RINGS(OffsetTiltedRingsPA.class),

    NONE(null);


    private final Class<? extends ParticleAnimationEffect> particleAnimationEffect;

    PEAnimationType(Class<? extends ParticleAnimationEffect> particleAnimationEffect) {
        this.particleAnimationEffect = particleAnimationEffect;
    }

    public static PEAnimationType getFromParticleAnimationEffect(ParticleAnimationEffect particleAnimationEffect) {
        for (PEAnimationType peAnimationType : values()) {
            if (particleAnimationEffect.getClass() == peAnimationType.particleAnimationEffect) {
                return peAnimationType;
            }
        }
        return null;
    }

    public ParticleAnimationEffect getAnimationEffectInstance(SpecializedCrates cc, ParticleData particleData) {
        switch (this) {
            case CIRCLE:
                return new CirclePA(cc, particleData);
            case SPIRAL:
                return new SpiralPA(cc, particleData);
            case DOUBLE_SPIRAL:
                return new DoubleSpiralPA(cc, particleData);
            case GROWING_SPIRAL:
                return new GrowingSpiralPA(cc, particleData);
            case TILTED_RINGS:
                return new TiltedRingsPA(cc, particleData);
            case OFFSET_TILTED_RINGS:
                return new OffsetTiltedRingsPA(cc, particleData);
            default:
                return null;
        }
    }
}
