package me.ztowne13.customcrates.crates.types;

import me.ztowne13.customcrates.crates.Crate;
import me.ztowne13.customcrates.crates.types.animations.*;
import me.ztowne13.customcrates.crates.types.animations.csgo.CSGOAnimation;
import me.ztowne13.customcrates.crates.types.animations.discover.DiscoverAnimation;
import me.ztowne13.customcrates.crates.types.animations.enclosement.EnclosementAnimation;
import me.ztowne13.customcrates.crates.types.animations.menu.MenuAnimation;
import me.ztowne13.customcrates.crates.types.animations.roulette.RouletteAnimation;

public enum CrateType
{
		INV_ROULETTE("CrateType.Inventory.Roulette"),
		
		INV_MENU("CrateType.Inventory.Menu"),
		
		INV_CSGO("CrateType.Inventory.CSGO"),

		INV_ENCLOSE("CrateType.Inventory.Enclose"),

		INV_DISCOVER("CrateType.Inventory.Discover"),
		
		GIVE_KEY("");
			
		/*BLOCK_MINEPLEX,
		
		BLOCK_CSGO;*/

	String prefix;

	CrateType(String prefix)
	{
		this.prefix = prefix;
	}

	public void setupFor(Crate crates)
	{
		CrateHead ch;
		switch(this)
		{
			case INV_ROULETTE:
				ch = new RouletteAnimation(null, crates);
				break;
			case INV_CSGO:
				//ch = new CSGOManager(null, crates);
				ch = new CSGOAnimation(null, crates);
				break;
			case INV_MENU:
				ch = new MenuAnimation(null, crates);
				break;
			case INV_ENCLOSE:
				ch = new EnclosementAnimation(null, crates);
				break;
			case INV_DISCOVER:
				ch = new DiscoverAnimation(null, crates);
				break;
			case GIVE_KEY:
			default:
				ch = new KeyCrate(crates);
		}
		crates.getCs().setCh(ch);
	}

	public int getUses()
	{
		int uses = 0;
		for(Crate crate : Crate.getLoadedCrates().values())
		{
			if(!crate.isMultiCrate())
			{
				if(crate.getCs().getCt().equals(this))
				{
					uses++;
				}
			}
		}

		return uses;
	}

	public String getPrefix()
	{
		return prefix;
	}

	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
	}
}
