package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.players.PlayerDataManager;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class RewardLimitEvent extends DataEvent
{
    public Reward r;
    int addedUses;
    public int currentUses;
    int totalUses;

    public RewardLimitEvent(Reward r, int currentUses, int addedUses)
    {
        super(r.getCc());
        this.r = r;
        this.addedUses = addedUses;
        this.currentUses = currentUses;
        this.totalUses = r.getTotalUses();
        updateCurrentUses();
    }

    @Override
    public void addTo(PlayerDataManager pdm)
    {
        /*
        if (getTotalUses() > 0) {
            if (!(pdm.getRewardLimitEventByMatch(this) == null)) {
                RewardLimitEvent rle = pdm.getRewardLimitEventByMatch(this);
                pdm.removeRewardLimit(rle, pdm.removeStringFromList(rle.getFormatted(), pdm.getRewardLimits()));
            }
            pdm.addRewardLimit(this, pdm.addStringToList(getFormatted(), pdm.getRewardLimits()));
        }*/
    }

    @Override
    public String getFormatted()
    {
        return r.getRewardName() + ";" + getCurrentUses();
    }

    public boolean getCanUse()
    {
        return getCurrentUses() <= getTotalUses();
    }

    public void updateCurrentUses()
    {
        setCurrentUses(getCurrentUses() + getAddedUses());
    }

    public boolean matches(RewardLimitEvent rle)
    {
        return r.getRewardName().equalsIgnoreCase(rle.r.getRewardName());
    }

    public Reward getR()
    {
        return r;
    }

    public void setR(Reward r)
    {
        this.r = r;
    }

    public int getAddedUses()
    {
        return addedUses;
    }

    public void setAddedUses(int addedUses)
    {
        this.addedUses = addedUses;
    }

    public int getCurrentUses()
    {
        return currentUses;
    }

    public void setCurrentUses(int currentUses)
    {
        this.currentUses = currentUses;
    }

    public int getTotalUses()
    {
        return totalUses;
    }

    public void setTotalUses(int totalUses)
    {
        this.totalUses = totalUses;
    }
}
