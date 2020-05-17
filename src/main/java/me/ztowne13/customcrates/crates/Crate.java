package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.DataHandler;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Crate
{
    static HashMap<String, Crate> loadedCrates = new HashMap<String, Crate>();

    SpecializedCrates cc;
    String name;

    int placedCount = 0;
    String lastOpenedName = "Nobody";
    String lastOpenedReward = "Nothing";


    boolean enabled = true,
            canBeEnabled = true,
            isMultiCrate,
            isUsedForCratesCommand = false,
            loadedProperly = false;

    CrateSettings cs;

    public Crate(SpecializedCrates cc, String name, boolean newFile)
    {
        this(cc, name, newFile, false);
    }

    public Crate(SpecializedCrates cc, String name, boolean newFile, boolean isMultiCrate)
    {
        cc.getDu().log("Crate() - new", getClass());
        this.cc = cc;
        this.name = name;

        this.isMultiCrate = isMultiCrate;
        this.cs = new CrateSettings(cc, this, newFile);

        setEnabled(!getSettings().getSettingsBuilder().hasV("enabled") || cs.getFc().getBoolean("enabled"));

        getLoadedCrates().put(name, this);

        getSettings().loadAll();
        loadedProperly = true;
    }

    @Deprecated
    public void tick(Location l, CrateState cstate, Player p, ArrayList<Reward> rewards)
    {
        tick(l, null, cstate, p, rewards);
    }

    public void tick(Location l, PlacedCrate placedCrate, CrateState cstate, Player p, ArrayList<Reward> rewards)
    {
        getSettings().getParticles().runAll(l, cstate, rewards);
        if (cstate.equals(CrateState.OPEN) && CrateUtils.isCrateUsable(this))
        {
            getSettings().getSounds().runAll(p, l, rewards);
            getSettings().getFireworks().runAll(p, l, rewards);
            if (rewards != null && !rewards.isEmpty())
            {
                getSettings().getActions().playAll(p, placedCrate, rewards, false);
            }
        }
    }

    public boolean rename(String newName)
    {

        if (newName.contains("."))
        {
            newName = newName.split(".")[0];
        }

        if(FileHandler.getMap().containsKey(newName + ".crate") |
                FileHandler.getMap().containsKey(newName + ".multicrate"))
        {
            return false;
        }

        FileHandler newFile =
                new FileHandler(getCc(), newName + (isMultiCrate() ? ".multicrate" : ".crate"),
                        "/Crates", true, true, true);
        newFile.reload();

        try
        {
            getSettings().getFileHandler().copy(newFile);
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
            return false;
        }

        HashMap<Location, PlacedCrate> placed = new HashMap<>(PlacedCrate.getPlacedCrates());

        for (Location l : placed.keySet())
        {
            PlacedCrate cm = placed.get(l);
            if (cm.getCrate().equals(this))
            {
                cm.rename(newName);
                PlacedCrate.getPlacedCrates().remove(l);
            }
        }

        deleteCrate();
        return true;
    }

    public String deleteCrate()
    {
        deleteAllPlaced();

        Path path = getSettings().getFileHandler().getDataFile().toPath();

        if (getSettings().getFileHandler().getDataFile().delete())
        {
            ChatUtils.log("Successfully deleted file " + path);
        }
        else
        {
            return "File nonexistent, please try reloading or contacting the plugin author.";
        }

        for (UUID id : cc.getDataHandler().getQuedGiveCommands().keySet())
        {
            ArrayList<DataHandler.QueuedGiveCommand> cmds = cc.getDataHandler().getQuedGiveCommands().get(id);
            for (DataHandler.QueuedGiveCommand cmd : cmds)
            {
                if (cmd.getCrate().equals(this))
                {
                    cmds.remove(cmd);
                    cc.getDataHandler().getQuedGiveCommands().remove(id);
                    cc.getDataHandler().getQuedGiveCommands().put(id, cmds);
                }
            }
        }

        cc.getDataHandler().saveToFile();

        Bukkit.getScheduler().scheduleSyncDelayedTask(cc, new Runnable()
        {
            @Override
            public void run()
            {
                cc.reload();

            }
        }, 20);
        return path.toString();
    }

    public ArrayList<PlacedCrate> deleteAllPlaced()
    {
        HashMap<Location, PlacedCrate> placed = new HashMap<Location, PlacedCrate>(PlacedCrate.getPlacedCrates());
        ArrayList<PlacedCrate> deleted = new ArrayList<PlacedCrate>();

        for (Location l : placed.keySet())
        {
            PlacedCrate cm = placed.get(l);
            if (cm.getCrate().equals(this))
            {
                deleted.add(cm);
                cm.getCrate().getSettings().getPlaceholder().remove(cm);
                cm.delete();
            }
        }

        return deleted;
    }

    public static Crate getCrate(SpecializedCrates cc, String name)
    {
        return getCrate(cc, name, false);
    }

    public static Crate getCrate(SpecializedCrates cc, String name, boolean isMultiCrate)
    {
        return getLoadedCrates().containsKey(name) ? getLoadedCrates().get(name) : new Crate(cc, name, false, isMultiCrate);
    }

    public static boolean existsNotCaseSensitive(String name)
    {
        for (String crates : getLoadedCrates().keySet())
        {
            if (crates.equalsIgnoreCase(name))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean exists(String name)
    {
        return getLoadedCrates().containsKey(name);
    }

    public static void clearLoaded()
    {
        getLoadedCrates().clear();
        setLoadedCrates(new HashMap<String, Crate>());
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public static HashMap<String, Crate> getLoadedCrates()
    {
        return loadedCrates;
    }

    public static void setLoadedCrates(HashMap<String, Crate> loadedCrates)
    {
        Crate.loadedCrates = loadedCrates;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public CrateSettings getSettings()
    {
        return cs;
    }

    public void setSettings(CrateSettings cs)
    {
        this.cs = cs;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        for (PlacedCrate cm : PlacedCrate.getPlacedCrates().values())
        {
            cm.setCratesEnabled(enabled);
        }

        this.enabled = enabled;
    }

    public boolean isCanBeEnabled()
    {
        return canBeEnabled;
    }

    public void setCanBeEnabled(boolean canBeEnabled)
    {
        this.canBeEnabled = canBeEnabled;
    }

    public boolean isMultiCrate()
    {
        return isMultiCrate;
    }

    public void setMultiCrate(boolean multiCrate)
    {
        isMultiCrate = multiCrate;
    }

    public int getPlacedCount()
    {
        return placedCount;
    }

    public void setPlacedCount(int placedCount)
    {
        this.placedCount = placedCount;
    }

    public boolean isUsedForCratesCommand()
    {
        return isUsedForCratesCommand;
    }

    public void setUsedForCratesCommand(boolean usedForCratesCommand)
    {
        isUsedForCratesCommand = usedForCratesCommand;
    }

    public String getLastOpenedName()
    {
        return lastOpenedName;
    }

    public void setLastOpenedName(String lastOpenedName)
    {
        this.lastOpenedName = lastOpenedName;
    }

    public String getLastOpenedReward()
    {
        return lastOpenedReward;
    }

    public void setLastOpenedReward(String lastOpenedReward)
    {
        this.lastOpenedReward = lastOpenedReward;
    }

    public String getDisplayName()
    {
        if((boolean) SettingsValues.USE_CRATE_NAME_FOR_DISPLAY.getValue(getCc())) {
            if(getSettings().getCrateItemHandler().getItem().hasDisplayName()) {
                return getSettings().getCrateItemHandler().getItem().getDisplayName(true);
            }
        }
        return getName();
    }

    public boolean isLoadedProperly()
    {
        return loadedProperly;
    }
}
