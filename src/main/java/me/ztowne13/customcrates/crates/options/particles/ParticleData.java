package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.crates.options.particles.effects.ParticleAnimationEffect;
import org.bukkit.Location;

public abstract class ParticleData
{
    String name;
    float rangeX, rangeY, rangeZ, centerX, centerY, centerZ, speed;
    boolean hasAnimation, hasColor;
    int amount, red, green, blue;

    ParticleAnimationEffect particleAnimationEffect;

    public ParticleData(String name, boolean hasAnimation)
    {
        this.name = name;
        this.hasAnimation = hasAnimation;

        this.rangeX = this.rangeY = this.rangeZ = 1;
        this.centerX = this.centerY = this.centerZ = 0;
        this.amount = 1;
        this.speed = isHasAnimation() ? 20 : 0;
    }

    public abstract void display(Location l);

    public abstract String getParticleName();

    public abstract boolean setParticle(String particleName);

    public float getRangeX()
    {
        return rangeX;
    }

    public ParticleData setRangeX(float rangeX)
    {
        this.rangeX = rangeX;
        return this;
    }

    public float getRangeY()
    {
        return rangeY;
    }

    public ParticleData setRangeY(float rangeY)
    {
        this.rangeY = rangeY;
        return this;
    }

    public float getRangeZ()
    {
        return rangeZ;
    }

    public ParticleData setRangeZ(float rangeZ)
    {
        this.rangeZ = rangeZ;
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

    public void setName(String name)
    {
        this.name = name;
    }

    public float getCenterX()
    {
        return centerX;
    }

    public void setCenterX(float centerX)
    {
        this.centerX = centerX;
    }

    public float getCenterY()
    {
        return centerY;
    }

    public void setCenterY(float centerY)
    {
        this.centerY = centerY;
    }

    public float getCenterZ()
    {
        return centerZ;
    }

    public void setCenterZ(float centerZ)
    {
        this.centerZ = centerZ;
    }
}
