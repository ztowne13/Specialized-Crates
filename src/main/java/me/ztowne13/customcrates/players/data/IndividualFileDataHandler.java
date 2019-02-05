package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.players.PlayerManager;
import me.ztowne13.customcrates.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;

public class IndividualFileDataHandler extends DataHandler
{
	FileUtil fu;
	FileConfiguration fc;

	public IndividualFileDataHandler(PlayerManager pm) 
	{
		super(pm);
		cc.getDu().log("Loading individual file data handler for " + pm.getP().getName());
		this.fu = new FileUtil(pm.getCc(), pm.getP().getUniqueId().toString() + ".stats", "/PlayerStats/", false, false, false);
		this.fc = getFu().get();
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
		getFu().save();
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

	public FileUtil getFu()
	{
		return fu;
	}

	public void setFu(FileUtil fu)
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
