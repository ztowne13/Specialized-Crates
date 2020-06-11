package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SettingsValues;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.CHolograms;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.utils.ChatUtils;
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

    SpecializedCrates cc;
    Crate crates;

    boolean isCratesEnabled;
    boolean deleted = false;

    CHolograms cholo;

    Location l;
    Long placedTime;
    boolean used = false;

    public PlacedCrate(SpecializedCrates cc, Location l)
    {
        this.cc = cc;
        this.l = l;

        getPlacedCrates().put(l, this);
    }


    public void delete()
    {
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()), null);
        getCc().getActivecratesFile().save();
        getHologram().getDh().delete();
        getCrate().getSettings().getPlaceholder().remove(this);
        getPlacedCrates().remove(getL());
        deleted = true;
    }

    public void writeToFile()
    {
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()) + ".crate", getCrate().getName());
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()) + ".placedTime", getPlacedTime());
        getCc().getActivecratesFile().save();
    }

    public void rename(String newCrateName)
    {
        getCc().getActivecratesFile().get().set(LocationUtils.locToString(getL()) + ".crate", newCrateName);
        getCc().getActivecratesFile().save();
    }

    public void setup(Crate crates, boolean writeToFile)
    {
        setup(crates, writeToFile, true);
    }

    public void setup(Crate crates, boolean writeToFile, boolean createdByPlayer)
    {
        this.crates = crates;
        crates.setPlacedCount(crates.getPlacedCount() + 1);
        setCratesEnabled(CrateUtils.isCrateUsable(crates));

        if (CrateUtils.isCrateUsable(this))
        {
            setupDisplay();
            setupHolo(crates, createdByPlayer);

            getCrate().getSettings().getPlaceholder().fixHologram(this);

            if (writeToFile)
            {
                setPlacedTime(System.currentTimeMillis());
                writeToFile();
            }
        }
    }

    public void setupDisplay()
    {
        getCrate().getSettings().getPlaceholder().place(this);
    }


    public void setupHolo(Crate crates, boolean createdByPlayer)
    {
        setCholo(crates.getSettings().getHologram().clone());
        if(!createdByPlayer) {
            getHologram().setCreatedByPlayer(false);
        }

        Location dupeLoc = getL().clone();
        dupeLoc.setY(dupeLoc.getY() + .5);
        getHologram().setDh(getHologram().createHologram(this, dupeLoc));
    }

    public void tick(CrateState cs)
    {
        if (isCratesEnabled())
        {
            getCrate().tick(getL(), cs, null, null);
            //getCrates().getCs().getCh().tick(null, getL(), cs, !getCrates().isMultiCrate());
            getHologram().getDh().tick();
        }

        if (crates.getSettings().getObtainType().equals(ObtainType.LUCKYCHEST))
        {
            int num = (int) cc.getSettings().getConfigValues().get(SettingsValues.LUCKYCHEST_DESPAWN.getPath()) * 60;
            if (num > 0 && ((System.currentTimeMillis() - getPlacedTime()) / 1000) > num)
            {
                delete();
            }
        }
    }

    public static boolean crateExistsAt(SpecializedCrates cc, Location l)
    {
        return getPlacedCrates().containsKey(l.getBlock().getLocation());
    }

    public static PlacedCrate get(SpecializedCrates cc, Location l)
    {
        Location bl;
        try
        {
            bl = l.getBlock().getLocation();
        }
        catch (Exception exc)
        {
            ChatUtils.log("A crate is trying to be placed in an ungenerated chunk or world. Deleting that placed instance.");
            return null;
        }

        return getPlacedCrates().containsKey(bl) ? getPlacedCrates().get(bl) : new PlacedCrate(cc, l);
    }

    public static void clearLoaded()
    {
        getPlacedCrates().clear();
        setPlacedCrates(new HashMap<Location, PlacedCrate>());
    }

    public CHolograms getHologram()
    {
        return cholo;
    }

    public void setCholo(CHolograms cholo)
    {
        this.cholo = cholo;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public Crate getCrate()
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
