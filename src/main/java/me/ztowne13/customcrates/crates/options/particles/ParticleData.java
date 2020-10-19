package me.ztowne13.customcrates.crates.options.particles;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.particles.effects.PEAnimationType;
import me.ztowne13.customcrates.crates.options.particles.effects.ParticleAnimationEffect;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class ParticleData {
    protected final SpecializedCrates instance;

    private String name;
    private float rangeX;
    private float rangeY;
    private float rangeZ;
    private float centerX;
    private float centerY;
    private float centerZ;
    private float speed;
    private boolean hasAnimation;
    private boolean hasColor;
    private int amount;
    private int colorRed;
    private int colorGreen;
    private int colorBlue;
    private float size;
    private boolean colorEnabled;

    private Particle.DustOptions dustOptions;

    private ParticleAnimationEffect particleAnimationEffect;

    public ParticleData(SpecializedCrates instance, String name, boolean hasAnimation) {
        this.instance = instance;
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

    public float getRangeX() {
        return rangeX;
    }

    public void setRangeX(float rangeX) {
        this.rangeX = rangeX;
    }

    public void save(FileHandler fileHandler, String path) {
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

        if (getParticleAnimationEffect() != null)
            fileHandler.get().set(path + "." + getName() + ".animation",
                    PEAnimationType.getFromParticleAnimationEffect(getParticleAnimationEffect()).name());
        else
            fileHandler.get().set(path + "." + getName() + ".animation", "NONE");

        fc.set(path + "." + getName() + ".color.enabled", isColorEnabled());
        fc.set(path + "." + getName() + ".color.red", getColorRed());
        fc.set(path + "." + getName() + ".color.green", getColorGreen());
        fc.set(path + "." + getName() + ".color.blue", getColorBlue());
        fc.set(path + "." + getName() + ".redstone-size", getSize() == 0 ? null : getSize());
    }

    public float getRangeY() {
        return rangeY;
    }

    public void setRangeY(float rangeY) {
        this.rangeY = rangeY;
    }

    public float getRangeZ() {
        return rangeZ;
    }

    public void setRangeZ(float rangeZ) {
        this.rangeZ = rangeZ;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public boolean isHasAnimation() {
        return hasAnimation;
    }

    public void setHasAnimation(boolean hasAnimation) {
        this.hasAnimation = hasAnimation;
    }

    public ParticleAnimationEffect getParticleAnimationEffect() {
        return particleAnimationEffect;
    }

    public void setParticleAnimationEffect(ParticleAnimationEffect particleAnimationEffect) {
        this.particleAnimationEffect = particleAnimationEffect;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCenterX() {
        return centerX;
    }

    public void setCenterX(float centerX) {
        this.centerX = centerX;
    }

    public float getCenterY() {
        return centerY;
    }

    public void setCenterY(float centerY) {
        this.centerY = centerY;
    }

    public float getCenterZ() {
        return centerZ;
    }

    public void setCenterZ(float centerZ) {
        this.centerZ = centerZ;
    }

    public Particle.DustOptions getDustOptions() {
        return dustOptions;
    }

    public void setDustOptions(Particle.DustOptions dustOptions) {
        this.dustOptions = dustOptions;
    }

    public int getColorRed() {
        return colorRed;
    }

    public void setColorRed(int colorRed) {
        this.colorRed = colorRed;
    }

    public int getColorGreen() {
        return colorGreen;
    }

    public void setColorGreen(int colorGreen) {
        this.colorGreen = colorGreen;
    }

    public int getColorBlue() {
        return colorBlue;
    }

    public void setColorBlue(int colorBlue) {
        this.colorBlue = colorBlue;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public boolean isHasColor() {
        return hasColor;
    }

    public void setHasColor(boolean hasColor) {
        this.hasColor = hasColor;
    }

    public boolean isColorEnabled() {
        return colorEnabled;
    }

    public void setColorEnabled(boolean colorEnabled) {
        this.colorEnabled = colorEnabled;
    }
}
