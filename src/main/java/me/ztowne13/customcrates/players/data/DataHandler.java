package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.players.PlayerManager;

public abstract class DataHandler
{
    SpecializedCrates cc;

    PlayerManager pm;
    String uuid;

    public DataHandler(PlayerManager pm)
    {
        this.cc = pm.getCc();
        this.pm = pm;
        this.uuid = pm.getP().getUniqueId().toString();
    }

    public abstract boolean load();

    public abstract Object get(String value);

    public abstract void write(String value, String toWrite);

    public abstract boolean hasDataPath();

    public abstract boolean hasDataValue(String value);

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }

    public PlayerManager getPm()
    {
        return pm;
    }

    public void setPm(PlayerManager pm)
    {
        this.pm = pm;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }


}
