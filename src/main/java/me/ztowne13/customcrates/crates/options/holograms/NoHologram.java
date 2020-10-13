package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import org.bukkit.Location;

/**
 * Created by ztowne13 on 2/14/16.
 */
@Deprecated
public class NoHologram extends DynamicHologram {
    public NoHologram(SpecializedCrates cc, PlacedCrate cm) {
        super(cc, cm);
    }

    public void create(Location l) {

    }

    public void addLine(String line) {

    }

    public void setLine(int lineNum, String line) {

    }

    public void delete() {

    }

    public void teleport(Location l) {

    }
}
