package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.crates.options.particles.effects.ParticleAnimationEffect;
import org.bukkit.Location;

public abstract class ParticleData
{
    String name;
    float offX, offY, offZ, speed;
    boolean hasAnimation, hasColor;
    int amount, red, green, blue;

    ParticleAnimationEffect particleAnimationEffect;

    public ParticleData(String name, boolean hasAnimation)
    {
        this.name = name;
        this.hasAnimation = hasAnimation;
    }

    public abstract void display(Location l);

    public abstract String getParticleName();

    public abstract boolean setParticle(String particleName);

    public float getOffX()
    {
        return offX;
    }

    public ParticleData setOffX(float offX)
    {
        this.offX = offX;
        return this;
    }

    public float getOffY()
    {
        return offY;
    }

    public ParticleData setOffY(float offY)
    {
        this.offY = offY;
        return this;
    }

    public float getOffZ()
    {
        return offZ;
    }

    public ParticleData setOffZ(float offZ)
    {
        this.offZ = offZ;
        return this;
    }

    public float getSpeed()
    {
        return speed;
    }

    public ParticleData setSpeed(float speed)
    {
        this.speed = speed;
        return this;
    }

    public int getAmount()
    {
        return amount;
    }

    public ParticleData setAmount(int amount)
    {
        this.amount = amount;
        return this;
    }

    public boolean isHasAnimation()
    {
        return hasAnimation;
    }

    public void setHasAnimation(boolean hasAnimation)
    {
        this.hasAnimation = hasAnimation;
    }

    public ParticleAnimationEffect getParticleAnimationEffect()
    {
        return particleAnimationEffect;
    }

    public void setParticleAnimationEffect(ParticleAnimationEffect particleAnimationEffect)
    {
        this.particleAnimationEffect = particleAnimationEffect;
    }

    public int getRed()
    {
        return red;
    }

    public void setRed(int red)
    {
        this.red = red;
    }

    public int getGreen()
    {
        return green;
    }

    public void setGreen(int green)
    {
        this.green = green;
    }

    public int getBlue()
    {
        return blue;
    }

    public void setBlue(int blue)
    {
        this.blue = blue;
    }

    public boolean isHasColor()
    {
        return hasColor;
    }

    public void setHasColor(boolean hasColor)
    {
        this.hasColor = hasColor;
    }

    public String getName()
    {
        return name;
    }
}
