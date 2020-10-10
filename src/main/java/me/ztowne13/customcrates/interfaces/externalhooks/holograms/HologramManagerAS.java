package me.ztowne13.customcrates.interfaces.externalhooks.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

@Deprecated
public class HologramManagerAS extends HologramManager {

    public HologramManagerAS(SpecializedCrates cc) {
        super(cc);
    }

    @Override
    public boolean isHologramEntity(ArmorStand stand) {
        for (Hologram hologram : getHolograms()) {
            if (((HologramAS) hologram).getStands().contains(stand))
                return true;
        }

        return false;
    }

    @Override
    public Hologram createHologram(String name, Location location) {
        HologramAS newHolo = new HologramAS(getCc(), name, location);
        getHolograms().add(newHolo);

        return newHolo;
    }

    @Override
    public void deleteHologram(Hologram hologram) {
        ((HologramAS) hologram).deleteStands();
        getHolograms().remove(hologram);
    }

    public SpecializedCrates getCc() {
        return cc;
    }
}
