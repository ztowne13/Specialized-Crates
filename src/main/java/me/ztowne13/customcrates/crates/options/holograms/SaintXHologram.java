package me.ztowne13.customcrates.crates.options.holograms;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

/**
 * Created by ztowne13 on 2/14/16.
 */
public class SaintXHologram extends DynamicHologram {
    HologramManager hm;
    Hologram hologram;
    double defaultYOffSet = -.2;
    Location l;

    public SaintXHologram(SpecializedCrates cc, PlacedCrate cm) {
        super(cc, cm);

        this.hm = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
    }

    @Override
    public void create(Location l) {
        this.l = l;
        UUID uuid = UUID.randomUUID();
        this.hologram = new Hologram(uuid.toString(), l);
        hm.addActiveHologram(hologram);
        this.hologram.spawn();
        teleport(l);
    }

    @Override
    public void addLine(String line) {
        hologram.addLine(new TextLine(hologram, line));
        if (hologram.getLine(0).getRaw().equalsIgnoreCase("THIS LINE SHOULD BE GETTING REMOVED")) {
            hologram.removeLine(hologram.getLine(0));
        }
    }

    @Override
    public void delete() {
        if (hologram != null && hm != null) {
            hm.removeActiveHologram(hologram);
            hm.deleteHologram(hologram);
        }
    }

    @Override
    public void teleport(Location l) {
        l.setY(l.getY() + getDefaultYOffSet() + getCm().getHologram().getHologramOffset());
        hologram.teleport(LocationUtils.getLocationCentered(l));
    }

    @Override
    public void setLine(int lineNum, String line) {
        hologram.removeLine(hologram.getLine(lineNum));
        hologram.addLine(new TextLine(hologram, line), lineNum);
    }

    public double getDefaultYOffSet() {
        return defaultYOffSet;
    }

    public void setDefaultYOffSet(double defaultYOffSet) {
        this.defaultYOffSet = defaultYOffSet;
    }
}
