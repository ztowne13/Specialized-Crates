package me.ztowne13.customcrates.crates.options;

import me.ztowne13.customcrates.crates.Crate;

public enum ObtainType
{
	STATIC(true, false),
	
	DYNAMIC(false, true),
	
	LUCKYCHEST(false, false);
	
	boolean isStatic;
	boolean canPlace;
	
	ObtainType(boolean isStatic, boolean canPlace)
	{
		this.isStatic = isStatic;
		this.canPlace = canPlace;
	}

	public static boolean getReqKey(Crate crates)
	{
		return crates.getCs().isRequireKey();
	}

	public boolean isStatic() 
	{
		return isStatic;
	}

	public void setStatic(boolean isStatic) 
	{
		this.isStatic = isStatic;
	}

	public boolean isCanPlace() 
	{
		return canPlace;
	}

	public void setCanPlace(boolean canPlace) 
	{
		this.canPlace = canPlace;
	}

}
