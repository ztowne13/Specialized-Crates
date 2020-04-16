package me.ztowne13.customcrates.interfaces.files;

import me.ztowne13.customcrates.crates.options.sounds.SoundData;
import me.ztowne13.customcrates.interfaces.items.DynamicMaterial;
import me.ztowne13.customcrates.interfaces.items.ItemBuilder;
import me.ztowne13.customcrates.interfaces.logging.StatusLogger;
import me.ztowne13.customcrates.interfaces.logging.StatusLoggerEvent;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Created by ztowne13 on 7/7/16.
 */
public class FileDataLoader
{
    FileHandler fileHandler;

    public FileDataLoader(FileHandler fileHandler)
    {
        this.fileHandler = fileHandler;
    }

    public String loadString(String path, StatusLogger  statusLogger, StatusLoggerEvent pathDoesntExist, StatusLoggerEvent success)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return "";
        }

        success.log(statusLogger);
        return fileHandler.get().getString(path);
    }

    public ItemBuilder loadItem(String path, ItemBuilder defValue, StatusLogger statusLogger, StatusLoggerEvent pathDoesntExist,
                              StatusLoggerEvent invalidMaterial,
                              StatusLoggerEvent invalidByte,
                              StatusLoggerEvent invalid, StatusLoggerEvent success)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return defValue;
        }

        String value = fileHandler.get().getString(path);

        String[] args = value.split(";");
        try
        {
            Material m = null;
            try
            {
                m = DynamicMaterial.fromString(args[0].toUpperCase()).parseMaterial();
            }
            catch (Exception exc)
            {
                invalidMaterial.log(statusLogger, new String[]{args[0]});
                return defValue;
            }

            short byt = 0;
            if (value.contains(";"))
            {
                if (Utils.isInt(args[1]))
                {
                    byt = Short.valueOf(args[1]);
                }
                else
                {
                    invalidByte.log(statusLogger, new String[]{args[1]});
                }
            }
            success.log(statusLogger, new String[]{value});
            DynamicMaterial dynMat = DynamicMaterial.fromString(m.name() + ";" + byt);
            return new ItemBuilder(dynMat, 1);
        }
        catch (Exception exc)
        {
            invalid.log(statusLogger, new String[]{value});
        }
        return defValue;
    }

    public SoundData loadSound(String path, StatusLogger statusLogger, StatusLoggerEvent pathDoesntExist,
                               StatusLoggerEvent soundSuccess,
                               StatusLoggerEvent soundFailure,
                               StatusLoggerEvent volumeSuccess,
                               StatusLoggerEvent volumeInvalid,
                               StatusLoggerEvent noVolPitch,
                               StatusLoggerEvent pitchSuccess,
                               StatusLoggerEvent pitchInvalid)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return new SoundData(Sound.values()[0], 0);
        }

        String value = fileHandler.get().getString(path);

        try
        {

            String[] args = value.replaceAll("\\s+", "").split(",");

            SoundData sd = new SoundData(Sound.valueOf(args[0].toUpperCase()));

            soundSuccess.log(statusLogger);

            if (args.length >= 2)
            {
                if (Utils.isInt(args[1]))
                {
                    sd.setVolume(Integer.parseInt(args[1]));
                    volumeSuccess.log(statusLogger);
                }
                else
                {
                    sd.setVolume(5);
                    volumeInvalid.log(statusLogger, new String[]{args[1]});
                }

                if (args.length >= 3)
                {
                    if (Utils.isInt(args[2]))
                    {
                        sd.setPitch(Integer.parseInt(args[2]));
                        pitchSuccess.log(statusLogger);
                    }
                    else
                    {
                        sd.setPitch(5);
                        pitchInvalid.log(statusLogger, new String[]{args[2]});
                    }
                }
                else
                {
                    sd.setPitch(5);
                }
            }
            else
            {
                noVolPitch.log(statusLogger);
                sd.setVolume(5);
                sd.setPitch(5);
            }

            return sd;
        }
        catch (Exception exc)
        {
            soundFailure.log(statusLogger);
        }

        return new SoundData(Sound.values()[0], 0);
    }

    public int loadInt(String path, int defValue, StatusLogger statusLogger, StatusLoggerEvent pathDoesntExist,
                       StatusLoggerEvent success, StatusLoggerEvent invalid)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return defValue;
        }

        String value = fileHandler.get().getString(path);

        if (Utils.isInt(value))
        {
            success.log(statusLogger);
            return Integer.parseInt(value);
        }
        else
        {
            invalid.log(statusLogger);
            return defValue;
        }
    }

    public double loadDouble(String path, double defValue, StatusLogger statusLogger, StatusLoggerEvent pathDoesntExist,
                             StatusLoggerEvent success,
                             StatusLoggerEvent invalid)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return defValue;
        }

        String value = fileHandler.get().getString(path);

        if (Utils.isDouble(value))
        {
            success.log(statusLogger);
            return Double.parseDouble(value);
        }
        else
        {
            invalid.log(statusLogger);
            return defValue;
        }
    }

    public long loadLong(String path, long defValue, StatusLogger statusLogger, StatusLoggerEvent pathDoesntExist,
                         StatusLoggerEvent success,
                         StatusLoggerEvent invalid)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return defValue;
        }

        String value = fileHandler.get().getString(path);

        if (Utils.isLong(value))
        {
            success.log(statusLogger);
            return Long.parseLong(value);
        }
        else
        {
            invalid.log(statusLogger);
            return defValue;
        }
    }

    public boolean loadBoolean(String path, boolean defValue, StatusLogger statusLogger, StatusLoggerEvent pathDoesntExist,
                               StatusLoggerEvent success,
                               StatusLoggerEvent invalid)
    {
        if (!fileHandler.get().contains(path))
        {
            pathDoesntExist.log(statusLogger, new String[]{path});
            return defValue;
        }

        String value = fileHandler.get().getString(path);

        if (Utils.isBoolean(value))
        {
            success.log(statusLogger);
            return Boolean.parseBoolean(value);
        }
        else
        {
            invalid.log(statusLogger);
            return defValue;
        }
    }
}
