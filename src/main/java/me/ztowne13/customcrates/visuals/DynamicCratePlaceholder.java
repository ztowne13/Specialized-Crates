package me.ztowne13.customcrates.visuals;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.PlacedCrate;

/**
 * Created by ztowne13 on 2/24/16.
 */
public abstract class DynamicCratePlaceholder
{
	CustomCrates cc;

	public DynamicCratePlaceholder(CustomCrates cc)
	{
		this.cc = cc;
	}

	public CustomCrates getCc()
	{
		return cc;
	}

	public abstract void place(PlacedCrate cm);

	public abstract void remove(PlacedCrate cm);

	public abstract void setType(Object obj);

	public abstract String getType();

	public abstract void fixHologram(PlacedCrate cm);

	public abstract String toString();
}
