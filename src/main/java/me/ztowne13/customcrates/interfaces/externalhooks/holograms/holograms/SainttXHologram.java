package me.ztowne13.customcrates.interfaces.externalhooks.holograms.holograms;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class SainttXHologram extends Hologram {
    private final HologramManager hm;
    private final com.sainttx.holograms.api.Hologram hologram;
    private double defaultYOffSet = -.2;

    public SainttXHologram(SpecializedCrates instance, Location location) {
        super(instance, location);
        this.hm = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
        UUID uuid = UUID.randomUUID();
        this.hologram = new com.sainttx.holograms.api.Hologram(uuid.toString(), location);
        hm.addActiveHologram(hologram);
        this.hologram.spawn();
        setLocation(location);
    }

    @Override
    public void addLine(String line) {
        hologram.addLine(new TextLine(hologram, line));
        if (hologram.getLine(0).getRaw().equalsIgnoreCase("THIS LINE SHOULD BE GETTING REMOVED")) {
            hologram.removeLine(hologram.getLine(0));
        }
    }

    @Override
    public void setLine(int i, String line) {
        hologram.removeLine(hologram.getLine(i));
        hologram.addLine(new TextLine(hologram, line), i);
    }

    @Override
    public void delete() {
        if (hologram != null && hm != null) {
            hm.removeActiveHologram(hologram);
            hm.deleteHologram(hologram);
        }
    }

    @Override
    public void setLocation(Location location) {
        location.setY(location.getY() + getDefaultYOffSet());
        super.setLocation(LocationUtils.getLocationCentered(location));
        hologram.teleport(this.location);
    }

    public double getDefaultYOffSet() {
        return defaultYOffSet;
    }

    public void setDefaultYOffSet(double defaultYOffSet) {
        this.defaultYOffSet = defaultYOffSet;
    }
}
