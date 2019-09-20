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
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.NMSUtils;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class CParticles extends CSetting
{
    private HashMap<String, ArrayList<ParticleData>> particles = new HashMap<String, ArrayList<ParticleData>>();

    public CParticles(Crate crates)
    {
        super(crates, crates.getCc());
    }

    @Override
    public void loadFor(CrateSettingsBuilder csb, CrateState cstate)
    {
        if (csb.hasV(cstate.name().toLowerCase() + ".particles"))
        {
            parseAndAddParticles(cstate.name().toUpperCase(),
                    cstate.name().toLowerCase() + ".particles");
        }

        if (cstate.equals(CrateState.OPEN))
        {
            if (csb.hasV("open.crate-tiers"))
            {
                for (String id : getCrates().getCs().getFc().getConfigurationSection("open.crate-tiers").getKeys(false))
                {
                    if (csb.hasV("open.crate-tiers." + id + ".particles"))
                    {
                        parseAndAddParticles(id, "open.crate-tiers." + id + ".particles");
                    }
                }
            }
        }
    }

    public void saveToFile()
    {
        if (!particles.isEmpty())
            for (String tier : particles.keySet())
                for (ParticleData pd : particles.get(tier))
                    pd.save(getFu(), getPath(tier));
    }

    public void deleteParticle(String tier, ParticleData pd)
    {
        getParticles().get(tier).remove(pd);
        getFu().get().set(getPath(tier) + "." + pd.getName(), null);
    }

    public void addParticle(ParticleData pd, String s)
    {
        ArrayList<ParticleData> plist =
                getParticles().containsKey(s) ? getParticles().get(s) : new ArrayList<ParticleData>();
        plist.add(pd);
        StatusLoggerEvent.PARTICLE_ADD_SUCCESS.log(getCrates(), new String[]{pd.getParticleName()});
        getParticles().put(s, plist);
    }

    public void parseAndAddParticles(String id, String path)
    {
        SettingsConverter.convertParticles(getFu(), path);

        try
        {
            for (String parent : getFu().get().getConfigurationSection(path).getValues(false).keySet())
            {
                String particleTypeAS = getFu().get().getString(path + "." + parent + ".type");
                String rangeXAS = getFu().get().getString(path + "." + parent + ".range-x");
                String rangeYAS = getFu().get().getString(path + "." + parent + ".range-y");
                String rangeZAS = getFu().get().getString(path + "." + parent + ".range-z");
                String centerXAS = getFu().get().getString(path + "." + parent + ".center-x");
                String centerYAS = getFu().get().getString(path + "." + parent + ".center-y");
                String centerZAS = getFu().get().getString(path + "." + parent + ".center-z");
                String speedAS = getFu().get().getString(path + "." + parent + ".speed");
                String amountAS = getFu().get().getString(path + "." + parent + ".amount");
                String animationAS = getFu().get().getString(path + "." + parent + ".animation");


                try
                {
                    ParticleData pd;

                    if (!NMSUtils.Version.v1_10.isServerVersionOrLater())
                    {
                        ParticleEffect pe = null;
                        try
                        {
                            pe = ParticleEffect.valueOf(particleTypeAS);
                        }
                        catch (Exception exc)
                        {
                            StatusLoggerEvent.PARTICLE_INVALID.log(getCrates(), new String[]{parent, particleTypeAS});
                            continue;
                        }
                        pd = new NMSParticleEffect(pe, parent, false);
                    }
                    else
                    {
                        try
                        {
                            pd = new BukkitParticleEffect(particleTypeAS, parent, false);
                        }
                        catch (Exception exc)
                        {
                            StatusLoggerEvent.PARTICLE_INVALID.log(getCrates(), new String[]{parent, particleTypeAS});
                            continue;
                        }
                    }

                    // Load default values - these must be correct for particles to work.

                    pd.setRangeX(Float.valueOf(rangeXAS));
                    pd.setRangeY(Float.valueOf(rangeYAS));
                    pd.setRangeZ(Float.valueOf(rangeZAS));
                    pd.setSpeed(Float.valueOf(speedAS));
                    pd.setAmount(Integer.valueOf(amountAS));

                    // Loading option values

                    // center x
                    try
                    {
                        pd.setCenterX(Float.valueOf(centerXAS));
                    }
                    catch (Exception exc)
                    {

                    }

                    // center y
                    try
                    {
                        pd.setCenterY(Float.valueOf(centerYAS));
                    }
                    catch (Exception exc)
                    {

                    }

                    // center z
                    try
                    {
                        pd.setCenterZ(Float.valueOf(centerZAS));
                    }
                    catch (Exception exc)
                    {

                    }

                    try
                    {
                        PEAnimationType peAnimationType = PEAnimationType.valueOf(animationAS);
                        if (!peAnimationType.equals(peAnimationType.NONE))
                        {
                            pd.setParticleAnimationEffect(peAnimationType.getAnimationEffectInstance(cc, pd));
                            pd.setHasAnimation(true);
                        }
                    }
                    catch (Exception exc)
                    {
                        StatusLoggerEvent.PARTICLE_ANIMATION_INVALID.log(getCrates(), new String[]{animationAS});
                    }

                    addParticle(pd, id);
                }
                catch (Exception exc)
                {
                    exc.printStackTrace();
                    StatusLoggerEvent.PARTICLE_STRING_INVALID.log(getCrates(), new String[]{parent});
                }
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    public void runAll(Location l, CrateState cs, ArrayList<Reward> rewards)
    {
        ArrayList<ParticleData> alreadyUpdatedAnimations = cc.getAlreadyUpdated();
        for (String id : getParticles().keySet())
        {
            if (cs.equals(CrateState.PLAY))
            {
                if (cs.name().toUpperCase().equalsIgnoreCase(id))
                {
                    for (ParticleData pd : getParticles().get(id))
                    {
                        if (pd.isHasAnimation())
                        {
                            //PEAnimationType peAnimationType = PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect());
                            if (!alreadyUpdatedAnimations.contains(pd))
                            {
                                pd.getParticleAnimationEffect().update();
                                alreadyUpdatedAnimations.add(pd);
                            }
                            //System.out.println("has animation");
                            pd.getParticleAnimationEffect().display(l);
                        }
                        else
                        {
                            //System.out.println("does not have an animation");
                            pd.display(l);
                        }
                    }
                }
                continue;
            }

            if ((id.equalsIgnoreCase(cs.name().toUpperCase()) && (!up().isTiersOverrideDefaults() || rewards.isEmpty() ||
                    !getParticles().keySet().contains(rewards.get(0).getRarity().toUpperCase()))) ||
                    (!rewards.isEmpty() && rewards.get(0).getRarity().equalsIgnoreCase(id)))
            {
                for (ParticleData pd : getParticles().get(id))
                {
                    if (pd.isHasAnimation())
                    {
                        //PEAnimationType peAnimationType = PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect());
                        if (!alreadyUpdatedAnimations.contains(pd))
                        {
                            pd.getParticleAnimationEffect().update();
                            alreadyUpdatedAnimations.add(pd);
                        }
                        pd.getParticleAnimationEffect().display(l);
                    }
                    else
                    {
                        pd.display(l);
                    }
                }
            }
        }
    }

    public String getPath(String tier)
    {
        return (tier.equalsIgnoreCase("PLAY") ? "play." : "open.") +
                (tier.equalsIgnoreCase("open") || tier.equalsIgnoreCase("play") ? "" : "crate-tiers." + tier + ".") +
                "particles";
    }

    public HashMap<String, ArrayList<ParticleData>> getParticles()
    {
        return particles;
    }

    public void setParticles(HashMap<String, ArrayList<ParticleData>> particles)
    {
        this.particles = particles;
    }

}
