package me.ztowne13.customcrates;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.*;
import org.bukkit.Location;

import java.io.File;
import java.util.HashMap;

public class Settings
{
    CustomCrates cc;

    HashMap<String, String> infoToLog = new HashMap<String, String>();
    HashMap<String, Object> configValues = new HashMap<String, Object>();

    public Settings(CustomCrates cc)
    {
        this.cc = cc;
    }


    public void load()
    {
        loadSettings();
        ChatUtils.log("-------------------------");
        loadCrates(false);
        loadCrates(true);
        loadPlacedCrates();
        loadInfo();
    }

    public void loadCrates(boolean multiCrate)
    {
        File folder = new File(getCc().getDataFolder().getPath() + "/Crates");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++)
        {
            if (listOfFiles[i].isFile())
            {
                File f = listOfFiles[i];

                if (f.getName().contains(";") || f.getName().contains(","))
                {
                    ChatUtils.log(new String[]{"FAILED TO LOAD THE " + f.getName() + " CRATE.",
                            "  CAUSE: Crate's Name contains ',' or ';' characters."});
                    continue;
                }

                if (f.getName().endsWith(".crate") && !multiCrate)
                {
                    String crateName = f.getName().replace(".crate", "");
                    Crate.getCrate(getCc(), crateName);
                }
                else if (f.getName().endsWith(".multicrate") && multiCrate)
                {
                    String crateName = f.getName().replace(".multicrate", "");
                    Crate.getCrate(getCc(), crateName, true);
                }
            }
        }
    }

    public void loadPlacedCrates()
    {
        for (String s : getCc().getActivecratesFile().get().getKeys(false))
        {
            FileHandler activeCrates = getCc().getActivecratesFile();
            String crateName = activeCrates.get().getString(s + ".crate");
            Location l = LocationUtils.stringToLoc(s);

            if (Crate.exists(crateName) && l != null)
            {
                Crate crates = Crate.getCrate(getCc(), crateName);

                PlacedCrate cm = new PlacedCrate(getCc(), l);

                if (crates.isEnabled())
                {
                    cm.setup(crates, false);
                }
                else
                {
                    cm.setCratesEnabled(false);
                }

                if (activeCrates.get().contains(s + ".placedTime"))
                {
                    cm.setPlacedTime(activeCrates.get().getLong(s + ".placedTime"));
                }
                else
                {
                    cm.setPlacedTime(0L);
                }
            }
            else
            {
                ChatUtils.log(new String[]{"ERROR: " + crateName + " DOES NOT EXIST TO BE USED AT LOCATION: " + s});
            }
        }
    }

	/*public void loadCrateSettingsAfter()
	{
		for(Crate crate : Crate.getLoadedCrates().values())
		{
			if(crate.isMultiCrate())
			{
				crate.getCs().getCmci().loadFor(crate.getCs().getCsb(), CrateState.OPEN);
			}
		}
	}*/

    public void loadSettings()
    {
        for (SettingsValues sv : SettingsValues.values())
        {
            getConfigValues().put(sv.getPath(), getCc().getConfig().get(sv.getPath()));
        }
    }

    public void loadInfo()
    {
        Utils.addToInfoLog(cc, "Server version", NMSUtils.getServerVersion());
        Utils.addToInfoLog(cc, "Citizens Installed",
                (NPCUtils.isCitizensInstalled() ? "" : "&c") + Utils.isPLInstalled("Citizens") + "");

        int enabled = 0, disabled = 0;
        for (Crate crates : Crate.getLoadedCrates().values())
        {
            if (crates.isEnabled())
            {
                enabled++;
            }
            else
            {
                disabled++;
            }
        }

        Utils.addToInfoLog(cc, "Crate loaded", enabled + " enabled &4/ &c" + disabled + " disabled");
        Utils.addToInfoLog(cc, "Crate placed", PlacedCrate.getPlacedCrates().keySet().size() + "");
    }

    public void writeSettingsValues()
    {
        FileHandler fu = new FileHandler(cc, "config.yml", true, true);
        for (String s : getConfigValues().keySet())
        {
            fu.get().set(s, getConfigValues().get(s));
        }

        fu.save();
    }

    public Boolean getConfigValAsBoolean(String path)
    {
        return Boolean.valueOf(getConfigValues().get(path).toString());
    }

    public HashMap<String, Object> getConfigValues()
    {
        return configValues;
    }

    public void setConfigValues(HashMap<String, Object> configValues)
    {
        this.configValues = configValues;
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public HashMap<String, String> getInfoToLog()
    {
        return infoToLog;
    }

    public void setInfoToLog(HashMap<String, String> infoToLog)
    {
        this.infoToLog = infoToLog;
    }
}
