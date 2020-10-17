package me.ztowne13.customcrates.crates.options.holograms.animations;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.utils.ChatUtils;

/**
 * Animation that modifies the hologram text directly to what was configured in the config
 */
public class TextChangeAnimation extends HoloAnimation {
    private String last = "";
    private int count = 0;

    public TextChangeAnimation(SpecializedCrates instance, DynamicHologram dynamicHologram) {
        super(instance, dynamicHologram);
    }

    @Override
    public void tick() {
        // One line is needed to be replaced, if it doesn't exist: add it.
        if (dynamicHologram.getPlacedCrate().getHologram().getLineCount() == 0) {
            dynamicHologram.getPlacedCrate().getHologram().setLineCount(dynamicHologram.getPlacedCrate().getHologram().getLineCount() + 1);
            dynamicHologram.getPlacedCrate().getHologram().addLine("");
        }

        setIntTicks(getIntTicks() + 1);
        if (getIntTicks() == getHolograms().getSpeed()) {
            setIntTicks(0);
            if (!getDynamicHologram().getDisplayingRewardHologram()) {
                update();
            }
        }
    }

    public void update() {
        update(false);
    }

    @Override
    public void update(boolean force) {
        if (count >= getHolograms().getPrefixes().size()) {
            count = 0;
        }

        String s = getHolograms().getPrefixes().get(count);

        if (!getLast().equals(s) || force) {
            getDynamicHologram().setLine(0, ChatUtils.toChatColor(s));
        }

        setLast(s);
        count++;
    }

    @Override
    public void stop() {
        // EMPTY
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }
}
