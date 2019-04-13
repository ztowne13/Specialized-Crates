package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.crates.options.CHolograms;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

import java.util.HashMap;

/**
 * Manages individual placed crates.
 */
public class PlacedCrate
{
    static HashMap<Location, PlacedCrate> placedCrates = new HashMap<Location, PlacedCrate>();

    CustomCrates cc;
    Crate crates;

    boolean isCratesEnabled;
    boolean deleted = false;

    CHolograms cholo;

    Location l;
    Long placedTime;
    boolean used = false;

    public PlacedCrate(CustomCrates cc, Location l)
    {
        this.cc = cc;
        this.l = l;

        getPlacedCrates().put(l, this);
    }


    public void delete()
    {
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()), null);
        getCc().getActivecratesFile().save();
        getCholo().getDh().delete();
        getCrates().getCs().getDcp().remove(this);
        getPlacedCrates().remove(getL());
        deleted = true;
    }

    public void writeToFile()
    {
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()) + ".crate", getCrates().getName());
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()) + ".placedTime", getPlacedTime());
        getCc().getActivecratesFile().save();
    }

    public void setup(Crate crates, boolean writeToFile)
    {
        this.crates = crates;
        crates.setPlacedCount(crates.getPlacedCount() + 1);
        setCratesEnabled(CrateUtils.isCrateUsable(crates));

        if (CrateUtils.isCrateUsable(this))
        {
            setupDisplay();
            setupHolo(crates);

            getCrates().getCs().getDcp().fixHologram(this);

            if (writeToFile)
            {
                setPlacedTime(System.currentTimeMillis());
                writeToFile();
            }
        }
    }

    public void setupDisplay()
    {
        getCrates().getCs().getDcp().place(this);
    }


    public void setupHolo(Crate crates)
    {
        setCholo(crates.getCs().getCholoCopy().clone());
        Location dupeLoc = getL().clone();
        dupeLoc.setY(dupeLoc.getY() + .5);
        getCholo().setDh(getCholo().createHologram(this, dupeLoc));
    }

    public void tick(CrateState cs)
    {
        if (isCratesEnabled())
        {
            getCrates().tick(getL(), cs);
            //getCrates().getCs().getCh().tick(null, getL(), cs, !getCrates().isMultiCrate());
            getCholo().getDh().tick();
        }

        if (crates.getCs().getOt().equals(ObtainType.LUCKYCHEST))
        {
            int num = (int) cc.getSettings().getConfigValues().get(SettingsValues.LUCKYCHEST_DESPAWN.getPath()) * 60;
            if (num > 0 && ((System.currentTimeMillis() - getPlacedTime()) / 1000) > num)
            {
                delete();
            }
        }
    }

    public static boolean crateExistsAt(CustomCrates cc, Location l)
    {
        return getPlacedCrates().containsKey(l.getBlock().getLocation());
    }

    public static PlacedCrate get(CustomCrates cc, Location l)
    {
        Location bl = l.getBlock().getLocation();
        return getPlacedCrates().containsKey(bl) ? getPlacedCrates().get(bl) : new PlacedCrate(cc, l);
    }

    public static void clearLoaded()
    {
        getPlacedCrates().clear();
        setPlacedCrates(new HashMap<Location, PlacedCrate>());
    }

    public CHolograms getCholo()
    {
        return cholo;
    }

    public void setCholo(CHolograms cholo)
    {
        this.cholo = cholo;
    }

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }

    public Crate getCrates()
    {
        return crates;
    }

    public void setCh(Crate crates)
    {
        this.crates = crates;
    }

    public Location getL()
    {
        return l;
    }

    public void setL(Location l)
    {
        this.l = l;
    }

    public boolean isUsed()
    {
        return used;
    }

    public void setUsed(boolean used)
    {
        this.used = used;
    }

    public static HashMap<Location, PlacedCrate> getPlacedCrates()
    {
        return placedCrates;
    }

    public static void setPlacedCrates(HashMap<Location, PlacedCrate> placedCrates)
    {
        PlacedCrate.placedCrates = placedCrates;
    }

    public boolean isCratesEnabled()
    {
        return isCratesEnabled;
    }

    public void setCratesEnabled(boolean cratesEnabled)
    {
        isCratesEnabled = cratesEnabled;
    }

    public Long getPlacedTime()
    {
        return placedTime;
    }

    public void setPlacedTime(Long placedTime)
    {
        this.placedTime = placedTime;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public boolean isDeleted()
    {
        return deleted;
    }
}
