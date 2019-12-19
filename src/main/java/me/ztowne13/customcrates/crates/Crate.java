package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class Crate
{
    static HashMap<String, Crate> loadedCrates = new HashMap<String, Crate>();

    SpecializedCrates cc;
    String name;

    int placedCount = 0;
    String lastOpenedName = "Nobody";
    String lastOpenedReward = "Nothing";

    boolean enabled = true, canBeEnabled = true, isMultiCrate, isUsedForCratesCommand = false;

    CrateSettings cs;

    public Crate(SpecializedCrates cc, String name, boolean newFile)
    {
        this(cc, name, newFile, false);
    }

    public Crate(SpecializedCrates cc, String name, boolean newFile, boolean isMultiCrate)
    {
        this.cc = cc;
        this.name = name;

        this.isMultiCrate = isMultiCrate;
        this.cs = new CrateSettings(cc, this, newFile);

        setEnabled(!getCs().getCsb().hasV("enabled") || cs.getFc().getBoolean("enabled"));

        getLoadedCrates().put(name, this);

        getCs().loadAll();
    }

    @Deprecated
    public void tick(Location l, CrateState cstate)
    {
        getCs().playAll(l, cstate);
    }

    @Deprecated
    public void tick(Location l, CrateState cstate, Player p, ArrayList<Reward> rewards)
    {
        tick(l, null, cstate, p, rewards);
    }

    public void tick(Location l, PlacedCrate placedCrate, CrateState cstate, Player p, ArrayList<Reward> rewards)
    {
        getCs().playAll(l, placedCrate, cstate, p, rewards);
    }

    public ArrayList<PlacedCrate> deleteAllPlaced()
    {
        HashMap<Location, PlacedCrate> placed = new HashMap<Location, PlacedCrate>(PlacedCrate.getPlacedCrates());
        ArrayList<PlacedCrate> deleted = new ArrayList<PlacedCrate>();

        for (Location l : placed.keySet())
        {
            PlacedCrate cm = placed.get(l);
            if (cm.getCrates().equals(this))
            {
                deleted.add(cm);
                cm.getCrates().getCs().getDcp().remove(cm);
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

    public boolean crateMatchesToStack(ItemStack stack)
    {
        ItemStack crate = getCs().getCrate(1);
        if (Utils.itemHasName(stack))
        {
            return crate.getType().equals(stack.getType()) &&
                    crate.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName());
        }
        return false;
    }

    public boolean keyMatchesToStack(ItemStack stack)
    {
        ItemStack crate = getCs().getKey(1);
        if (Utils.itemHasName(stack))
        {
            return crate.getType().equals(stack.getType()) &&
                    crate.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName());
        }
        return false;
    }

    public static boolean crateAlreadyExist(String name)
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

    public CrateSettings getCs()
    {
        return cs;
    }

    public void setCs(CrateSettings cs)
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
        return getName();
    }
}
