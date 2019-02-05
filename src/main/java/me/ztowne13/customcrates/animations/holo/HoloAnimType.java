package me.ztowne13.customcrates.animations.holo;

import me.ztowne13.customcrates.CustomCrates;
import me.ztowne13.customcrates.crates.options.holograms.DynamicHologram;

public enum HoloAnimType 
{
	SINGLELINE_CHANGE,

	TEXT_CHANGE;

	//MULTILINE_CHANGE;
	
	public HoloAnimation getAsHoloAnimation(CustomCrates cc, DynamicHologram dh)
	{
		switch(this)
		{
			case SINGLELINE_CHANGE:
			case TEXT_CHANGE:
				return new TextChangeAnimation(cc, dh);
			default:
				return null;
		}
	}
}
