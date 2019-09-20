package me.ztowne13.customcrates.external.holograms;

import me.ztowne13.customcrates.CustomCrates;
import org.bukkit.Location;

public abstract class Hologram
{
    CustomCrates customCrates;

    String name;
    Location location;

    public Hologram(CustomCrates cc, String name, Location location)
    {
        this.customCrates = cc;
        this.name = name;
        this.location = location;
    }

    public abstract void addLine(String line);

    public abstract void setLine(int i, String line);

    public abstract void update();

    public void setLocation(Location location)
    {
        this.location = location;
    }

    public CustomCrates getCustomCrates()
    {
        return customCrates;
    }

    public String getName()
    {
        return name;
    }

    public Location getLocation()
    {
        return location;
    }
}
