package me.ztowne13.customcrates.crates.options.holograms.animations;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.utils.ChatUtils;

/**
 * Created by ztowne13 on 1/16/17.
 */
public class MultiLineChangeAnimation extends HoloAnimation {
    private String last = "";
    private int count = 0;

    public MultiLineChangeAnimation(SpecializedCrates instance, DynamicHologram dynamicHologram) {
        super(instance, dynamicHologram);
    }

    @Override
    public void tick() {
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


        for (int i = 0; i < getHolograms().getLines().size(); i++) {
            try {
                String currentLine = getHolograms().getLines().get(i);
                currentLine = s + currentLine;
                if (!getLast().equals(s)) {
                    getDynamicHologram().setLine(i, ChatUtils.toChatColor(currentLine));
                }
            } catch (Exception exc) {
                break;
            }
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
