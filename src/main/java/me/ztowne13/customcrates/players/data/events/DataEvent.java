package me.ztowne13.customcrates.players.data.events;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.players.PlayerDataManager;

/**
 * Created by ztowne13 on 8/5/15.
 */
public abstract class DataEvent
{
    CustomCrates cc;

    public DataEvent(CustomCrates cc)
    {
        this.cc = cc;
    }

    public abstract void addTo(PlayerDataManager pdm);

    public abstract String getFormatted();

    public CustomCrates getCc()
    {
        return cc;
    }

    public void setCc(CustomCrates cc)
    {
        this.cc = cc;
    }
}
