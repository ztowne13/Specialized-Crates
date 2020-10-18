package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.SpecializedCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.interfaces.files.FileHandler;

public abstract class CSetting {
    protected Crate crates;
    protected final SpecializedCrates instance;

    public CSetting(Crate crates, SpecializedCrates instance) {
        this.crates = crates;
        this.instance = instance;
    }

    public abstract void loadFor(CrateSettingsBuilder csb, CrateState cs);

    public abstract void saveToFile();

    public FileHandler getFileHandler() {
        return getCrate().getSettings().getFileHandler();
    }


    public CrateSettings getSettings() {
        return getCrate().getSettings();
    }

    public Crate getCrate() {
        return crates;
    }

    public void setCrates(Crate crates) {
        this.crates = crates;
    }
}
