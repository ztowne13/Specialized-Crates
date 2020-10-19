package me.ztowne13.customcrates.interfaces.externalhooks.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Location;

public abstract class Hologram {
    protected final SpecializedCrates instance;

    protected Location location;

    public Hologram(SpecializedCrates instance, Location location) {
        this.instance = instance;
        this.location = location;
    }

    public abstract void addLine(String line);

    public abstract void setLine(int i, String line);

    public abstract void delete();

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
