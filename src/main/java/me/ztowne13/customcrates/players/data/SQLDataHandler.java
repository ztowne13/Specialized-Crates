package me.ztowne13.customcrates.players.data;

import me.ztowne13.customcrates.players.PlayerManager;
import org.bukkit.Bukkit;

public class SQLDataHandler extends DataHandler
{

	public SQLDataHandler(PlayerManager pm)
	{
		super(pm);
	}

	@Override
	public boolean load()
	{
		return false;
	}

	@Override
	public Object get(String value)
	{
		return null;
	}

	@Override
	public void write(String value, String toWrite)
	{
		Bukkit.getScheduler().runTaskLaterAsynchronously(getCc(), new Runnable()
		{
			public void run()
			{

			}
		}, 0);
	}

	@Override
	public boolean hasDataValue(String value)
	{
		return false;
	}

	@Override
	public boolean hasDataPath()
	{
		return false;
	}

}
