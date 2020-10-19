package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimType;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimation;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import me.ztowne13.customcrates.utils.LocationUtils;
import org.bukkit.Location;

import java.util.UUID;

public class DynamicHologram {
    private final UUID uuid;

    private final SpecializedCrates instance;
    private PlacedCrate placedCrate;

    private Hologram hologram;
    private HoloAnimation holoAnimation;

    private boolean displayingRewardHologram = false;

    public DynamicHologram(SpecializedCrates instance, PlacedCrate placedCrate) {
        this.uuid = UUID.randomUUID();

        this.instance = instance;
        this.placedCrate = placedCrate;

        if (placedCrate.getHologram().getHoloAnimType() != null && placedCrate.getHologram().getHoloAnimType() != HoloAnimType.NONE) {
            setHoloAnimation(placedCrate.getHologram().getHoloAnimType().getAsHoloAnimation(instance, this));
        }
    }

    public void create(Location location) {
        location.setY(location.getY() + getPlacedCrate().getHologram().getHologramOffset() - 1);
        location = LocationUtils.getLocationCentered(location);
        this.hologram = instance.getHologramManager().createHologram(location);
    }

    public void addLine(String line) {
        this.hologram.addLine(line);
    }

    public void setLine(int lineNum, String line) {
        this.hologram.setLine(lineNum, line);
    }

    public void delete() {
        instance.getHologramManager().deleteHologram(this.hologram);
        this.hologram = null;
    }

    public void teleport(Location location) {
        location.setY(location.getY() + getPlacedCrate().getHologram().getHologramOffset());
        this.hologram.setLocation(LocationUtils.getLocationCentered(location));
    }

    public void tick() {
        if (getHoloAnimation() != null && !placedCrate.getHologram().getPrefixes().isEmpty()) {
            getHoloAnimation().tick();
        }
    }

    public PlacedCrate getPlacedCrate() {
        return placedCrate;
    }

    public void setPlacedCrate(PlacedCrate placedCrate) {
        this.placedCrate = placedCrate;
    }

    public HoloAnimation getHoloAnimation() {
        return holoAnimation;
    }

    public void setHoloAnimation(HoloAnimation holoAnimation) {
        this.holoAnimation = holoAnimation;
    }

    public boolean getDisplayingRewardHologram() {
        return displayingRewardHologram;
    }

    public void setDisplayingRewardHologram(boolean displayingRewardHologram) {
        this.displayingRewardHologram = displayingRewardHologram;
    }

    public UUID getUuid() {
        return uuid;
    }
}
