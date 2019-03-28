package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.FileHandler;
import org.bukkit.configuration.file.FileConfiguration;

public class FlatFileDataHandler extends DataHandler
{
    FileHandler fu;
    FileConfiguration fc;

    public FlatFileDataHandler(PlayerManager pm)
    {
        super(pm);
        cc.getDu().log("Loading flat file data handler for " + pm.getP().getName());
        this.fu = new FileHandler(getCc(), "PlayerData.db", false, false);
        this.fc = getFu().get();
    }

    @Override
    public boolean load()
    {

        return false;
    }

    @Override
    public Object get(String value)
    {
        return getFc().get(toPath(value));
    }

    @Override
    public void write(String value, String toWrite)
    {
        getFc().set(toPath(value), toWrite);
        getFu().save();
    }

    @Override
    public boolean hasDataPath()
    {
        return getFc().contains(getUuid());
    }

    @Override
    public boolean hasDataValue(String value)
    {
        return getFc().contains(toPath(value));
    }

    public String toPath(String value)
    {
        return getUuid() + "." + value;
    }

    public FileHandler getFu()
    {
        return fu;
    }

    public void setFu(FileHandler fu)
    {
        this.fu = fu;
    }

    public FileConfiguration getFc()
    {
        return fc;
    }

    public void setFc(FileConfiguration fc)
    {
        this.fc = fc;
    }
}
