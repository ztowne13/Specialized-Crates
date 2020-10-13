package me.ztowne13.customcrates.interfaces.externalhooks.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Location;

public abstract class Hologram {
    private final SpecializedCrates customCrates;

    protected Location location;

    public Hologram(SpecializedCrates customCrates, Location location) {
        this.customCrates = customCrates;
        this.location = location;
    }

    public abstract void addLine(String line);

    public abstract void setLine(int i, String line);

    public abstract void update();

    public abstract void delete();

    public SpecializedCrates getCustomCrates() {
        return customCrates;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
