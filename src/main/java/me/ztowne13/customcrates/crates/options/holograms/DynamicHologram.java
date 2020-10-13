package me.ztowne13.customcrates.crates.options.holograms;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimType;
import me.ztowne13.customcrates.crates.options.holograms.animations.HoloAnimation;
import me.ztowne13.customcrates.interfaces.externalhooks.holograms.Hologram;
import org.bukkit.Location;

import java.util.UUID;

public class DynamicHologram {
    UUID uuid;

    SpecializedCrates cc;
    PlacedCrate cm;

    Hologram hologram;
    HoloAnimation ha;

    boolean displayingRewardHologram = false;

    public DynamicHologram(SpecializedCrates cc, PlacedCrate cm) {
        this.uuid = UUID.randomUUID();

        this.cc = cc;
        this.cm = cm;

        if (cm.getHologram().getHat() != null && cm.getHologram().getHat() != HoloAnimType.NONE) {
            setHa(cm.getHologram().getHat().getAsHoloAnimation(cc, this));
        }
    }

    public void create(Location l) {
        this.hologram = getCc().getHologramManager().createHologram(l);
    }

    public void addLine(String line) {
        this.hologram.addLine(line);
    }

    public void setLine(int lineNum, String line) {
        this.hologram.setLine(lineNum, line);
    }

    public void delete() {
        getCc().getHologramManager().deleteHologram(this.hologram);
        this.hologram = null;
    }

    public void teleport(Location l) {
        this.hologram.setLocation(l);
    }

    public void tick() {
        if (getHa() != null) {
            if (!cm.getHologram().getPrefixes().isEmpty())
                getHa().tick();
        }
    }

    public PlacedCrate getCm() {
        return cm;
    }

    public void setCm(PlacedCrate cm) {
        this.cm = cm;
    }

    public HoloAnimation getHa() {
        return ha;
    }

    public void setHa(HoloAnimation ha) {
        this.ha = ha;
    }

    public SpecializedCrates getCc() {
        return cc;
    }

    public void setCc(SpecializedCrates cc) {
        this.cc = cc;
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
