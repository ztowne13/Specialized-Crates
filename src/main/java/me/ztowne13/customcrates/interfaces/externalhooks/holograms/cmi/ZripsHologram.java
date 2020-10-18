package me.ztowne13.customcrates.interfaces.externalhooks.holograms.cmi;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Containers.CMILocation;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import com.Zrips.CMI.Modules.Holograms.HologramManager;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import org.bukkit.Location;

public class ZripsHologram extends Hologram {
    private final HologramManager hologramManager;
    private final CMIHologram cmiHologram;

    public ZripsHologram(SpecializedCrates instance, Location location) {
        super(instance, location);
        hologramManager = CMI.getInstance().getHologramManager();
        this.cmiHologram =
                new CMIHologram(
                        "Crate(" + location.getWorld().getName() + ","
                                + location.getBlockX() + ","
                                + location.getBlockY() + ","
                                + location.getBlockZ() + ")", new CMILocation(location));

        CMI.getInstance().getHologramManager().addHologram(this.cmiHologram);
        this.cmiHologram.update();
    }

    @Override
    public void addLine(String line) {
        cmiHologram.getLines().add(line);
        cmiHologram.refresh();
    }

    @Override
    public void setLine(int i, String line) {
        if (i >= cmiHologram.getLines().size()) {
            addLine(line);
        } else {
            cmiHologram.getLines().set(i, line);
            cmiHologram.refresh();
        }
    }

    @Override
    public void delete() {
        hologramManager.removeHolo(cmiHologram);
        cmiHologram.disable();
    }

    @Override
    public void setLocation(Location location) {
        super.setLocation(location);
        cmiHologram.setLoc(location);
        cmiHologram.refresh();
    }
}
