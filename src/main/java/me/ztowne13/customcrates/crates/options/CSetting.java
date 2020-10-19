package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.files.FileHandler;

public abstract class CSetting {
    protected final SpecializedCrates instance;
    protected Crate crate;

    public CSetting(Crate crate, SpecializedCrates instance) {
        this.crate = crate;
        this.instance = instance;
    }

    public abstract void loadFor(CrateSettingsBuilder crateSettingsBuilder, CrateState crateState);

    public abstract void saveToFile();

    public FileHandler getFileHandler() {
        return getCrate().getSettings().getFileHandler();
    }


    public CrateSettings getSettings() {
        return getCrate().getSettings();
    }

    public Crate getCrate() {
        return crate;
    }

    public void setCrate(Crate crate) {
        this.crate = crate;
    }
}
