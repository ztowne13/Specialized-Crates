package me.ztowne13.customcrates.animations.holo;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;
import me.ztowne13.customcrates.utils.ChatUtils;

/**
 * Animation that modifies the hologram text directly to what was configured in the config
 */
public class TextChangeAnimation extends HoloAnimation
{
    String last = "";
    int count = 0;

    public TextChangeAnimation(SpecializedCrates cc, DynamicHologram dh)
    {
        super(cc, dh);
    }

    @Override
    public void tick()
    {
        setIntTicks(getIntTicks() + 1);
        if (getIntTicks() == getCh().getSpeed())
        {
            setIntTicks(0);
            if (!getDh().getDisplayingRewardHologram())
            {
                update();
            }
        }
    }

    public void update()
    {
        update(false);
    }

    @Override
    public void update(boolean force)
    {
        if (count >= getCh().getPrefixes().size())
        {
            count = 0;
        }

        String s = getCh().getPrefixes().get(count);

        if (!getLast().equals(s) || force)
        {
            getDh().setLine(0, ChatUtils.toChatColor(s));
        }

        setLast(s);
        count++;
    }

    @Override
    public void stop()
    {

    }

    public String getLast()
    {
        return last;
    }

    public void setLast(String last)
    {
        this.last = last;
    }
}
