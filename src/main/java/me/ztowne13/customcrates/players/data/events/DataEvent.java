package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.players.PlayerDataManager;

/**
 * Created by ztowne13 on 8/5/15.
 */
public abstract class DataEvent
{
    SpecializedCrates cc;

    public DataEvent(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public abstract void addTo(PlayerDataManager pdm);

    public abstract String getFormatted();

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }
}
