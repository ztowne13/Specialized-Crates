package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.crates.options.rewards.Reward;
import me.ztowne13.customcrates.players.PlayerDataManager;

import java.util.HashMap;

/**
 * Created by ztowne13 on 8/5/15.
 */
public class RewardLimitEvent extends DataEvent
{
    public Reward r;
    HashMap<String,Integer> crateUses = new HashMap<>();

    public RewardLimitEvent(Reward r)
    {
        super(r.getCc());
        this.r = r;

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
        return "";
    }
}
