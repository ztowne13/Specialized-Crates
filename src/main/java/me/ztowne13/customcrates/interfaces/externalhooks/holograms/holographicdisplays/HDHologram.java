package me.ztowne13.customcrates.interfaces.externalhooks.holograms.holographicdisplays;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import org.bukkit.Location;

import java.util.HashMap;

public class HDHologram extends Hologram {
    private final com.gmail.filoghost.holographicdisplays.api.Hologram hologram;
    private final HashMap<Integer, TextLine> lines = new HashMap<>();

    public HDHologram(SpecializedCrates customCrates, Location location) {
        super(customCrates, location);
        this.hologram = HologramsAPI.createHologram(customCrates, location);

        setLocation(location);
        hologram.setAllowPlaceholders(true);
    }

    @Override
    public void addLine(String line) {
        TextLine tl = hologram.appendTextLine(line);
        lines.put(hologram.size() - 1, tl);
    }

    @Override
    public void setLine(int i, String line) {
        if (lines.containsKey(i)) {
            lines.get(i).setText(line);
        } else {
            addLine(line);
        }
    }

    @Override
    public void delete() {
        if (hologram != null) {
            hologram.delete();
        }
    }

    @Override
    public void setLocation(Location l) {
        super.setLocation(l);
        hologram.teleport(l);
    }
}
