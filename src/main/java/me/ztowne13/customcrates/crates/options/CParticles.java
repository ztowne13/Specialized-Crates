package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.SettingsConverter;
import me.ztowne13.customcrates.crates.options.particles.BukkitParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.NMSParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.ParticleData;
import me.ztowne13.customcrates.crates.options.particles.ParticleEffect;
import me.ztowne13.customcrates.crates.options.particles.effects.PEAnimationType;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.VersionUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CParticles extends CSetting {
    private Map<String, List<ParticleData>> particles = new HashMap<>();

    public CParticles(Crate crates) {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState) {
        if (crateSettingsBuilder.hasV(crateState.name().toLowerCase() + ".particles")) {
            parseAndAddParticles(crateState.name().toUpperCase(),
                    crateState.name().toLowerCase() + ".particles");
        }

        if (crateState.equals(CrateState.OPEN) && crateSettingsBuilder.hasV("open.crate-tiers")) {
            for (String id : getCrate().getSettings().getFc().getConfigurationSection("open.crate-tiers").getKeys(false)) {
                if (crateSettingsBuilder.hasV("open.crate-tiers." + id + ".particles")) {
                    parseAndAddParticles(id, "open.crate-tiers." + id + ".particles");
                }
            }
        }
    }

    public void saveToFile() {
        if (!particles.isEmpty())
            for (Map.Entry<String, List<ParticleData>> entry : particles.entrySet())
                for (ParticleData pd : entry.getValue())
                    pd.save(getFileHandler(), getPath(entry.getKey()));
    }

    public void deleteParticle(String tier, ParticleData particleData) {
        getParticles().get(tier).remove(particleData);
        getFileHandler().get().set(getPath(tier) + "." + particleData.getName(), null);
    }

    public void addParticle(ParticleData particleData, String s) {
        List<ParticleData> plist = getParticles().getOrDefault(s, new ArrayList<>());
        plist.add(particleData);
        StatusLoggerEvent.PARTICLE_ADD_SUCCESS.log(getCrate(), new String[]{particleData.getParticleName()});
        getParticles().put(s, plist);
    }

    public void parseAndAddParticles(String id, String path) {
        SettingsConverter.convertParticles(getFileHandler(), path);

        FileConfiguration fc = getFileHandler().get();

        try {
            for (String parent : getFileHandler().get().getConfigurationSection(path).getValues(false).keySet()) {
                String particleTypeAS = getFileHandler().get().getString(path + "." + parent + ".type");
                String rangeXAS = fc.getString(path + "." + parent + ".range-x");
                String rangeYAS = fc.getString(path + "." + parent + ".range-y");
                String rangeZAS = fc.getString(path + "." + parent + ".range-z");
                String centerXAS = fc.getString(path + "." + parent + ".center-x");
                String centerYAS = fc.getString(path + "." + parent + ".center-y");
                String centerZAS = fc.getString(path + "." + parent + ".center-z");
                String speedAS = fc.getString(path + "." + parent + ".speed");
                String amountAS = fc.getString(path + "." + parent + ".amount");
                String animationAS = fc.getString(path + "." + parent + ".animation");
                String colorRed = fc.getString(path + "." + parent + ".color.red");
                String colorGreen = fc.getString(path + "." + parent + ".color.green");
                String colorBlue = fc.getString(path + "." + parent + ".color.blue");
                String colorEnabled = fc.getString(path + "." + parent + ".color.enabled");
                String redstoneSize = fc.getString(path + "." + parent + ".redstone-size");

                try {
                    ParticleData pd;

                    if (VersionUtils.Version.v1_9.isServerVersionOrEarlier()) {
                        ParticleEffect pe;
                        try {
                            pe = ParticleEffect.valueOf(particleTypeAS);
                        } catch (Exception exc) {
                            StatusLoggerEvent.PARTICLE_INVALID.log(getCrate(), new String[]{parent, particleTypeAS});
                            continue;
                        }
                        pd = new NMSParticleEffect(instance, pe, parent, false);
                    } else {
                        try {
                            pd = new BukkitParticleEffect(instance, particleTypeAS, parent, false);
                        } catch (Exception exc) {
                            StatusLoggerEvent.PARTICLE_INVALID.log(getCrate(), new String[]{parent, particleTypeAS});
                            continue;
                        }
                    }

                    // Load default values - these must be correct for particles to work.

                    pd.setRangeX(Float.parseFloat(rangeXAS));
                    pd.setRangeY(Float.parseFloat(rangeYAS));
                    pd.setRangeZ(Float.parseFloat(rangeZAS));
                    pd.setSpeed(Float.parseFloat(speedAS));
                    pd.setAmount(Integer.parseInt(amountAS));

                    // Loading option values

                    // center x
                    try {
                        pd.setCenterX(Float.parseFloat(centerXAS));
                    } catch (Exception exc) {
                        // IGNORED
                    }

                    // center y
                    try {
                        pd.setCenterY(Float.parseFloat(centerYAS));
                    } catch (Exception exc) {
                        // IGNORED
                    }

                    // center z
                    try {
                        pd.setCenterZ(Float.parseFloat(centerZAS));
                    } catch (Exception exc) {
                        // IGNORED
                    }

                    // colors
                    try {
                        pd.setColorRed(Integer.parseInt(colorRed));
                    } catch (Exception exc) {
                        pd.setColorRed(255);
                    }

                    try {
                        pd.setColorBlue(Integer.parseInt(colorBlue));
                    } catch (Exception exc) {
                        pd.setColorBlue(0);
                    }

                    try {
                        pd.setColorGreen(Integer.parseInt(colorGreen));
                    } catch (Exception exc) {
                        pd.setColorGreen(0);
                    }

                    try {
                        pd.setColorEnabled(Boolean.parseBoolean(colorEnabled));
                    } catch (Exception exc) {
                        pd.setColorEnabled(false);
                    }

                    float size;

                    try {
                        size = Float.parseFloat(redstoneSize);

                        if (size <= 0)
                            size = 1;
                    } catch (Exception exc) {
                        size = 1;
                    }

                    // Redstone info
                    try {
                        if (VersionUtils.Version.v1_13.isServerVersionOrLater() && particleTypeAS.equalsIgnoreCase("REDSTONE")) {
                            pd.setHasColor(true);
                            pd.setColorEnabled(true);

                            pd.setSize(size);

                            Color color = Color.fromBGR(pd.getColorBlue(), pd.getColorGreen(), pd.getColorRed());
                            Particle.DustOptions dustOptions = new Particle.DustOptions(color, size);

                            pd.setDustOptions(dustOptions);
                        }
                    } catch (Exception exc) {
                        exc.printStackTrace();
                    }

                    try {
                        PEAnimationType peAnimationType = PEAnimationType.valueOf(animationAS);
                        if (!peAnimationType.equals(PEAnimationType.NONE)) {
                            // This is to default the particle animation speed and particle count so it doesn't appear broken
                            if (pd.getSpeed() < 3) {
                                pd.setSpeed(20);
                            }
                            if (pd.getAmount() == 0) {
                                pd.setAmount(1);
                            }

                            pd.setParticleAnimationEffect(peAnimationType.getAnimationEffectInstance(instance, pd));
                            pd.setHasAnimation(true);
                        }
                    } catch (Exception exc) {
                        StatusLoggerEvent.PARTICLE_ANIMATION_INVALID.log(getCrate(), new String[]{animationAS});
                    }

                    addParticle(pd, id);
                } catch (Exception exc) {
                    exc.printStackTrace();
                    StatusLoggerEvent.PARTICLE_STRING_INVALID.log(getCrate(), new String[]{parent});
                }
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void runAll(Location location, CrateState crateState, List<Reward> rewards) {
        List<ParticleData> alreadyUpdatedAnimations = instance.getAlreadyUpdated();
        for (String id : getParticles().keySet()) {
            if (crateState.equals(CrateState.PLAY)) {
                if (crateState.name().toUpperCase().equalsIgnoreCase(id)) {
                    for (ParticleData pd : getParticles().get(id)) {
                        if (pd.isHasAnimation()) {
                            if (!alreadyUpdatedAnimations.contains(pd)) {
                                pd.getParticleAnimationEffect().update();
                                alreadyUpdatedAnimations.add(pd);
                            }
                            pd.getParticleAnimationEffect().display(location);
                        } else {
                            pd.display(location);
                        }
                    }
                }
                continue;
            }

            if ((id.equalsIgnoreCase(crateState.name().toUpperCase()) && (!getSettings().isTiersOverrideDefaults() || rewards.isEmpty() ||
                    !getParticles().containsKey(rewards.get(0).getRarity().toUpperCase()))) ||
                    (!rewards.isEmpty() && rewards.get(0).getRarity().equalsIgnoreCase(id))) {
                for (ParticleData pd : getParticles().get(id)) {
                    if (pd.isHasAnimation()) {
                        if (!alreadyUpdatedAnimations.contains(pd)) {
                            pd.getParticleAnimationEffect().update();
                            alreadyUpdatedAnimations.add(pd);
                        }
                        pd.getParticleAnimationEffect().display(location);
                    } else {
                        pd.display(location);
                    }
                }
            }
        }
    }

    public String getPath(String tier) {
        return (tier.equalsIgnoreCase("PLAY") ? "play." : "open.") +
                (tier.equalsIgnoreCase("open") || tier.equalsIgnoreCase("play") ? "" : "crate-tiers." + tier + ".") +
                "particles";
    }

    public Map<String, List<ParticleData>> getParticles() {
        return particles;
    }

    public void setParticles(Map<String, List<ParticleData>> particles) {
        this.particles = particles;
    }

}
