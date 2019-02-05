package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.players.PlayerManager;

public abstract class DataHandler 
{
	CustomCrates cc;
	
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

	public CustomCrates getCc() 
	{
		return cc;
	}

	public void setCc(CustomCrates cc)
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
