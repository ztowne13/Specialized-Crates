package me.ztowne13.customcrates.utils;

import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.gui.DynamicMaterial;
import me.ztowne13.customcrates.logging.StatusLogger;
import me.ztowne13.customcrates.logging.StatusLoggerEvent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class FileUtils
{
	public static ItemStack loadItem(String s, StatusLogger sl, StatusLoggerEvent invMaterial, StatusLoggerEvent invByt, StatusLoggerEvent invalid, StatusLoggerEvent success)
	{
		String[] args = s.split(";");
		try
		{
			Material m = null;
			try
			{
				m = DynamicMaterial.fromString(args[0].toUpperCase()).parseMaterial();
			}
			catch(Exception exc)
			{
				invMaterial.log(sl, new String[]{args[0]});
				return new ItemStack(Material.AIR);
			}
			short byt = 0;
			if(s.contains(";"))
			{
				if(Utils.isInt(args[1]))
				{
					byt = Short.valueOf(args[1]);
				}
				else
				{
					invByt.log(sl, new String[]{args[1]});
				}
			}

			success.log(sl, new String[]{s});

			return new ItemStack(m, 1, byt);
		}
		catch(Exception exc)
		{
			invalid.log(sl, new String[]{s});
		}
		return new ItemStack(Material.AIR);
	}

	public static SoundData loadSound(String value, StatusLogger sl, StatusLoggerEvent soundSuccess,
									  StatusLoggerEvent soundFailure,
									  StatusLoggerEvent volumeSuccess,
									  StatusLoggerEvent volumeInvalid,
									  StatusLoggerEvent noVolPitch,
									  StatusLoggerEvent pitchSuccess,
									  StatusLoggerEvent pitchInvalid)
	{
		try
		{

			String[] args = value.replaceAll("\\s+","").split(",");

			SoundData sd = new SoundData(Sound.valueOf(args[0].toUpperCase()));

			soundSuccess.log(sl);

			if(args.length >= 2)
			{
				if(Utils.isInt(args[1]))
				{
					sd.setVolume(Integer.parseInt(args[1]));
					volumeSuccess.log(sl);
				}
				else
				{
					sd.setVolume(5);
					volumeInvalid.log(sl, new String[]{args[1]});
				}

				if(args.length >= 3)
				{
					if(Utils.isInt(args[2]))
					{
						sd.setPitch(Integer.parseInt(args[2]));
						pitchSuccess.log(sl);
					}
					else
					{
						sd.setPitch(5);
						pitchInvalid.log(sl, new String[]{args[2]});
					}
				}
				else
				{
					sd.setPitch(5);
				}
			}
			else
			{
				noVolPitch.log(sl);
				sd.setVolume(5);
				sd.setPitch(5);
			}

			return sd;
		}
		catch(Exception exc)
		{
			//setTickSound(new SoundData(Sound.FALL_BIG));
			soundFailure.log(sl);
		}

		return null;
	}

	public static int loadInt(String s, int defValue, StatusLogger sl, StatusLoggerEvent success, StatusLoggerEvent invalid)
	{
		if(Utils.isInt(s))
		{
			success.log(sl);
			return Integer.parseInt(s);
		}
		else
		{
			invalid.log(sl);
			return defValue;
		}
	}

	public static double loadDouble(String s, double defValue, StatusLogger sl, StatusLoggerEvent success, StatusLoggerEvent invalid)
	{
		if(Utils.isDouble(s))
		{
			success.log(sl);
			return Double.parseDouble(s);
		}
		else
		{
			invalid.log(sl);
			return defValue;
		}
	}

	public static boolean loadBoolean(String s, boolean defValue, StatusLogger sl, StatusLoggerEvent success, StatusLoggerEvent invalid)
	{
		if(Utils.isBoolean(s))
		{
			success.log(sl);
			return Boolean.parseBoolean(s);
		}
		else
		{
			invalid.log(sl);
			return defValue;
		}
	}
}
