package me.ztowne13.customcrates.crates;

import me.ztowne13.customcrates.SettingsValue;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.CHologram;
import me.ztowne13.customcrates.crates.options.ObtainType;
import me.ztowne13.customcrates.utils.ChatUtils;
import me.ztowne13.customcrates.utils.CrateUtils;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages individual placed crates.
 */
public class PlacedCrate {
    private static Map<Location, PlacedCrate> placedCrates = new HashMap<>();

    private final SpecializedCrates instance;
    private Crate crate;

    private boolean isCratesEnabled;
    private boolean deleted = false;

    private CHologram hologram;

    private Location location;
    private Long placedTime;
    private boolean used = false;

    public PlacedCrate(SpecializedCrates instance, Location location) {
        this.instance = instance;
        this.location = location;

        getPlacedCrates().put(location, this);
    }

    public static boolean crateExistsAt(Location l) {
        return getPlacedCrates().containsKey(l.getBlock().getLocation());
    }

    public static PlacedCrate get(SpecializedCrates cc, Location l) {
        Location bl;
        try {
            bl = l.getBlock().getLocation();
        } catch (Exception exc) {
            ChatUtils.log("A crate is trying to be placed in an ungenerated chunk or world. Deleting that placed instance.");
            return null;
        }

        return getPlacedCrates().containsKey(bl) ? getPlacedCrates().get(bl) : new PlacedCrate(cc, l);
    }

    public static void clearLoaded() {
        getPlacedCrates().clear();
        setPlacedCrates(new HashMap<>());
    }

    public static Map<Location, PlacedCrate> getPlacedCrates() {
        return placedCrates;
    }

    public static void setPlacedCrates(Map<Location, PlacedCrate> placedCrates) {
        PlacedCrate.placedCrates = placedCrates;
    }

    public void delete() {
        instance.getActiveCratesFile().get().set(LocationUtils.locToString(getLocation()), null);
        instance.getActiveCratesFile().save();
        getHologram().getDynamicHologram().delete();
        getCrate().getSettings().getPlaceholder().remove(this);
        getPlacedCrates().remove(getLocation());
        deleted = true;
    }

    public void writeToFile() {
        instance.getActiveCratesFile().get().set(LocationUtils.locToString(getLocation()) + ".crate", getCrate().getName());
        instance.getActiveCratesFile().get().set(LocationUtils.locToString(getLocation()) + ".placedTime", getPlacedTime());
        instance.getActiveCratesFile().save();
    }

    public void rename(String newCrateName) {
        instance.getActiveCratesFile().get().set(LocationUtils.locToString(getLocation()) + ".crate", newCrateName);
        instance.getActiveCratesFile().save();
    }

    public void setup(Crate crates, boolean writeToFile) {
        setup(crates, writeToFile, true);
    }

    public void setup(Crate crates, boolean writeToFile, boolean createdByPlayer) {
        this.crate = crates;
        crates.setPlacedCount(crates.getPlacedCount() + 1);
        setCratesEnabled(CrateUtils.isCrateUsable(crates));

        if (CrateUtils.isCrateUsable(this)) {
            setupDisplay();
            setupHolo(crates, createdByPlayer);

            getCrate().getSettings().getPlaceholder().fixHologram(this);

            if (writeToFile) {
                setPlacedTime(System.currentTimeMillis());
                writeToFile();
            }
        }
    }

    public void setupDisplay() {
        getCrate().getSettings().getPlaceholder().place(this);
    }

    public void setupHolo(Crate crates, boolean createdByPlayer) {
        setHologram(crates.getSettings().getHologram().clone());
        if (!createdByPlayer) {
            getHologram().setCreatedByPlayer(false);
        }

        Location dupeLoc = getLocation().clone();
        dupeLoc.setY(dupeLoc.getY() + .5);
        getHologram().setDynamicHologram(getHologram().createHologram(this, dupeLoc));
    }

    public void tick(CrateState cs) {
        if (isCratesEnabled()) {
            getCrate().tick(getLocation(), cs, null, null);
            //getCrates().getCs().getCh().tick(null, getL(), cs, !getCrates().isMultiCrate());
            getHologram().getDynamicHologram().tick();
        }

        if (crate.getSettings().getObtainType().equals(ObtainType.LUCKYCHEST)) {
            int num = (int) instance.getSettings().getConfigValues().get(SettingsValue.LUCKYCHEST_DESPAWN.getPath()) * 60;
            if (num > 0 && ((System.currentTimeMillis() - getPlacedTime()) / 1000) > num) {
                delete();
            }
        }
    }

    public CHologram getHologram() {
        return hologram;
    }

    public void setHologram(CHologram hologram) {
        this.hologram = hologram;
    }

    public Crate getCrate() {
        return crate;
    }

    public void setCrate(Crate crate) {
        this.crate = crate;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public boolean isCratesEnabled() {
        return isCratesEnabled;
    }

    public void setCratesEnabled(boolean cratesEnabled) {
        isCratesEnabled = cratesEnabled;
    }

    public Long getPlacedTime() {
        return placedTime;
    }

    public void setPlacedTime(Long placedTime) {
        this.placedTime = placedTime;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
