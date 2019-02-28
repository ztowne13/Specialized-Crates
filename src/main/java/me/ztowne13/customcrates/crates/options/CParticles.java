package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
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
import java.util.List;

public class CParticles extends CSetting
{
	private HashMap<String,ArrayList<ParticleData>> particles = new HashMap<String,ArrayList<ParticleData>>();

	public CParticles(Crate crates)
	{
		super(crates, crates.getCc());
	}

	@Override
	public void loadFor(CrateSettingsBuilder csb, CrateState cstate)
	{
		if(csb.hasV(cstate.name().toLowerCase() + ".particles"))
		{ 
			parseAndAddParticles(cstate.name().toUpperCase(), getCrates().getCs().getFc().getStringList(cstate.name().toLowerCase() + ".particles"));
		}
		
		if(cstate.equals(CrateState.OPEN))
		{
			if(csb.hasV("open.crate-tiers"))
			{
				for(String id: getCrates().getCs().getFc().getConfigurationSection("open.crate-tiers").getKeys(false))
				{
					if(csb.hasV("open.crate-tiers." + id + ".particles"))
					{
						parseAndAddParticles(id, getCrates().getCs().getFc().getStringList("open.crate-tiers." + id + ".particles"));
					}
				}
			}
		}
	}

	public void saveToFile()
	{
		if(!particles.isEmpty())
		{
			for(String tier : particles.keySet())
			{
				ArrayList<String> listToSet = new ArrayList<>();
				for(ParticleData pd : particles.get(tier))
				{
					String parsedParticle = pd.getParticleName() + ", " + pd.getOffX() + ", " + pd.getOffY() + ", " + pd.getOffZ() + ", " + pd.getSpeed() + ", " + pd.getAmount();
					if(!(pd.getParticleAnimationEffect() == null))
					{
						parsedParticle = parsedParticle + ", " + PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect());
					}
					listToSet.add(parsedParticle);
				}

				String path = (tier.equalsIgnoreCase("PLAY") ? "play." : "open.") + (tier.equalsIgnoreCase("open") || tier.equalsIgnoreCase("play") ? "" : "crate-tiers." + tier + ".") + "particles";
				getFu().get().set(path, listToSet);
			}
		}
	}

	public void addParticle(ParticleData pd, String s)
	{
		ArrayList<ParticleData> plist = getParticles().containsKey(s) ? getParticles().get(s) : new ArrayList<ParticleData>();
		plist.add(pd);
		StatusLoggerEvent.PARTICLE_ADD_SUCCESS.log(getCrates(), new String[]{pd.getParticleName()});
		getParticles().put(s, plist);
	}

	public void parseAndAddParticles(String id, List<String> list)
	{
		for(String s: list)
		{
			try
			{
				String[] split = s.toString().replace(" ", "").split(",");

				ParticleData pd;

				if(!NMSUtils.Version.v1_10.isServerVersionOrLater())
				{
					ParticleEffect pe = null;
					try
					{
						pe = ParticleEffect.valueOf(split[0].toUpperCase());
					}
					catch(Exception exc)
					{
						StatusLoggerEvent.PARTICLE_INVALID.log(getCrates(), new String[]{s, split[0].toUpperCase()});
						continue;
					}
					pd = new NMSParticleEffect(pe, false);
				}
				else
				{
					try
					{
						pd = new BukkitParticleEffect(split[0].toUpperCase(), false);
					}
					catch(Exception exc)
					{
						StatusLoggerEvent.PARTICLE_INVALID.log(getCrates(), new String[]{s, split[0].toUpperCase()});
						continue;
					}
				}

				pd.setOffX(Float.valueOf(split[1]));
				pd.setOffY(Float.valueOf(split[2]));
				pd.setOffZ(Float.valueOf(split[3]));
				pd.setSpeed(Float.valueOf(split[4]));
				pd.setAmount(Integer.valueOf(split[5]));

				if(split.length >= 7)
				{
					try
					{
						PEAnimationType peAnimationType = PEAnimationType.valueOf(split[6].toUpperCase());
						pd.setParticleAnimationEffect(peAnimationType.getAnimationEffectInstance(cc, pd));
						pd.setHasAnimation(true);
					}
					catch(Exception exc)
					{
						StatusLoggerEvent.PARTICLE_ANIMATION_INVALID.log(getCrates(), new String[]{split[6]});
					}
				}
				else if(split.length >= 10)
				{
					try
					{
						int red = Integer.parseInt(split[7]);
						int green = Integer.parseInt(split[8]);
						int blue = Integer.parseInt(split[9]);

						pd.setRed(red);
						pd.setGreen(green);
						pd.setBlue(blue);
						pd.setHasColor(true);
					}
					catch(Exception exc)
					{
						StatusLoggerEvent.PARTICLE_ANIMATION_COLOR_INVALID.log(getCrates(), new String[]{pd.getParticleName()});
					}
				}

				addParticle(pd, id);
			}	
			catch(Exception exc)
			{
				exc.printStackTrace();
				StatusLoggerEvent.PARTICLE_STRING_INVALID.log(getCrates(), new String[]{s});
			}
		}
	}

	public void runAll(Location l, CrateState cs, ArrayList<Reward> rewards)
	{
		ArrayList<ParticleData> alreadyUpdatedAnimations = cc.getAlreadyUpdated();
		for(String id: getParticles().keySet())
		{
			if(cs.equals(CrateState.PLAY))
			{
				if(cs.name().toUpperCase().equalsIgnoreCase(id))
				{
					for(ParticleData pd: getParticles().get(id))
					{
						if(pd.isHasAnimation())
						{
							//PEAnimationType peAnimationType = PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect());
							if(!alreadyUpdatedAnimations.contains(pd))
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

			if((id.equalsIgnoreCase(cs.name().toUpperCase()) && (!up().isTiersOverrideDefaults() || !getParticles().keySet().contains(rewards.get(0).getRarity().toUpperCase()))) || rewards.get(0).getRarity().equalsIgnoreCase(id))
			{
				for(ParticleData pd: getParticles().get(id))
				{
					if(pd.isHasAnimation())
					{
						//PEAnimationType peAnimationType = PEAnimationType.getFromParticleAnimationEffect(pd.getParticleAnimationEffect());
						if(!alreadyUpdatedAnimations.contains(pd))
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

	public ParticleData getParticleFromName(String tier, String pn)
	{
		ParticleData pd = null;

		for(ParticleData particleData : getParticles().get(tier))
		{
			if(particleData.getParticleName().equalsIgnoreCase(pn))
			{
				pd = particleData;
				break;
			}
		}

		return pd;
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
