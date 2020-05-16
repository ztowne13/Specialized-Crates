package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class FlatFileDataHandler extends DataHandler
{
    public static ArrayList<FlatFileDataHandler> toSave = new ArrayList<>();

    FileHandler fu;
    FileConfiguration fc;

    public FlatFileDataHandler(PlayerManager pm)
    {
        super(pm);
        cc.getDu().log("Loading flat file data handler for " + pm.getP().getName());
        this.fu = new FileHandler(getCc(), "PlayerData.db", false, false);
        this.fc = getFileHandler().get();
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

        if(!toSave.contains(this))
            toSave.add(this);
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

    public FileHandler getFileHandler()
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
