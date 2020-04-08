package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.files.FileHandler;

public abstract class CSetting
{
    Crate crates;
    SpecializedCrates cc;

    public CSetting(Crate crates, SpecializedCrates cc)
    {
        this.crates = crates;
        this.cc = cc;
    }

    public abstract void loadFor(CrateSettingsBuilder csb, CrateState cs);

    public abstract void saveToFile();

    public FileHandler getFu()
    {
        return getCrate().getSettings().getFileHandler();
    }


    public CrateSettings up()
    {
        return getCrate().getSettings();
    }

    public Crate getCrate()
    {
        return crates;
    }

    public void setCrates(Crate crates)
    {
        this.crates = crates;
    }

    public SpecializedCrates getCc()
    {
        return cc;
    }

    public void setCc(SpecializedCrates cc)
    {
        this.cc = cc;
    }
}
