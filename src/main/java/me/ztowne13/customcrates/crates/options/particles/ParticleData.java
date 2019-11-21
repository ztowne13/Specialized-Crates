package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.crates.options.particles.effects.PEAnimationType;
import me.ztowne13.customcrates.crates.options.particles.effects.ParticleAnimationEffect;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class ParticleData
{
    String name;
    float rangeX, rangeY, rangeZ;
    float centerX, centerY, centerZ;
    float speed;
    boolean hasAnimation;
    int amount;

    Particle.DustOptions dustOptions;

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

    public void save(FileHandler fileHandler, String path)
    {
        FileConfiguration fc = fileHandler.get();
        fc.set(path + "." + getName() + ".type", getParticleName());
        fc.set(path + "." + getName() + ".range-x", getRangeX());
        fc.set(path + "." + getName() + ".range-y", getRangeY());
        fc.set(path + "." + getName() + ".range-z", getRangeZ());
        fc.set(path + "." + getName() + ".center-x", getCenterX());
        fc.set(path + "." + getName() + ".center-y", getCenterY());
        fc.set(path + "." + getName() + ".center-z", getCenterZ());
        fc.set(path + "." + getName() + ".speed", getSpeed());
        fc.set(path + "." + getName() + ".amount", getAmount());

        if (!(getParticleAnimationEffect() == null))
            fileHandler.get().set(path + "." + getName() + ".animation",
                    PEAnimationType.getFromParticleAnimationEffect(getParticleAnimationEffect()).name());
        else
            fileHandler.get().set(path + "." + getName() + ".animation", "NONE");

        if(dustOptions != null)
        {
            fc.set(path + "." + getName() + ".redstone.red", dustOptions.getColor().getRed());
            fc.set(path + "." + getName() + ".redstone.green", dustOptions.getColor().getGreen());
            fc.set(path + "." + getName() + ".redstone.blue", dustOptions.getColor().getBlue());
            fc.set(path + "." + getName() + ".redstone.size", dustOptions.getSize());
        }
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

    public Particle.DustOptions getDustOptions()
    {
        return dustOptions;
    }

    public void setDustOptions(Particle.DustOptions dustOptions)
    {
        this.dustOptions = dustOptions;
    }
}
