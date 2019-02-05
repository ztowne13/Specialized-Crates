package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.CrateSettings;
import me.ztowne13.customcrates.crates.CrateSettingsBuilder;
import me.ztowne13.customcrates.crates.CrateState;
import me.ztowne13.customcrates.utils.FileUtil;

public abstract class CSetting 
{
	Crate crates;
	CustomCrates cc;
	
	public CSetting(Crate crates, CustomCrates cc)
	{
		this.crates = crates;
		this.cc = cc;
	}
	
	public abstract void loadFor(CrateSettingsBuilder csb, CrateState cs);

	public abstract void saveToFile();

	public FileUtil getFu()
	{
		return getCrates().getCs().getFu();
	}


	public CrateSettings up()
	{
		return getCrates().getCs();
	}

	public Crate getCrates()
	{
		return crates;
	}

	public void setCrates(Crate crates) {
		this.crates = crates;
	}

	public CustomCrates getCc()
	{
		return cc;
	}

	public void setCc(CustomCrates cc)
	{
		this.cc = cc;
	}
}
