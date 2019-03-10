package me.ztowne13.customcrates;

import java.util.Collection;

public enum SettingsValues
{
	STORE_DATA("store-data", String.class,
			new String[]{
					"Set how would you like to store",
					" player data."
	}),
	
	LOG_SUCCESSES("log-successes", String.class,
			new String[]{
					"Set how much would you like to",
					"be logged to console.",
					"Use EVERYTHING, FAILURES, or",
					"NOTHING."
	}),
	
	PUSHBACK("push-back", Boolean.class,
			new String[]{
					"Players will be pushed back if",
					"they fail to open a crate"
	}),
	
	AUTO_CLOSE("auto-close", Boolean.class,
			new String[]{
					"Crate animations should automatically",
					"close when done."
	}),
	
	REQUIRED_SLOTS("required-slots", Integer.class,
			new String[]{
					"Set amount of slots required",
					"to open a crate?"
	}),
	
	OPEN_CREATIVE("open-creative", Boolean.class,
			new String[]{
					"Players can open crates in",
					"creative mode."
	}),
	
	PLACE_CREATIVE("place-creative", Boolean.class,
			new String[]{
					"Players can place crates in",
					"creative mode."
	}),

	LUCKYCHEST_CREATIVE("luckychest-creative", Boolean.class,
			new String[]{
					"Players can find lucky chests",
					"in creative mode."
	}),

	REQUIRE_KEY_LORE("require-key-lore", Boolean.class,
			new String[]{
					"A key's lore is required to",
					"match the key."
	}),
		
	CA_FADE_IN("fade-in-time", Integer.class,
			new String[]{
					"Set how long would you like titles",
					"or subtitles to take to fade in.",
					"(In seconds)"
	}),
	
	CA_STAY("stay-time", Integer.class,
			new String[]{
					"Set how long would you like titles",
					"or subtitles to stay on the screen.",
					"(In seconds)"
	}),
	
	CA_FADE_OUT("fade-out-time", Integer.class,
			new String[]{
					"Set how long would you like titles",
					"or subtitles to take to fade out.",
					"In seconds"
	}),

	LUCKYCHEST_DESPAWN("luckychest-despawn-after", Integer.class,
			new String[]{
					"Set how many MINUTES luckycrates will",
					" take to despawn.",
					"Set to -1 to never despawn."
	}),

	REWARD_DISPLAY_ENABLED("enabled", Boolean.class,
			new String[]{
					"Crate will display their rewards",
					"when left clicked."
	}),
	
	REWARD_DISPLAY_NAME("inv-reward-display-name", String.class,
			new String[]{
					"Set the reward display inventory's."
	}),
	
	REWARD_ITEM_NAME("inv-reward-item-name", String.class,
			new String[]{
					"Set the reward display item's name."
	}),
	
	REWARD_ITEM_LORE("inv-reward-item-lore", Collection.class,
			new String[]{
					"Edit the reward display item's lore."
	}),
	
	EXPLODE_DYNAMIC("explosions-destroy-dynamic-crates", Boolean.class,
			new String[]{
					"Explosions will destroy dynamic crates."
	}),

	HOLOGRAM_OFFSET("hologram-offset", Double.class,
			new String[]{
					"Set the global hologram location offset."
	}),

	PLACE_EFFECT("place-effect", Boolean.class,
			new String[]{
					"A cool effect will be displayed when",
					"a crate is placed."
	}),

	PRIORITIZE_PHYSICAL_KEY("prioritize-physical-key", Boolean.class,
			new String[]{
					"Uses a physical key first if the",
					"player has a physical & virtaul key."
	}),

	VIRTUAL_CRATE_LORE("virtual-crate-lore", String.class,
			new String[]{
					"Change the lore to display the",
					"virtual crates."
	}),

	VIRTUAL_KEY_LORE("virtual-key-lore", String.class,
			new String[]{
					"Change the lore to display the",
					"virtual keys."
	}),

	CRATES_COMMAND_MULTICRATE("crates-command-multicrate", String.class,
			new String[]{
					"The name of the crate that",
					"is run when /crates is run."
	}),

	CRATES_COMMAND_NAME("crates-command-name", String.class,
			new String[]{
					"The name of the /crates virtual",
					"crates menu."
	}),

	MC_REWARD_DISPLAY_LEFTCLICK("mc-reward-display-leftclick", Boolean.class,
			new String[]{
					"MultiCrates will display rewards",
					"on left click instead of",
					"right click."
	}),

	NOTIFY_UPDATES("notify-updates", Boolean.class,
			new String[]{
					"The plugin will notify administrators",
					"when there is an update",
					"for the plugin."
	}),

	DEBUG("debug", Boolean.class,
			new String[]{
					"The plugin will log developer",
					"information to console."
	}),

	VIRTUAL_CRATE_KEYCOUNT("virtual-crate-keycount", Boolean.class,
			new String[]{
					"Multicrates will show the",
					"player's virtual keys",
					"amount."
	}),

	VIRTUAL_CRATE_CRATECOUNT("virtual-crate-cratecount", Boolean.class,
			new String[]{
					"Multicrates will show the",
					"player's virtual crates",
					"amount."
	}),

	REWARD_HOLOGRAM_LENGTH("reward-hologram-length", Integer.class,
			new String[]{
					"The duration reward holograms",
					"will display for in",
					"seconds."
	});
		
	String path;
	String[] descriptor;
	Object obj;
	
	SettingsValues(String path, Object obj, String[] descriptor)
	{
		this.path = path;
		this.descriptor = descriptor;
		this.obj = obj;
	}

	public static SettingsValues getByPath(String s)
	{
		for (SettingsValues sv : values())
		{
			if (sv.getPath().equalsIgnoreCase(s))
			{
				return sv;
			}
		}

		return null;
	}

	public Object getValue(CustomCrates cc)
	{
		return cc.getSettings().getConfigValues().get(path);
	}

	public void write(CustomCrates cc, Object obj)
	{

	}

	public String getPath()
	{
		return path;
	}

	public void setPath(String path)
	{
		this.path = path;
	}

	public String[] getDescriptor()
	{
		return descriptor;
	}

	public void setDescriptor(String[] descriptor)
	{
		this.descriptor = descriptor;
	}

	public Object getObj()
	{
		return obj;
	}

	public void setObj(Object obj)
	{
		this.obj = obj;
	}
}
