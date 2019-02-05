package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSounds extends CSetting
{
	HashMap<String,ArrayList<SoundData>> sounds = new HashMap<String,ArrayList<SoundData>>();
	
	public CSounds(Crate crates)
	{
		super(crates, crates.getCc());
	}

	@Override
	public void loadFor(CrateSettingsBuilder csb, CrateState cs) 
	{
		if(csb.hasV("open.sounds"))
		{
			addSoundsFromList("OPEN", csb.getSettings().getFc().getStringList("open.sounds"));
		}
		if(csb.hasV("open.crate-tiers"))
		{
			for(String id: up().getFc().getConfigurationSection("open.crate-tiers").getKeys(false))
			{
				if(csb.hasV("open.crate-tiers." + id + ".sounds"))
				{
					addSoundsFromList(id.toUpperCase(), up().getFc().getStringList("open.crate-tiers." + id + ".sounds"));
				}
			}
		}
	}

	public void saveToFile()
	{
		if(!sounds.isEmpty())
		{
			for(String tier : sounds.keySet())
			{
				ArrayList<String> listToSet = new ArrayList<>();
				for(SoundData sd : sounds.get(tier))
				{
					String parsedSoundData = sd.getSound().name() + ", " + sd.getPitch() + ", " + sd.getVolume();
					listToSet.add(parsedSoundData);
				}

				String path = "open." + (tier.equalsIgnoreCase("OPEN") ? "" : "crate-tiers." + tier) + "sounds";
				getFu().get().set(path, listToSet);
			}
		}
	}
	
	public void addSoundsFromList(String id, List<String> list)
	{
		for(String sound: list)
		{
			try
			{
				String[] args = sound.replace(" ", "").split(",");
				
				Sound soundFormatted;

				try
				{
					soundFormatted = Sound.valueOf(args[0].toUpperCase());
				}
				catch(Exception exc)
				{
					StatusLoggerEvent.SOUND_NONEXISTENT.log(getCrates(), new String[]{sound, args[0]});
					continue;
				}
				
				SoundData sd = new SoundData(soundFormatted);
				
				try
				{
					Integer pitch = Integer.valueOf(args[1]);
					sd.setPitch(pitch);
				}
				catch(Exception exc)
				{
					if(args.length > 0)
					{
						StatusLoggerEvent.SOUND_PITCH_INVALID.log(getCrates(), new String[]{soundFormatted.name(), args[1]});
					}
					else
					{
						StatusLoggerEvent.SOUND_PITCH_NONEXISTENT.log(getCrates(), new String[]{soundFormatted.name()});
					}
					continue;
				}
				
				try
				{
					Integer volume = Integer.valueOf(args[2]);
					sd.setVolume(volume);
				}
				catch(Exception exc)
				{
					if(args.length > 1)
					{
						StatusLoggerEvent.SOUND_VOLUME_INVALID.log(getCrates(), new String[]{soundFormatted.name(), args[2]});
					}
					else
					{
						StatusLoggerEvent.SOUND_VOLUME_NONEXISTENT.log(getCrates(), new String[]{soundFormatted.name()});
					}
					continue;
				}
				
				addSound(id, sd);
				StatusLoggerEvent.SOUND_ADD_SUCCESS.log(getCrates(), new String[]{soundFormatted.name()});
			}
			catch(Exception exc)
			{
				StatusLoggerEvent.SOUND_ADD_IMPROPER_SETUP.log(getCrates(), new String[]{sound});
				exc.printStackTrace();
			}
		}
	}
	
	public void addSound(String id, SoundData s)
	{
		id = id.toUpperCase();
		if(getSounds().containsKey(id))
		{
			ArrayList<SoundData> list = getSounds().get(id);
			list.add(s);
			getSounds().put(id, list);
			return;
		}
		
		ArrayList<SoundData> list = new ArrayList<SoundData>();
		list.add(s);
		getSounds().put(id, list);
	}

	public void runAll(Player p, Location l, ArrayList<Reward> rewards)
	{
		for(String tier: getSounds().keySet())
		{
			if((tier.equalsIgnoreCase("OPEN") && (!up().isTiersOverrideDefaults() || !getSounds().containsKey(rewards.get(0).getRarity().toUpperCase()))) || rewards.get(0).getRarity().equalsIgnoreCase(tier))
			{
				for(SoundData sd: getSounds().get(tier))
				{
					sd.playTo(p, l);
				}
			}
		}
	}

	public SoundData getSoundFromName(String tier, Sound s)
	{
		SoundData sd = null;

		for(SoundData soundData : getSounds().get(tier))
		{
			if(soundData.getSound().equals(s))
			{
				sd = soundData;
				break;
			}
		}

		return sd;
	}

	public HashMap<String, ArrayList<SoundData>> getSounds()
	{
		return sounds;
	}

	public void setSounds(HashMap<String, ArrayList<SoundData>> sounds)
	{
		this.sounds = sounds;
	}
}
