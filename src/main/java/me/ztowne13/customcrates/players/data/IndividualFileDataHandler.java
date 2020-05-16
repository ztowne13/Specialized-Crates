package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.interfaces.files.FileHandler;
import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class IndividualFileDataHandler extends DataHandler
{
    public static ArrayList<IndividualFileDataHandler> toSave = new ArrayList<>();

    FileHandler fu;
    FileConfiguration fc;

    public IndividualFileDataHandler(PlayerManager pm)
    {
        super(pm);
        cc.getDu().log("Loading individual file data handler for " + pm.getP().getName());
        this.fu = new FileHandler(pm.getCc(), pm.getP().getUniqueId().toString() + ".stats", "/PlayerStats/", false, false,
                false);
        this.fc = getFileHandler().get();
        cc.getDu().log(fu.getDataFile().getAbsolutePath());
    }

    @Override
    public boolean load()
    {
        return false;
    }

    @Override
    public Object get(String value)
    {
        return getFc().get(value);
    }

    @Override
    public void write(String value, String toWrite)
    {
        getFc().set(value, toWrite);

        if(!toSave.contains(this))
            toSave.add(this);
    }

    @Override
    public boolean hasDataValue(String value)
    {
        return getFc().contains(value);
    }

    @Override
    public boolean hasDataPath()
    {
        return true;
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
