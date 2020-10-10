package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramNMS;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;

@Deprecated
public class NativeHologram extends DynamicHologram {
    HologramManager hologramManager;
    Hologram hologram;
    Location l;

    public NativeHologram(SpecializedCrates cc, PlacedCrate placedCrate) {
        super(cc, placedCrate);
        this.hologramManager = getCc().getHologramManager();
    }

    @Override
    public void create(Location l) {
        this.l = l;
//        this.hologram = hologramManager.createHologram("test", l);
//        teleport(l);
    }

    @Override
    public void addLine(String line) {
        if (hologram == null) {
            this.hologram = hologramManager.createHologram("test", l);
            teleport(l);
        }

        hologram.addLine(line);

        if (hologram instanceof HologramNMS) {
            ((HologramNMS) hologram).displayTo(Bukkit.getOnlinePlayers().iterator().next());
        }
    }

    @Override
    public void setLine(int lineNum, String line) {
        if (hologram == null) {
            this.hologram = hologramManager.createHologram("test", l);
            teleport(l);
        }

        hologram.setLine(lineNum, line);
        if (hologram instanceof HologramNMS) {
            ((HologramNMS) hologram).displayTo(Bukkit.getOnlinePlayers().iterator().next());
        }
    }

    @Override
    public void delete() {
        if (hologram != null) {
            hologramManager.deleteHologram(hologram);
        }
    }

    @Override
    public void teleport(Location l) {
        if (hologram != null) {
            hologram.setLocation(LocationUtils.getLocationCentered(l.clone()));
            if (hologram instanceof HologramNMS) {
                ((HologramNMS) hologram).displayTo(Bukkit.getOnlinePlayers().iterator().next());
            }
        }
    }
}
