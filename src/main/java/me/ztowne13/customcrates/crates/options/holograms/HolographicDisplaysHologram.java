package me.ztowne13.customcrates.crates.options.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

import java.util.HashMap;

@Deprecated
public class HolographicDisplaysHologram extends DynamicHologram {
    Hologram h;
    HashMap<Integer, TextLine> lines = new HashMap<>();

    public HolographicDisplaysHologram(SpecializedCrates cc, PlacedCrate cm) {
        super(cc, cm);
    }

    @Override
    public void create(Location l) {
        if (!getCm().isDeleted()) {
            setH(HologramsAPI.createHologram(getCc(), l));
            teleport(l);
        }

        h.setAllowPlaceholders(true);
    }

    @Override
    public void addLine(String line) {
        TextLine tl = getH().appendTextLine(line);
        lines.put(getH().size() - 1, tl);
    }

    @Override
    public void delete() {
        if (h != null) {
            getH().delete();
        }
    }

    @Override
    public void teleport(Location l) {
        l.setY(l.getY() + getCm().getHologram().getHologramOffset());
        getH().teleport(LocationUtils.getLocationCentered(l));
    }

    @Override
    public void setLine(int lineNum, String line) {
        if (lines.containsKey(lineNum)) {
            lines.get(lineNum).setText(line);
        } else {
            addLine(line);
        }
		/*getH().removeLine(lineNum);
		getH().insertTextLine(lineNum, line);*/
    }

    public Hologram getH() {
        return h;
    }

    public void setH(Hologram h) {
        this.h = h;
    }
}
