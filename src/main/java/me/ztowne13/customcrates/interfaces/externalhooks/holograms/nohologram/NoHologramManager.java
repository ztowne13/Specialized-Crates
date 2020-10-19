package me.ztowne13.customcrates.interfaces.externalhooks.holograms.nohologram;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.HologramManager;
import org.bukkit.Location;

public class NoHologramManager extends HologramManager {
    private final Hologram noHologram;

    public NoHologramManager(SpecializedCrates instance) {
        super(instance);
        this.noHologram = new Hologram(instance, null) {
            @Override
            public void addLine(String line) {
                // EMPTY
            }

            @Override
            public void setLine(int i, String line) {
                // EMPTY
            }

            @Override
            public void delete() {
                // EMPTY
            }
        };
    }

    @Override
    public Hologram newHologram(Location location) {
        return noHologram;
    }
}
